package org.jcvi.jillion.testutils.assembly.cas;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableIterator;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.jcvi.jillion.trace.sff.SffReadData;
import org.jcvi.jillion.trace.sff.SffReadHeader;
import org.jcvi.jillion.trace.sff.SffWriter;
import org.jcvi.jillion.trace.sff.SffWriterBuilder;

public final class SffRecordWriter implements RecordWriter{

	private static final Range NO_CLIP = Range.of(CoordinateSystem.RESIDUE_BASED, 0,0);
	private final File sffFile;
	private final SffWriter writer;
	private final int maxNumberOfRecordsToWrite;
	private final NucleotideSequence flowSequence;
	
	private int counter=0;
	private final int flowLength;
	
	private SffRecordWriter(Builder builder) throws IOException{
		
		this.maxNumberOfRecordsToWrite = builder.maxNumberOfRecordsToWrite;
		this.flowSequence = builder.flowSequence;
		this.sffFile = builder.sffFile;
		this.flowLength = builder.flowLength;
		
		this.writer = new SffWriterBuilder(sffFile,
				builder.keySequence,
				flowSequence
				).build();
	}
	
	@Override
	public void close() throws IOException {
		writer.close();
		
	}
	
	private Nucleotide convertToACGT(Nucleotide n){
		switch(n){
		case Adenine:
		case Cytosine:
		case Guanine:
				return n;
		default:
			return Nucleotide.Thymine;
		}
	}
	public void write(String id, NucleotideSequence seq, QualitySequence qualSeq) throws IOException{
		write(id, seq, qualSeq, NO_CLIP, NO_CLIP);
	}
	public void write(String id, NucleotideSequence seq, QualitySequence qualSeq, Range qualClip, Range adapterClip) throws IOException{
		if(!canWriteAnotherRecord()){
			throw new IllegalStateException("too many records to write");
		}
		counter++;
		
		SffReadHeader header = new SffReadHeader() {
			
			@Override
			public Range getQualityClip() {
				return qualClip;
			}
			
			@Override
			public int getNumberOfBases() {
				return (int) seq.getLength();
			}
			
			@Override
			public String getId() {
				return id;
			}
			
			@Override
			public Range getAdapterClip() {
				return adapterClip;
			}
		};
		
	
		
		int length = (int)seq.getLength();
		short[] flowgrams = new short[flowLength];
		
		byte[] flowIndexes = new byte[length];
		PeekableIterator<Nucleotide> flowIter = IteratorUtil.createPeekableIterator(flowSequence.iterator());
		PeekableIterator<Nucleotide> seqIter = IteratorUtil.createPeekableIterator(seq.iterator());
		
		int currentFlowgramIndex =0;
		int currentIndex=0;
		while(seqIter.hasNext()){
			//flowgrams is num consequtive bases
			//indexes is how many flows until that base
			Nucleotide s;
			int homopolymerCount=0;
			do{
				homopolymerCount++;
				s = convertToACGT(seqIter.next());

			}while(seqIter.hasNext() && s == convertToACGT(seqIter.peek()));
			
			//need to account for the ACGT flows between basecalls
			byte flowIndexCount=0;
			do{
				flowIndexCount++;
				currentFlowgramIndex ++;
			}while(flowIter.hasNext() && s != flowIter.next());
			
			flowgrams[currentFlowgramIndex-1] = (short)(homopolymerCount *100);
			
			flowIndexes[currentIndex] = flowIndexCount;
			currentIndex++;
		}
		SffReadData data = new SffReadData() {
			
			@Override
			public QualitySequence getQualitySequence() {
				return qualSeq;
			}
			
			@Override
			public NucleotideSequence getNucleotideSequence() {
				return seq;
			}
			
			@Override
			public short[] getFlowgramValues() {
				return Arrays.copyOf(flowgrams, flowLength);
			}
			
			@Override
			public byte[] getFlowIndexPerBase() {
				return Arrays.copyOf(flowIndexes, flowIndexes.length);
			}
		};
		writer.write(header, data);
	}
	@Override
	public void write(String id, NucleotideSequence seq) throws IOException {
		
		
		
		int length = (int)seq.getLength();
		QualitySequenceBuilder quals = new QualitySequenceBuilder(length);
		
		for(int i=0 ;i<length; i++){
			quals.append(RecordWriter.DEFAULT_QV);
		}
		QualitySequence qualSeq = quals.build();
		write(id, seq, qualSeq);
		
	}

	@Override
	public boolean canWriteAnotherRecord() {
		return counter<maxNumberOfRecordsToWrite;
	}

	@Override
	public File getFile() {
		return sffFile;
	}
	
	public static final class Builder{
		private static final NucleotideSequence KEY_SEQUENCE = NucleotideSequenceTestUtil.create("ACGT");
		
		private File sffFile;
		private int maxNumberOfRecordsToWrite = Integer.MAX_VALUE;
		private NucleotideSequence flowSequence=null;
		private NucleotideSequence keySequence = KEY_SEQUENCE;
		private int flowLength = 400;

		
		public Builder(File workingDir) throws IOException{
			
			sffFile = File.createTempFile("reads", ".sff", workingDir);
		}
		
		public Builder flowLength(int flowLength){
			if(flowLength %4 !=0){
				throw new IllegalArgumentException("flow length must be divisible by 4");
			}
			this.flowLength= flowLength;
			return this;
		}
		
		public Builder maxRecordsToWrite(int maxRecordsToWrite){
			if(maxRecordsToWrite <1){
				throw new IllegalArgumentException("max records can not < 1");
			}
			this.maxNumberOfRecordsToWrite= maxRecordsToWrite;
			return this;
		}
		
		public Builder flowSequence(NucleotideSequence seq){
			Objects.requireNonNull(seq);
			this.flowSequence = seq;
			this.keySequence = new NucleotideSequenceBuilder(seq, Range.ofLength(4)).build();
			return this;
		}
		
		public SffRecordWriter build() throws IOException{
			if(flowSequence ==null){
				NucleotideSequenceBuilder flowSequenceBuilder = new NucleotideSequenceBuilder(flowLength);
				int flowGroups = flowLength/4;
				for(int i=0; i< flowGroups; i++){
					flowSequenceBuilder.append(KEY_SEQUENCE);
				}
				flowSequence = flowSequenceBuilder.build();
			}
			
			return new SffRecordWriter(this);
			
		}
	}
}