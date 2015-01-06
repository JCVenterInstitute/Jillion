package org.jcvi.jillion.testutils.assembly.cas;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.assembly.clc.cas.CasAlignment;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.CasFileInfo;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor.CasVisitorCallback;
import org.jcvi.jillion.assembly.clc.cas.CasMatch;
import org.jcvi.jillion.assembly.clc.cas.CasMatchVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasParser;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriterBuilder;


public class CasParserTestDouble implements CasParser {
	
	private static final NucleotideSequence EMPTY_SEQ = new NucleotideSequenceBuilder().build();
	
	private static final CasMatch NOT_MATCHED = new CasMatch() {
		
		@Override
		public boolean readIsPartOfAPair() {
			return false;
		}
		
		@Override
		public boolean readHasMutlipleMatches() {
			return false;
		}
		
		@Override
		public boolean matchReported() {
			return false;
		}
		
		@Override
		public boolean hasMultipleAlignments() {
			return false;
		}
		
		@Override
		public Range getTrimRange() {
			return null;
		}
		
		@Override
		public int getScore() {
			return 0;
		}
		
		@Override
		public long getNumberOfReportedAlignments() {
			return 0;
		}
		
		@Override
		public long getNumberOfMatches() {
			return 0;
		}
		
		@Override
		public CasAlignment getChosenAlignment() {
			return null;
		}
	};
	
	
	private final List<CasMatch> matches;
	private final File workingDir;
	
	private final CasFileInfo referenceFileInfo, readFileInfo;
	private final long numReferences, numReads;
	
	private CasParserTestDouble(Builder builder){
		this.matches = builder.matches;

		this.workingDir = builder.workingDir;
		
		this.referenceFileInfo = builder.refFileInfo;
		this.readFileInfo = builder.readFileInfo;
		
		this.numReads = builder.readCounter;
		this.numReferences = builder.references.size();
	}
	
	@Override
	public boolean canParse() {
		return true;
	}

	@Override
	public void parse(CasFileVisitor visitor) throws IOException {
		visitor.visitAssemblyProgramInfo("CasParserTestDouble", "1.0", "");
		visitor.visitMetaData(numReferences, numReads);
		
		visitor.visitNumberOfReferenceFiles(1);		
		visitor.visitNumberOfReadFiles(1);
		
		visitor.visitReferenceFileInfo(referenceFileInfo);
		visitor.visitReadFileInfo(readFileInfo);
		
		AtomicBoolean keepParsing = new AtomicBoolean(true);
		
		CasVisitorCallback callback = new CasVisitorCallback() {
			
			@Override
			public void haltParsing() {
				keepParsing.set(false);
			}
			
			@Override
			public boolean canCreateMemento() {
				return false;
			}

			@Override
			public CasVisitorMemento createMemento() {
				throw new UnsupportedOperationException("mementos not supported");
			}
		};
	

		handleMatches(visitor.visitMatches(callback), keepParsing);
		if(keepParsing.get()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}

	private void handleMatches(CasMatchVisitor visitor, AtomicBoolean keepParsing) {
		if(visitor ==null){
			return;
		}
		for(CasMatch match : matches){
			if(keepParsing.get()){
				visitor.visitMatch(match);
			}
		}
		if(keepParsing.get()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}

	@Override
	public File getWorkingDir() {
		return workingDir;
	}

	public static class Builder{
		private final List<CasMatch> matches = new ArrayList<>();
		
		
		Map<String, Integer> refIndex = new HashMap<String, Integer>();
		Map<String, NucleotideSequence> references = new LinkedHashMap<>();
		
		private final File workingDir,referenceFile;
		
		private int readCounter=0;
		private long residueCounter=0;
		private NucleotideFastaRecordWriter referenceWriter;
		private final List<RecordWriter> readWriters = new ArrayList<>();
		private final Iterator<RecordWriter> recordWriterIterator;
		private RecordWriter currentRecordWriter;
		
		private CasFileInfo refFileInfo, readFileInfo;
		
		public Builder(File workingDir) throws IOException{
			this(workingDir, new FastaRecordWriter(workingDir));
		}
		
		public Builder(File workingDir, RecordWriter...recordWriters) throws IOException{
			
			for(RecordWriter recordWriter : recordWriters){
				if(recordWriter ==null){
					throw new NullPointerException("record writer can not be null");
				}
				readWriters.add(recordWriter);
			}
			if(readWriters.isEmpty()){
				throw new IllegalStateException("must have at least 1 RecordWriter");
			}
			referenceFile =  new File(workingDir,"reference.fasta");
			referenceWriter = new NucleotideFastaRecordWriterBuilder(referenceFile)
										.build();
			this.workingDir = workingDir;
			recordWriterIterator = readWriters.iterator();
			currentRecordWriter = recordWriterIterator.next();
			
		}
		
		public Builder addReference(String name, String sequence){
			refIndex.put(name, refIndex.size());
			NucleotideSequence seq = new NucleotideSequenceBuilder(sequence).build();
			references.put(name, seq);
			
			try {
				referenceWriter.write(name, seq);
			} catch (IOException e) {
				throw new IllegalStateException("error writing reference sequence", e);
			}
			return this;
		}
		public AlignmentBuilder forwardMatch(String refName, long ungappedStart){
			
			validateReadParameters(refName, ungappedStart);
			return new AlignmentBuilder(this,refName, ungappedStart, Direction.FORWARD);
		}

		private void validateReadParameters(String refName, long ungappedStart) {
			NucleotideSequence seq = references.get(refName);
			if(seq ==null){
				throw new IllegalArgumentException("unknown reference : " + refName);
			}
			if(ungappedStart > seq.getUngappedLength()){
				throw new IllegalArgumentException("read starts beyond reference " + ungappedStart);
			}
		}
		public AlignmentBuilder reverseMatch(String refName, long ungappedStart){
			validateReadParameters(refName, ungappedStart);
			return new AlignmentBuilder(this,refName, ungappedStart, Direction.REVERSE);
		}
		private Builder match(String refName, long ungappedStart, Direction dir, CasAlignmentRegionImpl[] alignmentRegions){
			Integer index = refIndex.get(refName);
			
			updateCurrentRecordWriterIfNeeded();
			
			boolean matchReported = matchReported(alignmentRegions);
			CasAlignment alignment;
			if(matchReported){
				DefaultCasAlignment.Builder alignmentBuilder = new DefaultCasAlignment.Builder(index.longValue(), ungappedStart, dir);
				
				for(CasAlignmentRegion alignmentRegion : alignmentRegions){
					alignmentBuilder.addRegion(alignmentRegion);
				}
			
				NucleotideSequence fullLengthReadSequence = computeFullRangeUngappedReadSequence2(refName, ungappedStart, dir, alignmentRegions);
				residueCounter += fullLengthReadSequence.getLength();
				try {
					currentRecordWriter.write(getNextReadId(), fullLengthReadSequence);
				} catch (IOException e) {
					throw new IllegalStateException("error writing fasta read sequence", e);
				}
				
				alignment = alignmentBuilder.build();
			}else{
				alignment =null;
				//need to write empty sequence
				try {
					currentRecordWriter.write(getNextReadId(), EMPTY_SEQ);
				} catch (IOException e) {
					throw new IllegalStateException("error writing fasta read sequence", e);
				}
			}
			matches.add(new CasMatch() {
				
				@Override
				public boolean readIsPartOfAPair() {
					return false;
				}
				
				@Override
				public boolean readHasMutlipleMatches() {
					return false;
				}
				
				@Override
				public boolean matchReported() {
					return matchReported;
				}
				
				@Override
				public boolean hasMultipleAlignments() {
					return false;
				}
				
				@Override
				public Range getTrimRange() {
					return null;
				}
				
				@Override
				public int getScore() {
					return 0;
				}
				
				@Override
				public long getNumberOfReportedAlignments() {
					return 1;
				}
				
				@Override
				public long getNumberOfMatches() {
					return 1;
				}
				
				@Override
				public CasAlignment getChosenAlignment() {
					return alignment;
				}
			});
			
			return this;
		}

		private void updateCurrentRecordWriterIfNeeded() {
			if(!currentRecordWriter.canWriteAnotherRecord()){				
				if(!recordWriterIterator.hasNext()){
					throw new IllegalStateException("no more record writers");
				}
				
				currentRecordWriter = recordWriterIterator.next();
			}
		}

		private NucleotideSequence computeFullRangeUngappedReadSequence2(
				String refName, long ungappedStart, Direction dir,
				CasAlignmentRegionImpl[] alignmentRegions) {
			NucleotideSequence refSeq = references.get(refName);
			
			NucleotideSequenceBuilder fullLengthBuilder = new NucleotideSequenceBuilder();
			Iterator<CasAlignmentRegionImpl> alignmentIter = new ArrayIterator<>(alignmentRegions);
			boolean inValidRange = false;
			long currentUngappedRefOffset = ungappedStart;
			
			while(alignmentIter.hasNext()){
				CasAlignmentRegionImpl region = alignmentIter.next();
				CasAlignmentRegionType type = region.getType();
				if(!inValidRange){
					if(type ==CasAlignmentRegionType.INSERT){
						//outside of validrange
						//don't update currentoffset
						if(region.seq ==null){
							//use N's
							char[] ns = new char[(int)region.getLength()];
							Arrays.fill(ns, 'N');
							fullLengthBuilder.append(ns);
						}else{
							fullLengthBuilder.append(region.seq);
						}
					}else{
						inValidRange =true;
					}
				}
				if(inValidRange){
					//already iterated so don't need to again
					switch(type){
						case INSERT: if(region.seq ==null){
										//use N's
										char[] ns = new char[(int)region.getLength()];
										Arrays.fill(ns, 'N');
										fullLengthBuilder.append(ns);
										
									}else{
										fullLengthBuilder.append(region.seq);
									}
									//don't increment refOffset
									break;
						case DELETION : 
									//we would normally add gaps here
									//but since this is the full range ungapped
									//sequence we don't use gaps
									//but increment ref offset
									
									currentUngappedRefOffset+=region.getLength();
									break;
						case MATCH_MISMATCH :
									if(region.seq==null){
										//use ref seq
										Range refRange = new Range.Builder(region.getLength())
																.shift(currentUngappedRefOffset)
																.build();
										Iterator<Nucleotide> refIter = refSeq.iterator(refRange);
										while(refIter.hasNext()){
											fullLengthBuilder.append(refIter.next());
										}										
									}else{
										fullLengthBuilder.append(region.seq);
									}
									currentUngappedRefOffset+=region.getLength();
									break;
						default : //no-op
					}
					
				}
			}
			if(dir ==Direction.REVERSE){
				fullLengthBuilder.reverseComplement();
			}
			return fullLengthBuilder.build();
		}

		private String getNextReadId() {
			return "read" + (readCounter++);
		}
		
		private boolean matchReported(CasAlignmentRegion[] alignmentRegions) {
			for(CasAlignmentRegion r : alignmentRegions){
				if(r.getType() !=CasAlignmentRegionType.INSERT){
					return true;
				}
			}
			return false;
		}

	
		

		

		public CasParserTestDouble build() throws IOException{
			if(readCounter ==0){
				//have to have some reads!
				throw new IllegalStateException("must have at least one read");
			}
			List<String> paths = new ArrayList<>(readWriters.size());
			for(RecordWriter writer : readWriters){
				paths.add(writer.getFile().getName());
				IOUtil.closeAndIgnoreErrors(writer);
			}
			try {
				referenceWriter.close();
			} catch (IOException e) {
				throw new IllegalStateException("error closing referece fasta writer" ,e);
			}
			refFileInfo = new CasFileInfoImpl(referenceFile.getName(), references);
			
			readFileInfo = new CasFileInfoImpl(paths, readCounter, residueCounter);
			return new CasParserTestDouble(this);
		}

		public Builder unMatched() {
			updateCurrentRecordWriterIfNeeded();
			//make a read of 10 Ns
			char[] ns = new char[10];
			Arrays.fill(ns ,'N');
			NucleotideSequence seq = new NucleotideSequenceBuilder(ns).build();
			try{
				currentRecordWriter.write(getNextReadId(), seq);
			} catch (IOException e) {
				throw new IllegalStateException("error writing fasta read sequence", e);
			}
			
			matches.add(NOT_MATCHED);
			
			return this;
			
		}
	}
	
	public static final class AlignmentBuilder{
		

		private final String refName;
		private final long ungappedStart;
		private final Direction dir;
		private final Builder builder;
		
		
		private List<CasAlignmentRegionImpl> regions = new ArrayList<>();
		
		private AlignmentBuilder(Builder builder, String refName, long ungappedStart,
				Direction dir) {
			this.builder = builder;
			this.refName = refName;
			this.ungappedStart = ungappedStart;
			this.dir = dir;
		}
		
		public AlignmentBuilder addAlignmentRegion(CasAlignmentRegionType type, int length){
			regions.add(new CasAlignmentRegionImpl(type,length));
			return this;
		}
		
		public AlignmentBuilder addAlignmentRegion(CasAlignmentRegionType type, String sequence){
			regions.add(new CasAlignmentRegionImpl(type,sequence));
			return this;
		}

		public CasParserTestDouble.Builder build(){
			return builder.match(refName, ungappedStart, dir, regions.toArray(new CasAlignmentRegionImpl[regions.size()]));
		}
	}
	
	private static final class CasAlignmentRegionImpl implements
			CasAlignmentRegion {
		private final CasAlignmentRegionType type;
		private final int length;
		private final NucleotideSequence seq;
		
		private CasAlignmentRegionImpl(CasAlignmentRegionType type, int length) {
			this.type = type;
			this.length = length;
			this.seq = null;
		}
		private CasAlignmentRegionImpl(CasAlignmentRegionType type, String seq){
			this.type = type;
			this.seq = new NucleotideSequenceBuilder(seq).build();
			this.length = (int)this.seq.getLength();
		}

		@Override
		public CasAlignmentRegionType getType() {
			return type;
		}

		@Override
		public long getLength() {
			return length;
		}
	}
	
	private static final class CasFileInfoImpl implements CasFileInfo{
		private final long numSeq;
		private final BigInteger residues;
		private final List<String> path;
		
		public CasFileInfoImpl(List<String> path, int numReads, long numResidues){
			this.path = path;
			this.numSeq = numReads;
			this.residues = BigInteger.valueOf(numResidues);
		}
		public CasFileInfoImpl(String path, Map<String, NucleotideSequence> seqMap){
			this.path = Collections.singletonList(path);;
			this.numSeq = seqMap.size();
			
			long sum =0;
			for(NucleotideSequence s : seqMap.values()){
				sum += s.getLength();
			}
			residues= BigInteger.valueOf(sum);
		}
		@Override
		public long getNumberOfSequences() {
			return numSeq;
		}

		@Override
		public BigInteger getNumberOfResidues() {
			return residues;
		}

		@Override
		public List<String> getFileNames() {
			return path;
		}
		
	}
}
