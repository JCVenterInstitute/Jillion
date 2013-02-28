/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
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
final class LargeAceFileDataStore2 implements AceFileContigDataStore{

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
		return new LargeAceFileDataStore2(aceFile, DataStoreFilters.alwaysAccept());
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
		return new LargeAceFileDataStore2(aceFile, contigIdFilter);
	}
	
	
	private LargeAceFileDataStore2(File aceFile, DataStoreFilter contigIdFilter) throws FileNotFoundException {
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
		SingleContigVisitor visitor = new SingleContigVisitor(id);

		try {
			AceFileParser2.create(aceFile).accept(visitor);
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
			AceFileParser2.create(aceFile).accept(visitor);
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
	

	private final class SizeVisitor implements AceFileVisitor2{

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

		/*
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
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			 ClipPointsType clipPointsType = ConsedUtil.ClipPointsType.getType(qualLeft, qualRight, alignLeft, alignRight);
	     		if(clipPointsType !=ClipPointsType.VALID){
	     			//ignore read
	     			totalNumberOfReads--;
	     		}
			
		}

		*/

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
            AceFileVisitor2 visitor = new AceFileVisitor2() {

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
                AceFileParser2.create(aceFile).accept(visitor);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
    private static final class SingleContigVisitor implements AceFileVisitor2{

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
    	private class InnerVisitor implements AceFileVisitor2{

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
        	AceFileVisitor2 builder = new InnerVisitor();
            try {
                AceFileParser2.create(aceFile).accept(builder);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
}
