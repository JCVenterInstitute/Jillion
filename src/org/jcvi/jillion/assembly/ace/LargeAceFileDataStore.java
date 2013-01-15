package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jcvi.jillion.assembly.ace.consed.ConsedUtil;
import org.jcvi.jillion.assembly.ace.consed.ConsedUtil.ClipPointsType;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.internal.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code LargeAceFileDataStore} is an {@link AceFileContigDataStore}
 * implementation that doesn't store any contig or 
 * read information in memory.
 * This means that each {@link #get(String)} or {@link #contains(String)}
 * requires re-parsing the ace file which can take some time.
 * Other methods such as {@link #getNumberOfRecords()} are lazy-loaded
 * and are only parsed the first time they are asked for.
 * <p/>
 * Since each method call involves re-parsing the ace file,
 * that file must not be modified or moved during the
 * entire lifetime of the instance.
 * It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.common.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 */
final class LargeAceFileDataStore implements AceFileContigDataStore{

	private final File aceFile;
	private Long numberOfContigs = null;
	private Long totalNumberOfReads =null;
	
	private List<WholeAssemblyAceTag> wholeAssemblyTags =null;
    private List<ConsensusAceTag> consensusTags= null;
    private List<ReadAceTag> readTags=null;
    
	private final DataStoreFilter contigIdFilter;
	 private volatile boolean isClosed;
	/**
	 * Create a new instance of {@link LargeAceFileDataStore}.
	 * @param aceFile the ace file to create an {@link AceFileContigDataStore}
	 * from. (can not be null and must exist)
	 * @return a new {@link AceFileContigDataStore}; 
	 * will never be null.
	 * @throws FileNotFoundException if the ace file does not exist.
	 * @throws NullPointerException if aceFile is null.
	 */
	public static AceFileContigDataStore create(File aceFile) throws FileNotFoundException{
		return new LargeAceFileDataStore(aceFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new instance of {@link LargeAceFileDataStore}
	 * with only some of the contigs from the given ace file.
	 * Any contigs excluded by the given {@link DataStoreFilter}
	 * will be completely ignored during calls to {@link #getNumberOfRecords()}
	 * {@link #iterator()} and {@link #idIterator()}, return
	 * {@code false} for {@link #contains(String)}
	 * and return null for {@link #get(String)}
	 * @param aceFile the ace file to create an {@link AceFileContigDataStore}
	 * from. (can not be null and must exist)
	 * @param contigIdFilter a {@link DataStoreFilter}
	 * instance if only some contigs from the given
	 * file should be included in this datastore.
	 * Calls to {@link #get(String)}
	 * @return a new {@link AceFileContigDataStore}; 
	 * will never be null.
	 * @throws FileNotFoundException if the ace file does not exist.
	 * @throws NullPointerException if aceFile is null.
	 */
	public static AceFileContigDataStore create(File aceFile, DataStoreFilter contigIdFilter) throws FileNotFoundException{
		return new LargeAceFileDataStore(aceFile, contigIdFilter);
	}
	
	
	private LargeAceFileDataStore(File aceFile, DataStoreFilter contigIdFilter) throws FileNotFoundException {
		if(contigIdFilter ==null){
			throw new NullPointerException("filter can not be null");
		}
		if(aceFile ==null){
			throw new NullPointerException("ace file can not be null");
		}
		if(!aceFile.exists()){
			throw new FileNotFoundException(
					String.format("ace file %s does not exist", aceFile.getAbsolutePath()));
		}
		this.aceFile = aceFile;
		this.contigIdFilter = contigIdFilter;
	}
	
	
	    
    protected final void throwExceptionIfClosed() {
        if(isClosed){
            throw new IllegalStateException("DataStore is closed");
        }
    }
    
    @Override
    public final void close() throws IOException {	    	
        isClosed = true;
    }
    @Override
    public final boolean isClosed() {
        return isClosed;
    }
	@Override
	public synchronized StreamingIterator<String> idIterator() throws DataStoreException {
		throwExceptionIfClosed();
		IdIteratorImpl ids = new IdIteratorImpl();
		ids.start();
		return DataStoreStreamingIterator.create(this, ids);
	}
	@Override
	public AceContig get(String id) throws DataStoreException {
		throwExceptionIfClosed();
		if(!contigIdFilter.accept(id)){
			throw new DataStoreException(String.format("contig id %s not allowed by filter", id));
		}
		IndexedSingleContigVisitor visitor = new IndexedSingleContigVisitor(id);

		try {
			AceFileParser.parse(aceFile, visitor);
		} catch (IOException e) {
			throw new DataStoreException("error parsing ace file",e);
		}
		return visitor.build();
	}
	@Override
	public boolean contains(String id) throws DataStoreException {		
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		throwExceptionIfClosed();
		if(!contigIdFilter.accept(id)){
			throw new DataStoreException(String.format("contig id %s not allowed by filter", id));
		}

		StreamingIterator<String> ids = idIterator();
		try{
			while(ids.hasNext()){
				String nextId = ids.next();
				if(id.equals(nextId)){
					return true;
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(ids);
		}
		return false;
	}
	
	
	@Override
	public synchronized StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator()
			throws DataStoreException {
		throwExceptionIfClosed();
		if( wholeAssemblyTags==null){
			setTagLists();
		}
		return IteratorUtil.createStreamingIterator(wholeAssemblyTags.iterator());
	}
	@Override
	public synchronized StreamingIterator<ReadAceTag> getReadTagIterator()
			throws DataStoreException {
		throwExceptionIfClosed();
		if( readTags==null){
			setTagLists();
		}
		return IteratorUtil.createStreamingIterator(readTags.iterator());
	}
	@Override
	public synchronized StreamingIterator<ConsensusAceTag> getConsensusTagIterator()
			throws DataStoreException {
		throwExceptionIfClosed();
		if( consensusTags==null){
			setTagLists();
		}
		return IteratorUtil.createStreamingIterator(consensusTags.iterator());
	}
	private void setTagLists() throws DataStoreException {
		try {
			AceTags aceTags = DefaultAceTagsFromAceFile.create(aceFile);
			wholeAssemblyTags = aceTags.getWholeAssemblyTags();
			consensusTags = aceTags.getConsensusTags();
			readTags = aceTags.getReadTags();
		} catch (IOException e) {
			throw new DataStoreException("error parsing ace tags from ace file", e);
		}
	}
	@Override
	public synchronized long getNumberOfTotalReads() throws DataStoreException {
		throwExceptionIfClosed();
		if( totalNumberOfReads==null){
			//haven't parsed total number of reads yet 
			SizeVisitor visitor = new SizeVisitor();
			try {
				AceFileParser.parse(aceFile, visitor);
			} catch (IOException e) {
				throw new DataStoreException("error parsing total number of reads",e);
			}
			totalNumberOfReads= visitor.getTotalNumberOfReads();
			numberOfContigs= visitor.getNumberOfContigs();
		}
		return totalNumberOfReads;
	}
	@Override
	public synchronized long getNumberOfRecords() throws DataStoreException {
		throwExceptionIfClosed();
		if(numberOfContigs ==null){
			//haven't parsed num contigs yet 
			SizeVisitor visitor = new SizeVisitor();
			try {
				AceFileParser.parse(aceFile, visitor);
			} catch (IOException e) {
				throw new DataStoreException("error parsing number of contigs",e);
			}
			totalNumberOfReads= visitor.getTotalNumberOfReads();
			numberOfContigs= visitor.getNumberOfContigs();
		}
		return numberOfContigs;
	}
	
	@Override
	public StreamingIterator<AceContig> iterator() {
		throwExceptionIfClosed();
		AceFileDataStoreIterator iter= new AceFileDataStoreIterator();
	    iter.start();
	    return DataStoreStreamingIterator.create(this, iter);
	}
	

	private final class SizeVisitor implements AceFileVisitor{

		private long size=0L;
		private long totalNumberOfReads=0L;
		
		public long getNumberOfContigs() {
			return size;
		}

		public long getTotalNumberOfReads() {
			return totalNumberOfReads;
		}

		@Override
		public void visitLine(String line) {
			//no-op
			
		}

		@Override
		public void visitFile() {
			//no-op
			
		}

		@Override
		public void visitEndOfFile() {
			//no-op
			
		}

		@Override
		public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
			//no-op
			
		}

		@Override
		public BeginContigReturnCode visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			if(contigIdFilter.accept(contigId)){
				size++;
				totalNumberOfReads+=numberOfReads;
				return BeginContigReturnCode.VISIT_CURRENT_CONTIG;
			}
			return BeginContigReturnCode.SKIP_CURRENT_CONTIG;
			
		}

		@Override
		public void visitConsensusQualities(QualitySequence ungappedConsensusQualities) {
			//no-op
			
		}

		@Override
		public void visitAlignedReadInfo(String readId, Direction dir,
				int gappedStartOffset) {
			//no-op
			
		}

		@Override
		public void visitBaseSegment(Range gappedConsensusRange, String readId) {
			//no-op
			
		}

		@Override
		public BeginReadReturnCode visitBeginRead(String readId, int gappedLength) {
			return BeginReadReturnCode.VISIT_CURRENT_READ;
			
		}

		@Override
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			 ClipPointsType clipPointsType = ConsedUtil.ClipPointsType.getType(qualLeft, qualRight, alignLeft, alignRight);
	     		if(clipPointsType !=ClipPointsType.VALID){
	     			//ignore read
	     			totalNumberOfReads--;
	     		}
			
		}

		@Override
		public void visitTraceDescriptionLine(String traceName, String phdName,
				Date date) {
			//no-op
			
		}

		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			//no-op
			
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			//no-op
			
		}

		@Override
		public EndContigReturnCode visitEndOfContig() {
			//no-op
			return EndContigReturnCode.KEEP_PARSING;
		}

		@Override
		public void visitBeginConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			//no-op
			
		}

		@Override
		public void visitConsensusTagComment(String comment) {
			//no-op
			
		}

		@Override
		public void visitConsensusTagData(String data) {
			//no-op
			
		}

		@Override
		public void visitEndConsensusTag() {
			//no-op
			
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			//no-op
			
		}

	}
	/**
     * Special implementation of a {@link StreamingIterator}
     * that directly parses the ace file.  This allows us
     * to iterate over the entire file in 1 pass.
     * @author dkatzel
     */
    private final class AceFileDataStoreIterator extends AbstractBlockingStreamingIterator<AceContig>{

        /**
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() {
            AbstractAceFileVisitorContigBuilder builder = new AbstractAceFileVisitorContigBuilder() {

                @Override
				public synchronized boolean shouldParseContig(String contigId,
						int numberOfBases, int numberOfReads,
						int numberOfBaseSegments, boolean reverseComplimented) {
					return contigIdFilter.accept(contigId);
				}

			

                @Override
                protected void visitContig(AceContigBuilder contigBuilder) {
                    AceFileDataStoreIterator.this.blockingPut(contigBuilder.build());
                    
                }
                
                
            };
            try {
                AceFileParser.parse(aceFile, builder);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
    private static final class IndexedSingleContigVisitor implements AceFileVisitor, Builder<AceContig>{

    	private SingleAceContigBuilderVisitor visitorBuilder;
    	private final String contigIdToGet;
    	private String currentLine;
		public IndexedSingleContigVisitor(String contigIdToGet) {
			this.contigIdToGet = contigIdToGet;
		}

		@Override
		public void visitLine(String line) {
			if(visitorBuilder==null){
				currentLine = line;
			}else{				
				visitorBuilder.visitLine(line);
			}
			
		}

		@Override
		public void visitFile() {
			//no-op
		}

		@Override
		public void visitEndOfFile() {
			//no-op
		}

		@Override
		public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
			//no-op
		}



		@Override
		public BeginContigReturnCode visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			
			if(contigIdToGet.equals(contigId)){
				visitorBuilder = new SingleAceContigBuilderVisitor();
				visitorBuilder.visitLine(currentLine);
				return visitorBuilder.visitBeginContig(contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplimented);
			}
			return BeginContigReturnCode.SKIP_CURRENT_CONTIG;
			
		}

		@Override
		public void visitConsensusQualities(QualitySequence ungappedConsensusQualities) {
			//no-op
		}

		@Override
		public void visitAlignedReadInfo(String readId, Direction dir,
				int gappedStartOffset) {
			if(visitorBuilder!=null){
				visitorBuilder.visitAlignedReadInfo(readId, dir, gappedStartOffset);
			}
			
		}

		@Override
		public void visitBaseSegment(Range gappedConsensusRange, String readId) {
			//no-op
		}

		@Override
		public BeginReadReturnCode visitBeginRead(String readId, int gappedLength) {
			if(visitorBuilder!=null){
				visitorBuilder.visitBeginRead(readId, gappedLength);
			}
			return BeginReadReturnCode.VISIT_CURRENT_READ;
		}

		@Override
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			if(visitorBuilder!=null){
				visitorBuilder.visitQualityLine(qualLeft, qualRight, alignLeft, alignRight);
			}
			
		}

		@Override
		public void visitTraceDescriptionLine(String traceName, String phdName,
				Date date) {
			if(visitorBuilder!=null){
				visitorBuilder.visitTraceDescriptionLine(traceName, phdName, date);
			}
			
		}

		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			if(visitorBuilder!=null){
				visitorBuilder.visitBasesLine(mixedCaseBasecalls);
			}
			
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			
		}

		@Override
		public EndContigReturnCode visitEndOfContig() {
			if(visitorBuilder !=null){
				return visitorBuilder.visitEndOfContig();
			}
			//haven't found our contig yet
			return EndContigReturnCode.KEEP_PARSING;
		}

		@Override
		public void visitBeginConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			
		}

		@Override
		public void visitConsensusTagComment(String comment) {
			//no-op
		}

		@Override
		public void visitConsensusTagData(String data) {
			//no-op
		}

		@Override
		public void visitEndConsensusTag() {
			//no-op
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			//no-op
		}

		@Override
		public AceContig build() {
			return visitorBuilder.getContig();
		}
    	
    }
    
    
    	
    
    private final class IdIteratorImpl extends AbstractBlockingStreamingIterator<String>{
    	private class InnerVisitor implements AceFileVisitor{
    		@Override
			public void visitLine(String line) {
				//no-op
				
			}

			@Override
			public void visitFile() {
				//no-op
				
			}

			@Override
			public void visitEndOfFile() {
				//no-op
				
			}

			@Override
			public void visitHeader(int numberOfContigs,
					int totalNumberOfReads) {
				//no-op
				
			}


			@Override
			public BeginContigReturnCode visitBeginContig(String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplimented) {
				if(contigIdFilter.accept(contigId)){
					IdIteratorImpl.this.blockingPut(contigId);
					return BeginContigReturnCode.VISIT_CURRENT_CONTIG;
				}
				return BeginContigReturnCode.SKIP_CURRENT_CONTIG;
				
			}

			@Override
			public void visitConsensusQualities(QualitySequence ungappedConsensusQualities) {
				//no-op
				
			}

			@Override
			public void visitAlignedReadInfo(String readId,
					Direction dir, int gappedStartOffset) {
				//no-op
				
			}

			@Override
			public void visitBaseSegment(Range gappedConsensusRange,
					String readId) {
				//no-op
				
			}

			@Override
			public BeginReadReturnCode visitBeginRead(String readId, int gappedLength) {
				return BeginReadReturnCode.VISIT_CURRENT_READ;
				
			}

			@Override
			public void visitQualityLine(int qualLeft, int qualRight,
					int alignLeft, int alignRight) {
				//no-op
				
			}

			@Override
			public void visitTraceDescriptionLine(String traceName,
					String phdName, Date date) {
				//no-op
				
			}

			@Override
			public void visitBasesLine(String mixedCaseBasecalls) {
				//no-op
				
			}

			@Override
			public void visitReadTag(String id, String type,
					String creator, long gappedStart, long gappedEnd,
					Date creationDate, boolean isTransient) {
				//no-op
				
			}

			@Override
			public EndContigReturnCode visitEndOfContig() {
				//no-op
				return EndContigReturnCode.KEEP_PARSING;
			}

			@Override
			public void visitBeginConsensusTag(String id, String type,
					String creator, long gappedStart, long gappedEnd,
					Date creationDate, boolean isTransient) {
				//no-op
				
			}

			@Override
			public void visitConsensusTagComment(String comment) {
				//no-op
				
			}

			@Override
			public void visitConsensusTagData(String data) {
				//no-op
				
			}

			@Override
			public void visitEndConsensusTag() {
				//no-op
				
			}

			@Override
			public void visitWholeAssemblyTag(String type, String creator,
					Date creationDate, String data) {
				//no-op
				
			}
    	}
        /**
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() {
        	AceFileVisitor builder = new InnerVisitor();
            try {
                AceFileParser.parse(aceFile, builder);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
}
