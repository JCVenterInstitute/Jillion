package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

final class DefaultAceFileDataStore2 implements AceFileContigDataStore{

	/**
	 * {@link DataStore} wrapper of our {@link AceContig}s.
	 */
	private final DataStore<AceContig> delegate;
	/**
	 * The total number of reads in our datastore
	 * (sum of all the reads in the contigs).
	 */
    private final long totalNumberOfReads;
    /**
     * List of all the {@link WholeAssemblyAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     */
    private final List<WholeAssemblyAceTag> wholeAssemblyTags;
    /**
     * List of all the {@link ConsensusAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     */
    private final List<ConsensusAceTag> consensusTags;
    /**
     * List of all the {@link ReadAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     */
    private final List<ReadAceTag> readTags;
    
    public static AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException{
    	Visitor builder = new Visitor(filter);
    	AceFileParser2 parser = AceFileParser2.create(aceFile);
    	parser.accept(builder);
    	return new DefaultAceFileDataStore2(builder);
    }
    
	private DefaultAceFileDataStore2(Visitor builder) {
		if(!builder.completed){
			throw new IllegalStateException("did not completely parse ace file");
		}
		this.delegate = DataStoreUtil.adapt(builder.map);
		this.totalNumberOfReads = builder.totalNumberOfReads;
		this.wholeAssemblyTags = builder.wholeAssemblyTags;
		this.consensusTags = builder.consensusTags;
		this.readTags = builder.readTags;
	}	
    
	
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return delegate.idIterator();
	}


	@Override
	public AceContig get(String id) throws DataStoreException {
		return delegate.get(id);
	}


	@Override
	public boolean contains(String id) throws DataStoreException {
		return delegate.contains(id);
	}


	@Override
	public long getNumberOfRecords() throws DataStoreException {
		return delegate.getNumberOfRecords();
	}


	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}


	@Override
	public StreamingIterator<AceContig> iterator() throws DataStoreException {
		return delegate.iterator();
	}


	@Override
	public void close() throws IOException {
		delegate.close();
		
	}


	@Override
	public long getNumberOfTotalReads() throws DataStoreException {
		if(isClosed()){
			throw new DataStoreException("closed");
		}
		return totalNumberOfReads;
	}


	@Override
	public StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator()
			throws DataStoreException {
		return DataStoreStreamingIterator.create(this, wholeAssemblyTags.iterator());
	}


	@Override
	public StreamingIterator<ReadAceTag> getReadTagIterator()
			throws DataStoreException {
		return DataStoreStreamingIterator.create(this, readTags.iterator());
	}


	@Override
	public StreamingIterator<ConsensusAceTag> getConsensusTagIterator()
			throws DataStoreException {
		return DataStoreStreamingIterator.create(this, consensusTags.iterator());
	}


	private static final class Visitor implements AceFileVisitor2{

		private final DataStoreFilter filter;
		
		private Map<String, AceContig> map;
		/**
		 * The total number of reads in our datastore
		 * (sum of all the reads in the contigs).
		 */
	    private long totalNumberOfReads;
	    /**
	     * List of all the {@link WholeAssemblyAceTag}s
	     * in the ace file in the order they are
	     * declared in the file.
	     */
	    private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
	    /**
	     * List of all the {@link ConsensusAceTag}s
	     * in the ace file in the order they are
	     * declared in the file.
	     */
	    private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
	    /**
	     * List of all the {@link ReadAceTag}s
	     * in the ace file in the order they are
	     * declared in the file.
	     */
	    private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
	    
	    private boolean completed = false;
	    
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
			this.totalNumberOfReads = totalNumberOfReads;
			this.map = new LinkedHashMap<String, AceContig>(numberOfContigs);
		}

		@Override
		public AceContigVisitor visitContig(AceFileVisitorCallback callback, final String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplemented) {
			if(filter.accept(contigId)){
				return new AbstractAceContigBuilderVisitor(contigId, numberOfBases, numberOfReads) {
					
					@Override
					protected void visitContig(AceContigBuilder builder) {
						map.put(contigId, builder.build());
						
					}
				};
			}
			//skip 
			return null;
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			readTags.add(new ReadAceTag(id, type, creator, creationDate, 
                    Range.of(gappedStart,gappedEnd), isTransient));
			
		}

		@Override
		public AceConsensusTagVisitor visitConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			if(filter.accept(id)){
				return new AbstractAceConsensusTagVisitor(id, type,creator, 
						gappedStart, gappedEnd, creationDate, isTransient) {
					
					@Override
					protected void visitConsensusTag(ConsensusAceTag consensusTag) {
						consensusTags.add(consensusTag);						
					}
				};
			}
			//skip
			return null;
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			wholeAssemblyTags.add(new WholeAssemblyAceTag(type, creator, creationDate, data.trim()));
			
		}

		@Override
		public void visitEnd() {
			completed = true;			
		}

		@Override
		public void halted() {
			//no-op			
		}		
	}    
}
