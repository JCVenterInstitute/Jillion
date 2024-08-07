/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ConsedUtil.ClipPointsType;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
/**
 * {@code LargeAceFileDataStore} is an {@link AceFileDataStore}
 * implementation that doesn't store any contig or 
 * read information in memory.
 * This means that each {@link DataStore#get(String)} or {@link DataStore#contains(String)}
 * requires re-parsing the ace file which can take some time.
 * Other methods such as {@link DataStore#getNumberOfRecords()} are lazy-loaded
 * and are only parsed the first time they are asked for.
 * <p/>
 * Since each method call involves re-parsing the ace file,
 * that file must not be modified or moved during the
 * entire lifetime of the instance.
 * It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 */
final class LargeAceFileDataStore implements AceFileDataStore{

    private final File aceFile;
    private Long numberOfContigs = null;
    private Long totalNumberOfReads = null;

    private List<WholeAssemblyAceTag> wholeAssemblyTags = null;
    private List<ConsensusAceTag> consensusTags = null;
    private List<ReadAceTag> readTags = null;

    private final DataStoreFilter contigIdFilter;
    private volatile boolean isClosed;
	/**
	 * Create a new instance of {@link AceFileDataStore}.
	 * 
	 * @param aceFile the ace file to create an {@link AceFileDataStore}
	 * from. (can not be null and must exist)
	 * 
	 * @return a new {@link AceFileDataStore}; 
	 * will never be null.
	 * 
	 * @throws FileNotFoundException if the ace file does not exist.
	 * @throws NullPointerException if aceFile is null.
	 */
	public static AceFileDataStore create(File aceFile) throws FileNotFoundException{
		return new LargeAceFileDataStore(aceFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new instance of {@link AceFileDataStore}
	 * with only some of the contigs from the given ace file.
	 * Any contigs excluded by the given {@link DataStoreFilter}
	 * will be completely ignored during calls to {@link DataStore#getNumberOfRecords()}
	 * {@link DataStore#iterator()} and {@link DataStore#idIterator()}, return
	 * {@code false} for {@linkDataStore #contains(String)}
	 * and return null for {@link DataStore#get(String)}.
	 * 
	 * @param aceFile the ace file to create an {@link AceFileDataStore}
	 * from. (can not be null and must exist)
	 * 
	 * @param contigIdFilter a {@link DataStoreFilter}
	 * instance if only some contigs from the given
	 * file should be included in this datastore.
	 * Calls to {@link DataStore#get(String)}
	 * 
	 * @return a new {@link AceFileDataStore}; 
	 * will never be null.
	 * 
	 * @throws FileNotFoundException if the ace file does not exist.
	 * @throws NullPointerException if aceFile is null.
	 */
	public static AceFileDataStore create(File aceFile, DataStoreFilter contigIdFilter) throws FileNotFoundException{
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
	
	
	    
    private final void throwExceptionIfClosed() {
        if(isClosed){
            throw new DataStoreClosedException("DataStore is closed");
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
			//if the id isn't accepted by the filter
			//then there is no way it's in this datastore.
			return null;
		}
		SingleContigVisitor visitor = new SingleContigVisitor(id);

		try {
			AceFileParser.create(aceFile).parse(visitor);
		} catch (IOException e) {
			throw new DataStoreException("error parsing ace file",e);
		}
		return visitor.getContig();
	}
	@Override
	public boolean contains(String id) throws DataStoreException {		
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		throwExceptionIfClosed();
		if(!contigIdFilter.accept(id)){
			//if the id isn't accepted by the filter
			//then there is no way it's in this datastore
			return false;
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
			AceTagsVisitor visitor = new AceTagsVisitor();
			AceFileParser.create(aceFile).parse(visitor);
			if(!visitor.isCompletlyParsed()){
				throw new DataStoreException("could not completely parse tags from ace file");
			}
			wholeAssemblyTags = visitor.getWholeAssemblyTags();
			consensusTags = visitor.getConsensusTags();
			readTags = visitor.getReadTags();
		} catch (IOException e) {
			throw new DataStoreException("error parsing ace tags from ace file", e);
		}
	}
	@Override
	public synchronized long getNumberOfTotalReads() throws DataStoreException {
		throwExceptionIfClosed();
		if( totalNumberOfReads==null){
			setNumContigsAndTotalReads();
		}
		return totalNumberOfReads;
	}
	@Override
	public synchronized long getNumberOfRecords() throws DataStoreException {
		throwExceptionIfClosed();
		if(numberOfContigs ==null){
			setNumContigsAndTotalReads();
		}
		return numberOfContigs;
	}
	private synchronized void setNumContigsAndTotalReads() throws DataStoreException {
		//haven't parsed num contigs yet 
		SizeVisitor visitor = new SizeVisitor();
		try {
			AceFileParser.create(aceFile).parse(visitor);
		} catch (IOException e) {
			throw new DataStoreException("error parsing number of contigs",e);
		}
		totalNumberOfReads= visitor.getTotalNumberOfReads();
		numberOfContigs= visitor.getNumberOfContigs();
	}
	
	@Override
	public StreamingIterator<AceContig> iterator() {
		throwExceptionIfClosed();
		AceFileDataStoreIterator iter= new AceFileDataStoreIterator();
	    iter.start();
	    return DataStoreStreamingIterator.create(this, iter);
	}
	
	
	

	@Override
	public final StreamingIterator<DataStoreEntry<AceContig>> entryIterator()
			throws DataStoreException {
		throwExceptionIfClosed();
		return new StreamingIterator<DataStoreEntry<AceContig>>(){
			StreamingIterator<AceContig> iter = iterator();
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public void close() {
				iter.close();
			}

			@Override
			public DataStoreEntry<AceContig> next() {
				AceContig next = iter.next();
				return new DataStoreEntry<AceContig>(next.getId(), next);
			}

			@Override
			public void remove() {
				iter.remove();
			}
			
		};
	}




	private final class SizeVisitor implements AceFileVisitor{
private final AceContigReadVisitor readVisitor = new AceContigReadVisitor() {
			
			@Override
			public void visitTraceDescriptionLine(String traceName, String phdName,
					Date date) {
				//no-op								
			}
			
			@Override
			public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
					int alignRight) {
				 ClipPointsType clipPointsType = ConsedUtil.ClipPointsType.getType(qualLeft, qualRight, alignLeft, alignRight);
		     		if(clipPointsType !=ClipPointsType.VALID){
		     			//ignore read
		     			totalNumberOfReads--;
		     		}
				
			}
			
			@Override
			public void visitEnd() {
				//no-op								
			}
			
			@Override
			public void visitBasesLine(String mixedCaseBasecalls) {
				//no-op								
			}
			
			@Override
			public void halted() {
				//no-op								
			}
		}; 
		private long size=0L;
		private long totalNumberOfReads=0L;
		
		public long getNumberOfContigs() {
			return size;
		}

		public long getTotalNumberOfReads() {
			return totalNumberOfReads;
		}

		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
			//no-op			
		}

		@Override
		public AceContigVisitor visitContig(AceFileVisitorCallback callback,
				String contigId, int numberOfBases, int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplemented) {
			if(contigIdFilter.accept(contigId)){
				size++;
				totalNumberOfReads+=numberOfReads;
				return new AceContigVisitor() {
					
					@Override
					public void visitEnd() {
						//no-op						
					}
					
					@Override
					public void visitConsensusQualities(
							QualitySequence ungappedConsensusQualities) {
						//no-op						
					}
					
					@Override
					public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
						return readVisitor;
					}
					
					@Override
					public void visitBasesLine(String mixedCaseBasecalls) {
						//no-op						
					}
					
					@Override
					public void visitBaseSegment(Range gappedConsensusRange, String readId) {
						//no-op						
					}
					
					@Override
					public void visitAlignedReadInfo(String readId, Direction dir,
							int gappedStartOffset) {
						//no-op						
					}
					
					@Override
					public void halted() {
						//no-op						
					}
				};
			}
			return null;
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			//no-op		
		}

		@Override
		public AceConsensusTagVisitor visitConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			//skip
			return null;
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			//no-op		
		}

		@Override
		public void visitEnd() {
			//no-op		
		}

		@Override
		public void halted() {
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
            AceFileVisitor visitor = new AceFileVisitor() {

				@Override
				public void visitHeader(int numberOfContigs,
						long totalNumberOfReads) {
					//no-op					
				}

				@Override
				public AceContigVisitor visitContig(
						AceFileVisitorCallback callback, String contigId,
						int numberOfBases, int numberOfReads,
						int numberOfBaseSegments, boolean reverseComplemented) {
					if(contigIdFilter.accept(contigId)){
						return new AbstractAceContigBuilderVisitor(contigId, numberOfBases, numberOfReads) {
							
							@Override
							protected void visitContig(AceContigBuilder builder) {
								 AceFileDataStoreIterator.this.blockingPut(builder.build());								
							}
						};
					}
					return null;
				}

				@Override
				public void visitReadTag(String id, String type,
						String creator, long gappedStart, long gappedEnd,
						Date creationDate, boolean isTransient) {
					//no-op		
				}

				@Override
				public AceConsensusTagVisitor visitConsensusTag(String id,
						String type, String creator, long gappedStart,
						long gappedEnd, Date creationDate, boolean isTransient) {
					//skip
					return null;
				}

				@Override
				public void visitWholeAssemblyTag(String type, String creator,
						Date creationDate, String data) {
					//no-op		
				}

				@Override
				public void visitEnd() {
					//no-op		
				}

				@Override
				public void halted() {
					//no-op		
				}

            };
            try {
                AceFileParser.create(aceFile).parse(visitor);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
    private static final class SingleContigVisitor implements AceFileVisitor{

    	private AceContig contig;
    	private final String contigIdToGet;
    	
		public SingleContigVisitor(String contigIdToGet) {
			this.contigIdToGet = contigIdToGet;
		}		
		
		public final AceContig getContig() {
			return contig;
		}


		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
			//no-op			
		}
		@Override
		public AceContigVisitor visitContig(final AceFileVisitorCallback callback,
				String contigId, int numberOfBases, int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplemented) {
			if(contigId.equals(contigIdToGet)){
				return new AbstractAceContigBuilderVisitor(contigId, numberOfBases, numberOfReads) {
					
					@Override
					protected void visitContig(AceContigBuilder builder) {
						contig = builder.build();	
						callback.haltParsing();
					}
				};
			}
			return null;
		}
		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			//no-op		
		}
		@Override
		public AceConsensusTagVisitor visitConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			//skip	
			return null;
		}
		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			//no-op		
		}
		@Override
		public void visitEnd() {
			//no-op		
		}
		@Override
		public void halted() {
			//no-op		
		}
		
    	
    }
    
    
    	
    
    private final class IdIteratorImpl extends AbstractBlockingStreamingIterator<String>{
    	private class InnerVisitor implements AceFileVisitor{

			@Override
			public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
				//no-op				
			}

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				if(contigIdFilter.accept(contigId)){
					IdIteratorImpl.this.blockingPut(contigId);
				}
				return null;
			}

			@Override
			public void visitReadTag(String id, String type, String creator,
					long gappedStart, long gappedEnd, Date creationDate,
					boolean isTransient) {
				//no-op	
			}

			@Override
			public AceConsensusTagVisitor visitConsensusTag(String id,
					String type, String creator, long gappedStart,
					long gappedEnd, Date creationDate, boolean isTransient) {
				//skip
				return null;
			}

			@Override
			public void visitWholeAssemblyTag(String type, String creator,
					Date creationDate, String data) {
				//no-op	
			}

			@Override
			public void visitEnd() {
				//no-op	
			}

			@Override
			public void halted() {
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
                AceFileParser.create(aceFile).parse(builder);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
    
    private class AceTagsVisitor implements AceFileVisitor{

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
	    
	    private boolean completlyParsed=false;
	    
		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
			//no-op
			
		}
		@Override
		public AceContigVisitor visitContig(AceFileVisitorCallback callback,
				String contigId, int numberOfBases, int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplemented) {
			//always skip
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
			if(contigIdFilter.accept(id)){
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
			completlyParsed = true;
			
		}
		@Override
		public void halted() {
			//no-op			
		}
		public final List<WholeAssemblyAceTag> getWholeAssemblyTags() {
			return wholeAssemblyTags;
		}
		public final List<ConsensusAceTag> getConsensusTags() {
			return consensusTags;
		}
		public final List<ReadAceTag> getReadTags() {
			return readTags;
		}
		public final boolean isCompletlyParsed() {
			return completlyParsed;
		}
	    
	    
    }
}
