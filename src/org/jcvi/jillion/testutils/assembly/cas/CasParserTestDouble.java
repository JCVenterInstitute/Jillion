package org.jcvi.jillion.testutils.assembly.cas;

import java.io.Closeable;
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
import java.util.Objects;
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
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;

/**
 * {@code CasParserTestDouble} is a {@link CasParser}
 * implementation that will allow
 * {@link CasFileVisitor}s to get visit messages from
 * arbitrary cas data without needing a real
 * cas file.
 * @see CasParserTestDouble.Builder
 * @author dkatzel
 *
 */
public final class CasParserTestDouble implements CasParser {
	
	private static final NucleotideSequence EMPTY_SEQ = NucleotideSequenceTestUtil.emptySeq();
	
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
	
	public interface ReadFileGenerator extends Closeable{
		void write(NucleotideSequence readSequence) throws IOException;
		
		List<String> getPaths();
	}
	
	private static final class DefaultReadIdGenerator implements ReadFileGenerator{
		private int readCounter=0;
		
		private final List<RecordWriter> readWriters = new ArrayList<>();
		private final Iterator<RecordWriter> recordWriterIterator;
		private RecordWriter currentRecordWriter;

		public DefaultReadIdGenerator(RecordWriter...recordWriters){
			for(RecordWriter recordWriter : recordWriters){
				if(recordWriter ==null){
					throw new NullPointerException("record writer can not be null");
				}
				readWriters.add(recordWriter);
			}
			if(readWriters.isEmpty()){
				throw new IllegalStateException("must have at least 1 RecordWriter");
			}
			
			recordWriterIterator = readWriters.iterator();
			currentRecordWriter = recordWriterIterator.next();
		}
		
		
		
		private void updateCurrentRecordWriterIfNeeded() {
			if(!currentRecordWriter.canWriteAnotherRecord()){				
				if(!recordWriterIterator.hasNext()){
					throw new IllegalStateException("no more record writers");
				}
				
				currentRecordWriter = recordWriterIterator.next();
			}
		}
		
		
		public String getNextId() {
			updateCurrentRecordWriterIfNeeded();
			return "read" + (readCounter++);
		}

		@Override
		public void close() throws IOException {
			List<String> paths = new ArrayList<>(readWriters.size());
			for(RecordWriter writer : readWriters){
				paths.add(writer.getFile().getName());
				IOUtil.closeAndIgnoreErrors(writer);
			}
		}
		@Override
		public List<String> getPaths(){
		
			List<String> paths = new ArrayList<>(readWriters.size());
			for(RecordWriter writer : readWriters){
				paths.add(writer.getFile().getName());		
			}
			return paths;
		}


		@Override
		public void write(NucleotideSequence readSequence) throws IOException {
			String id = getNextId();
			currentRecordWriter.write(id, readSequence);
			
		}
		
	}

	/**
	 * This class allows arbitrary cas data to be visited
	 * without needing a real underlying CLC .cas encoded file.
	 * This allows test code to make up cas alignments to fit
	 * the situation under test.  The builder will
	 * make all related reference and read files as well
	 * in case the visiting code needs to reference or parse them.
	 * 
	 * @author dkatzel
	 *
	 */
	public static class Builder{
		private final List<CasMatch> matches = new ArrayList<>();
		
		
		Map<String, Integer> refIndex = new HashMap<String, Integer>();
		Map<String, NucleotideSequence> references = new LinkedHashMap<>();
		
		private final File workingDir,referenceFile;
		
		private final ReadFileGenerator readFileGenerator;
		private int readCounter=0;
		private long residueCounter=0;
		private NucleotideFastaWriter referenceWriter;
		
		private CasFileInfo refFileInfo, readFileInfo;
		/**
		 * 
		 * @param workingDir
		 * @throws IOException
		 */
		public Builder(File workingDir) throws IOException{
			this(workingDir, new FastaRecordWriter(workingDir));
		}
		/**
		 * Create a new Builder instance which will
		 * use the provided working directory to write all 
		 * of its read and reference files.
		 * 
		 * @param workingDir the working directory to use 
		 * which will not only be the root of the reference and read files
		 * created but will also be returned by {@link CasParser#getWorkingDir()}.
		 * 
		 * @param recordWriters {@link RecordWriter}s to use to write
		 * the underlying read data.  The record writers will be used
		 * in the provided order.  The Builder to keep using a RecordWriter
		 * until it has reached its max records to be written as specified 
		 * by {@link RecordWriter#canWriteAnotherRecord()}, then it will
		 * move onto the next RecordWriter.  No {@link RecordWriter}s
		 * can be null.
		 * @throws NullPointerException if any recordWriters are null.
		 * @throws IllegalStateException if no RecordWriters are provided.
		 * @throws IOException if there is a problem creating the working directory
		 * or opening a file for writing in that directory.
		 */
		public Builder(File workingDir, RecordWriter...recordWriters) throws IOException{
			
			this(workingDir, new File(workingDir,"reference.fasta"),
					new DefaultReadIdGenerator(recordWriters));
			
		}
		
		
		/**
		 * Create a new Builder instance which will
		 * use the provided working directory to write all 
		 * of its read and reference files.
		 * 
		 * @param workingDir the working directory to use 
		 * which will not only be the root of the reference and read files
		 * created but will also be returned by {@link CasParser#getWorkingDir()}.
		 * 
		 * @param referenceFile the {@link File} to write the reference sequence to; can not be null.
		 * 
		 * @param recordWriters {@link RecordWriter}s to use to write
		 * the underlying read data.  The record writers will be used
		 * in the provided order.  The Builder to keep using a RecordWriter
		 * until it has reached its max records to be written as specified 
		 * by {@link RecordWriter#canWriteAnotherRecord()}, then it will
		 * move onto the next RecordWriter.  No {@link RecordWriter}s
		 * can be null.
		 * 
		 * @throws NullPointerException if any recordWriters or referenceFile are null.
		 * 
		 * @throws IllegalStateException if no RecordWriters are provided.
		 * 
		 * @throws IOException if there is a problem creating the working directory
		 * or opening a file for writing in that directory.
		 */
		public Builder(File workingDir, File referenceFile, ReadFileGenerator readIdGenerator) throws IOException{
			Objects.requireNonNull(referenceFile);
			Objects.requireNonNull(readIdGenerator);
			this.readFileGenerator = readIdGenerator;
			
			
			this.referenceFile = referenceFile;
			referenceWriter = new NucleotideFastaWriterBuilder(referenceFile)
										.build();
			this.workingDir = workingDir;
		
			
		}
		
		/**
		 * Add a reference with the given reference name
		 * and the provided full sequence.  This reference
		 * can now be referred to by its name 
		 * in {@link #forwardMatch(String, long)} and {@link #reverseMatch(String, long)}
		 * and the reference and sequence will be written to the reference fasta file.
		 * 
		 * @param name the reference name.
		 * @param sequence the nucleotide sequence as a String.
		 * 
		 * @return this
		 * 
		 * @throws NullPointerException if either parameter is null.
		 * @throws IllegalStateException if a reference with the same name
		 * already exists.
		 * @throws IllegalArgumentException if the given sequence is emtpy
		 * or has any invalid bases.
		 */
		public Builder addReference(String name, String sequence){
			Objects.requireNonNull(name);
			Objects.requireNonNull(sequence);
			if(references.containsKey(name)){
				throw new IllegalStateException("reference name already exists : " + name);
			}
			
			
			NucleotideSequence seq = new NucleotideSequenceBuilder(sequence).build();
			if(seq.getLength() ==0){
				throw new IllegalArgumentException("sequence must not be empty");
			}
			refIndex.put(name, refIndex.size());
			references.put(name, seq);
			
			try {
				referenceWriter.write(name, seq);
			} catch (IOException e) {
				throw new IllegalStateException("error writing reference sequence", e);
			}
			return this;
		}
		/**
		 * Create a new {@link AlignmentBuilder} to make an alignment
		 * of a read that matches the given reference in the forward direction.
		 * 
		 * @param refName the reference the read aligns to (this name 
		 * must already exist by a previous call to {@link #addReference(String, String)} ).
		 * @param ungappedStart the ungapped left most offset (0- based) that this
		 * read begins to align to the reference PROVIDING COVERAGE.  If the read starts off
		 * with trimmed off   ({@link CasAlignmentRegionType#INSERT} ) bases,
		 * then this offset value should be the value after those. 
		 * @return a new {@link AlignmentBuilder}
		 * 
		 * @throws IllegalArgumentException if refName is not known or if ungappedStart
		 * starts beyond the reference end.
		 */
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
		/**
		 * Create a new {@link AlignmentBuilder} to make an alignment
		 * of a read that matches the given reference in the reverse direction.
		 * 
		 * @param refName the reference the read aligns to (this name 
		 * must already exist by a previous call to {@link #addReference(String, String)} ).
		 * @param ungappedStart the ungapped left most offset (0- based) that this
		 * read begins to align to the reference PROVIDING COVERAGE.  If the read starts off
		 * with trimmed off   ({@link CasAlignmentRegionType#INSERT} ) bases,
		 * then this offset value should be the value after those. 
		 * @return a new {@link AlignmentBuilder}
		 * 
		 * @throws IllegalArgumentException if refName is not known or if ungappedStart
		 * starts beyond the reference end.
		 */
		public AlignmentBuilder reverseMatch(String refName, long ungappedStart){
			validateReadParameters(refName, ungappedStart);
			return new AlignmentBuilder(this,refName, ungappedStart, Direction.REVERSE);
		}
		private Builder match(String refName, long ungappedStart, Direction dir, CasAlignmentRegionImpl[] alignmentRegions){
			Integer index = refIndex.get(refName);

			readCounter++;
			
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
					readFileGenerator.write(fullLengthReadSequence);
				} catch (IOException e) {
					throw new IllegalStateException("error writing fasta read sequence", e);
				}
				
				alignment = alignmentBuilder.build();
			}else{
				alignment =null;
				//need to write empty sequence
				try {
					readFileGenerator.write(EMPTY_SEQ);
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

		
		
		private boolean matchReported(CasAlignmentRegion[] alignmentRegions) {
			for(CasAlignmentRegion r : alignmentRegions){
				if(r.getType() !=CasAlignmentRegionType.INSERT){
					return true;
				}
			}
			return false;
		}

	
		

		
		/**
		 * Write all the underlying read and reference files
		 * out and create a new {@link CasParser} instance
		 * that will make the visit calls that conform to the 
		 * alignment data previously given.
		 * @return a new {@link CasParser} instance; will never be null.
		 * 
		 * @throws IOException if there is a problem writing any of the files.
		 * 
		 * @throws IllegalStateException if no alignments were written.
		 */
		public CasParserTestDouble build() throws IOException{
			if(readCounter ==0){
				//have to have some reads!
				throw new IllegalStateException("must have at least one read");
			}
			List<String> paths = readFileGenerator.getPaths();
			
			IOUtil.closeAndIgnoreErrors(readFileGenerator);
			
			try {
				referenceWriter.close();
			} catch (IOException e) {
				throw new IllegalStateException("error closing referece fasta writer" ,e);
			}
			refFileInfo = new CasFileInfoImpl(referenceFile.getName(), references);
			
			readFileInfo = new CasFileInfoImpl(paths, readCounter, residueCounter);
			return new CasParserTestDouble(this);
		}
		/**
		 * Write a Read that did not align to any of the references.
		 * By default, this will write a new read with 10 Ns.
		 * @return this
		 */
		public Builder unMatched() {
			readCounter++;
			//make a read of 10 Ns
			char[] ns = new char[10];
			Arrays.fill(ns ,'N');
			NucleotideSequence seq = new NucleotideSequenceBuilder(ns).build();
			try{
				readFileGenerator.write(seq);
			} catch (IOException e) {
				throw new IllegalStateException("error writing fasta read sequence", e);
			}
			
			matches.add(NOT_MATCHED);
			
			return this;
			
		}
	}
	/**
	 * Class that builds an alignment
	 * for a single read to a reference.
	 * @author dkatzel
	 *
	 */
	public static final class AlignmentBuilder{
		

		private final String refName;
		private final long ungappedStart;
		private final Direction dir;
		private final Builder builder;
		
		
		private final List<CasAlignmentRegionImpl> regions = new ArrayList<>();
		
		private AlignmentBuilder(Builder builder, String refName, long ungappedStart,
				Direction dir) {
			this.builder = builder;
			this.refName = refName;
			this.ungappedStart = ungappedStart;
			this.dir = dir;
		}
		/**
		 * Add a new {@link CasAlignmentRegion} of the specified
		 * length.  The read's ungapped sequence of the will default
		 * to either the reference sequence if the type is a 
		 * {@link CasAlignmentRegionType#MATCH_MISMATCH} or
		 * Ns for {@link CasAlignmentRegionType#INSERT}s.
		 * @param type the alignment type; can not be null.
		 * @param length the length; must be  >= 1.
		 * @return this
		 * 
		 * @throws NullPointerException if type is null.
		 * @throws IllegalArgumentException if length < 1
		 */
		public AlignmentBuilder addAlignmentRegion(CasAlignmentRegionType type, int length){
			regions.add(new CasAlignmentRegionImpl(type,length));
			return this;
		}
		/**
		 * Add a new {@link CasAlignmentRegion} of the specified
		 * sequence. The sequence provided is in the ALIGNED
		 * orientation.  So a reverse read will have the 
		 * final full sequence reverse complemented 
		 * when it is written out to the read file.
		 * 
		 * The decision to use the aligned orientation
		 * is to make tests more clear by having
		 * the reverse reads easily understandable
		 * from an alignment context without having
		 * to mentally reverse complement when reading code.
		 * 
		 * @param type the alignment type; can not be null.
		 * @param length the length; must be  >= 1.
		 * @return this
		 * 
		 * @throws NullPointerException if type is null.
		 * @throws IllegalArgumentException if length < 1
		 */
		public AlignmentBuilder addAlignmentRegion(CasAlignmentRegionType type, String sequence){
			regions.add(new CasAlignmentRegionImpl(type,sequence));
			return this;
		}
		/**
		 * This read's alignment is complete.
		 * @return the parent CasParserTestDouble.Builder instance
		 * that created this object.
		 */
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
			Objects.requireNonNull(type);
			if(length <1){
				throw new IllegalArgumentException("length must be positive");
			}
			this.type = type;
			this.length = length;
			this.seq = null;
		}
		
		private CasAlignmentRegionImpl(CasAlignmentRegionType type, String seq){
			Objects.requireNonNull(type);
			this.type = type;
			this.seq = new NucleotideSequenceBuilder(seq).build();
			this.length = (int)this.seq.getLength();
			if(length <1){
				throw new IllegalArgumentException("length must be positive");
			}
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
