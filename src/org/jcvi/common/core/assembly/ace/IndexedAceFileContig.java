package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore.ReadVisitorBuilder;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.MapUtil;
import org.jcvi.common.core.util.impl.Caches;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code IndexedAceFileContig} is an {@link AceContig}
 * that doesn't store all the data of this contig
 * in memory at any one time.  Instead of storing
 * {@link AceAssembledRead}s, only a map of offset ranges
 * into the ace file are stored.  Whenever a read
 * is requested, the file is re-opened, and 
 * <strong>just the lines for that one read</strong>
 * are re-parsed.  To improve performance,
 * an LRU cache is used to store the most recent fetched
 * reads.  This helps performance if the same reads
 * are requested over and over again in close proximity
 * (like  iterating through regions in a coverage map).
 * Currently the cache size is 
 * {@code max depth of coverage * 2}.
 * @author dkatzel
 *
 */
final class IndexedAceFileContig implements AceContig{

	private final String contigId;
	private final Map<String, AlignedReadInfo> readInfoMap;
	private final Map<String,Range> readOffsetRanges;
	private final boolean isComplimented;
	private final NucleotideSequence consensus;
	private final File aceFile;
	private final long contigStartFileOffset;
	private final Map<String, AceAssembledRead> cachedReads;
	private final boolean aceFileHasSortedReads;
	
	private IndexedAceFileContig(String contigId,
			Map<String, AlignedReadInfo> readInfoMap,
			Map<String,Range> readOffsetRanges, boolean isComplimented,
			NucleotideSequence consensus, File aceFile,
			long contigStartFileOffset,
			int maxCoverage,
			boolean aceFileHasSortedReads) {
		this.contigId = contigId;
		this.readInfoMap = readInfoMap;
		this.readOffsetRanges = readOffsetRanges;
		this.isComplimented = isComplimented;
		this.consensus = consensus;
		this.aceFile = aceFile;
		this.contigStartFileOffset = contigStartFileOffset;
		this.cachedReads = Caches.createSoftReferencedValueLRUCache(maxCoverage*2);
		this.aceFileHasSortedReads = aceFileHasSortedReads;
	}

	@Override
	public String getId() {
		return contigId;
	}

	@Override
	public long getNumberOfReads() {
		return readInfoMap.size();
	}

	@Override
	public StreamingIterator<AceAssembledRead> getReadIterator() {
		if(aceFileHasSortedReads){
			return createIteratorOverFile();
		}
		return new OutOfOrderReadIterator();
	}

	private StreamingIterator<AceAssembledRead> createIteratorOverFile() {
		InputStream in = null;
		try{
			in = new FileInputStream(aceFile);
			//seek to start of contig
			IOUtil.blockingSkip(in, contigStartFileOffset);
			//start parsing
			IndexedReadIterator iter = new IndexedReadIterator(in, consensus, readInfoMap);
			iter.start();
			return iter;
		} catch (IOException e) {
			throw new IllegalStateException("error iterating over reads",e);
		}
	}

	@Override
	public NucleotideSequence getConsensusSequence() {
		return consensus;
	}

	@Override
	public synchronized AceAssembledRead getRead(String id) {
		if(!containsRead(id)){
			return null;
		}
		AceAssembledRead cachedRead =cachedReads.get(id);
		if(cachedRead !=null){
			return cachedRead;
		}
		InputStream in = null;
		try{
			Range offsetRange = readOffsetRanges.get(id);
			in = IOUtil.createInputStreamFromFile(aceFile, (int)offsetRange.getBegin(), (int)offsetRange.getLength());
			ReadVisitorBuilder builder = new ReadVisitorBuilder(consensus);
			builder.visitBeginContig(contigId, 0, 0, 0, isComplimented);
			AlignedReadInfo alignmentInfo = readInfoMap.get(id);
			builder.visitAssembledFromLine(id, alignmentInfo.getDirection(), alignmentInfo.getStartOffset());
			
			AceFileParser.parse(in, builder);
			AceAssembledRead read= builder.build();
			cachedReads.put(id, read);
			return read;
			
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("ace file no longer exists", e);
		} catch (IOException e) {
			
			throw new IllegalStateException("error parsing ace file", e);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}

	@Override
	public boolean containsRead(String readId) {
		return readInfoMap.containsKey(readId);
	}

	@Override
	public boolean isComplemented() {
		return isComplimented;
	}
	
	/**
	 * To maintain the contract that the read ids
	 * are always in consed preferred sorted order,
	 * we must fetch the reads in the order
	 * they should be in if the ace file
	 * does not order the reads correctly.
	 * 
	 * This should be rare and only occurs with ace files
	 * that are created outside of this library, consed or phrap.
	 * (perhaps others follow this pattern too, but 454 as of 2009 did not).
	 * @author dkatzel
	 *
	 */
	class OutOfOrderReadIterator implements StreamingIterator<AceAssembledRead>{
		private final StreamingIterator<String> sortedIdIterator;
		
		public OutOfOrderReadIterator(){
			sortedIdIterator = IteratorUtil.createStreamingIterator(readOffsetRanges.keySet().iterator());
		}

		@Override
		public boolean hasNext() {
			return sortedIdIterator.hasNext();
		}

		@Override
		public void close() throws IOException {
			sortedIdIterator.close();
			
		}

		@Override
		public AceAssembledRead next() {
			return getRead(sortedIdIterator.next());
		}

		@Override
		public void remove() {
			sortedIdIterator.remove();
			
		}
		
	}
	
	
	
	static final class IndexedContigVisitorBuilder extends AbstractAceFileVisitor implements Builder<AceContig>{
		
		private long currentOffset;
		private Map<String,Range> readFileOffsetRanges;
		private final File aceFile;
		private String currentLine;
		private String contigId;
		private boolean isComplimented;
		private final long contigStartOffset;
		private Map<String,Range> coverageRanges;
		private long currentReadStart;
		private NucleotideSequence contigConsensusSequence;
		Map<String, AlignedReadInfo> alignedInfoMapCopy;
		
		public IndexedContigVisitorBuilder(long startOffset, File aceFile) {
			this.currentOffset = startOffset;
			this.aceFile = aceFile;
			this.contigStartOffset = startOffset;
		}

		@Override
		public synchronized void visitLine(String line) {
			currentLine = line;
			currentOffset+=currentLine.length();
		}


		@Override
		protected void visitNewContig(String contigId,
				NucleotideSequence consensus, int numberOfBases,
				int numberOfReads, boolean isComplemented) {
			contigConsensusSequence = consensus;
			
		}

		@Override
		public synchronized void visitReadHeader(String readId, int gappedLength) {			
			super.visitReadHeader(readId, gappedLength);
			currentReadStart = currentOffset - currentLine.length();
		}

		@Override
		protected void visitAceRead(String readId,
				NucleotideSequence validBasecalls, int offset, Direction dir,
				Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
			
			readFileOffsetRanges.put(readId, Range.of(currentReadStart, currentOffset-1));
			coverageRanges.put(readId, new Range.Builder(validBasecalls.getLength()).shift(offset).build());
			
			currentReadStart =currentOffset+1;
		}

		@Override
		public AceContig build() {
			
			CoverageMap<Range> coverageMap = CoverageMapFactory.create(coverageRanges.values());
			int maxCoverage = getMaxCoverage(coverageMap);
			
			
			SortedMap<String,Range> sortedFileOffsetRanges = new TreeMap<String,Range>(new ConsedOrderedReads(coverageRanges));
			sortedFileOffsetRanges.putAll(readFileOffsetRanges);
			
			
			boolean aceFileHasSortedReads = idsInSameOrder(readFileOffsetRanges,sortedFileOffsetRanges);
			
			if(aceFileHasSortedReads){
				return new IndexedAceFileContig(contigId, alignedInfoMapCopy, readFileOffsetRanges, isComplimented, 
						contigConsensusSequence, aceFile, 
						contigStartOffset, maxCoverage,aceFileHasSortedReads);
			}
			return new IndexedAceFileContig(contigId, alignedInfoMapCopy, sortedFileOffsetRanges, isComplimented, 
					contigConsensusSequence, aceFile, 
					contigStartOffset, maxCoverage,aceFileHasSortedReads);
		}

		private boolean idsInSameOrder(Map<String, Range> actual,
				SortedMap<String, Range> sortedIds) {
			Iterator<String> actualIter = actual.keySet().iterator();
			Iterator<String> sortedIter = sortedIds.keySet().iterator();
			while(sortedIter.hasNext()){
				if(!actualIter.next().equals(sortedIter.next())){
					return false;
				}
			}
			return true;
		}

		private int getMaxCoverage(CoverageMap<?> coverageMap){
			int currentMax = 0;
			for(CoverageRegion<?> r : coverageMap){
				Math.max(currentMax, r.getCoverageDepth());
			}
			return currentMax;
		}

		@Override
		public boolean shouldVisitContig(String contigId,
				int numberOfBases, int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplimented) {
			this.contigId =contigId;
			int mapCapacity = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads);
			readFileOffsetRanges = new LinkedHashMap<String, Range>(mapCapacity);
			coverageRanges = new HashMap<String, Range>(mapCapacity);
			isComplimented = reverseComplimented;
			return true;
		}


		


		


		@Override
		public EndContigReturnCode visitEndOfContig() {
			super.visitEndOfContig();
			alignedInfoMapCopy = getAlignedInfoMap();
			return EndContigReturnCode.STOP_PARSING;
		}

		
		
	}
	
	private static class ConsedOrderedReads implements Comparator<String>{

		private final Map<String,Range> gappedCoverageRanges;

		public ConsedOrderedReads(Map<String, Range> gappedCoverageRanges) {
			this.gappedCoverageRanges = gappedCoverageRanges;
		}

		@Override
		public int compare(String o1, String o2) {
			Range o1Range = gappedCoverageRanges.get(o1);
			Range o2Range = gappedCoverageRanges.get(o2);
			if(o1Range==null){
				throw new NullPointerException("no range for "+o1);
			}
			if(o2Range==null){
				throw new NullPointerException("no range for "+o2);
			}
			int rangeCmp = Range.Comparators.ARRIVAL.compare(o1Range, o2Range);
			if(rangeCmp !=0){
				return rangeCmp;
			}
			return o1.compareTo(o2);
		}
		
		
		
	}
	
	 private static final class IndexedReadIterator extends AbstractBlockingCloseableIterator<AceAssembledRead>{

	    	private final NucleotideSequence consensus;
	    	private final Map<String, AlignedReadInfo> readInfoMap;
	    	private final InputStream in;
	    	
			public IndexedReadIterator(InputStream in, NucleotideSequence consensus, final Map<String, AlignedReadInfo> readInfoMap) {
				this.consensus = consensus;
				this.readInfoMap = readInfoMap;
				this.in = in;
			}

			@Override
			protected void backgroundThreadRunMethod()
					throws RuntimeException {
				AbstractAceFileVisitor visitor = new AbstractAceFileVisitor() {
					{
						this.setAlignedInfoMap(readInfoMap);
					}
					@Override
					protected void visitNewContig(String contigId,
							NucleotideSequence consensus, int numberOfBases, int numberOfReads,
							boolean isComplimented) {
						//no-op
						
					}
					
					@Override
					public EndContigReturnCode visitEndOfContig() {
						return EndContigReturnCode.STOP_PARSING;
					}

					@Override
					protected void visitAceRead(String readId,
							NucleotideSequence validBasecalls, int offset, Direction dir,
							Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
						AceAssembledRead read =DefaultAceAssembledRead.createBuilder(IndexedReadIterator.this.consensus, readId, validBasecalls, offset, dir, validRange, phdInfo, ungappedFullLength)
								.build();
						
						blockingPut(read);
					}
				};
				try{
					AceFileParser.parse(in, visitor);
				} catch (IOException e) {
					throw new IllegalStateException("error parsing reads from contig in ace file",e);
				}finally{
					IOUtil.closeAndIgnoreErrors(in);
				}
				
			}
			
		}
}
