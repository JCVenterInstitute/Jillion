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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
/**
 * {@code DefaultAceFileDataStore} is a AceContigDataStore
 * implementation that stores all the {@link AceContig}s
 * in a Map.  This implementation is not very 
 * memory efficient and therefore should not be used
 * for large ace files.
 * @author dkatzel
 *
 *
 */
final class DefaultAceFileDataStore implements AceFileContigDataStore{

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
    
    public static AceFileContigDataStore create(InputStream aceFileStream) throws IOException{
    	return create(aceFileStream, DataStoreFilters.alwaysAccept());
    }
    
    public static AceFileContigDataStore create(InputStream aceFileStream, DataStoreFilter filter) throws IOException{
    	Visitor builder = new Visitor(filter);
    	AceHandler parser = AceFileParser.create(aceFileStream);
    	parser.accept(builder);
    	return new DefaultAceFileDataStore(builder);
    }
    public static AceFileContigDataStore create(File aceFile) throws IOException{
    	return create(aceFile, DataStoreFilters.alwaysAccept());
    }
    public static AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException{
    	Visitor builder = new Visitor(filter);
    	AceHandler parser = AceFileParser.create(aceFile);
    	parser.accept(builder);
    	return new DefaultAceFileDataStore(builder);
    }
    
	private DefaultAceFileDataStore(Visitor builder) {
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
	public long getNumberOfTotalReads(){
		if(isClosed()){
			throw new DataStoreClosedException("closed");
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


	private static final class Visitor implements AceFileVisitor{

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
						totalNumberOfReads += builder.numberOfReads();
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
