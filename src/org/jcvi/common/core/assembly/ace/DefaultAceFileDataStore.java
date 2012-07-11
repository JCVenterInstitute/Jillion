/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on May 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;
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
public final class DefaultAceFileDataStore implements AceFileContigDataStore{
    /**
     * Create a new empty AceContigDataStoreBuilder
     * that will need to be populated.
     * @return a new AceContigDataStoreBuilder; never null.
     */
    public static AceContigDataStoreBuilder createBuilder(){
        return new DefaultAceFileDataStoreBuilder();
    }
    /**
     * Create a new empty AceContigDataStoreBuilder
     * that will need to be populated.
     * @param filter a {@link DataStoreFilter} that can be used
     * to include/exclude certain contigs can not be null.
     * @return a new AceContigDataStoreBuilder; never null.
     * @throws NullPointerException if filter is null.
     */
    public static AceContigDataStoreBuilder createBuilder(DataStoreFilter filter){
        return new DefaultAceFileDataStoreBuilder(filter);
    }
    /**
     * Create a new AceContigDataStore that stores
     * all the {@link AceContig}s in a Map.
     * @param aceFile the ace file to use to 
     * to populate the datstore.
     * @return a new AceContigDataStore which contains
     * all the {@link AceContig}s specified in the given
     * ace file.
     * @throws IOException if there is a problem reading the ace file.
     */
    public static AceFileContigDataStore create(File aceFile) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder();
        AceFileParser.parse(aceFile, builder);
        return builder.build();
    }
    /**
     * Create a new AceContigDataStore that stores
     * all the {@link AceContig}s in a Map.
     * @param aceFile the ace file to use to 
     * to populate the datstore.
     * @param filter a {@link DataStoreFilter} that can be used
     * to include/exclude certain contigs can not be null.
     * @return a new AceContigDataStore which contains
     * all the {@link AceContig}s specified in the given
     * ace file.
     * @throws IOException if there is a problem reading the ace file.
     * @throws NullPointerException if filter is null.
     */
    public static AceFileContigDataStore create(File aceFile,DataStoreFilter filter) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder(filter);
        AceFileParser.parse(aceFile, builder);
        return builder.build();
    }
    private final DataStore<AceContig> delegate;
    private final long totalNumberOfReads;
    private final List<WholeAssemblyAceTag> wholeAssemblyTags;
    private final List<ConsensusAceTag> consensusTags;
    private final List<ReadAceTag> readTags;
   
    
    private DefaultAceFileDataStore(DataStore<AceContig> delegate,
			long totalNumberOfReads,
			List<WholeAssemblyAceTag> wholeAssemblyTags,
			List<ConsensusAceTag> consensusTags, List<ReadAceTag> readTags) {
		this.delegate = delegate;
		this.totalNumberOfReads = totalNumberOfReads;
		this.wholeAssemblyTags = wholeAssemblyTags;
		this.consensusTags = consensusTags;
		this.readTags = readTags;
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> idIterator() throws DataStoreException {
        return delegate.idIterator();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public AceContig get(String id) throws DataStoreException {
        return delegate.get(id);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return delegate.getNumberOfRecords();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
       delegate.close();
        
    }
    /**
    * {@inheritDoc}
     * @throws DataStoreException 
    */
    @Override
    public CloseableIterator<AceContig> iterator() throws DataStoreException {
        return delegate.iterator();
    }

    @Override
	public long getNumberOfTotalReads() {
		return totalNumberOfReads;
	}
	@Override
	public CloseableIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() {
		return CloseableIteratorAdapter.adapt(wholeAssemblyTags.iterator());
	}
	@Override
	public CloseableIterator<ReadAceTag> getReadTagIterator() {
		return CloseableIteratorAdapter.adapt(readTags.iterator());
	}
	@Override
	public CloseableIterator<ConsensusAceTag> getConsensusTagIterator() {
		return CloseableIteratorAdapter.adapt(consensusTags.iterator());
	}

	private static class DefaultAceFileDataStoreBuilder extends AbstractAceContigBuilder implements AceContigDataStoreBuilder{
        private Map<String, AceContig> contigMap;
        
        private final DataStoreFilter filter;
        private long totalNumberOfReads =0L;
        private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
        private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
        private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
        /**
         * Consensus tags span multiple lines of the ace file so we need to build
         * up the consensus tags as we parse.
         */
        private DefaultConsensusAceTag.Builder consensusTagBuilder;
        
        public DefaultAceFileDataStoreBuilder(){
            this(AcceptingDataStoreFilter.INSTANCE);
        }
        public DefaultAceFileDataStoreBuilder(DataStoreFilter filter) {
            if(filter==null){
                throw new NullPointerException("filter can not be null");
            }
            this.filter = filter;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitHeader(int numberOfContigs,
                int totalNumberOfReads) {            
            super.visitHeader(numberOfContigs, totalNumberOfReads);
            contigMap = new LinkedHashMap<String, AceContig>(numberOfContigs+1,1F);
        }

        @Override
		public boolean shouldVisitContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			return filter.accept(contigId);
		}
		
        /**
        * {@inheritDoc}
        */
        @Override
        public AceFileContigDataStore build() {
        	AceFileContigDataStore datastore= new DefaultAceFileDataStore(new SimpleDataStore<AceContig>(contigMap),
        			totalNumberOfReads,
        			wholeAssemblyTags,
        			consensusTags,
        			readTags);
        	contigMap=null;
        	return datastore;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        protected void visitContig(AceContig contig) {
            contigMap.put(contig.getId(), contig);
            totalNumberOfReads +=contig.getNumberOfReads();
        }
        
        @Override
        public synchronized void visitBeginConsensusTag(String id, String type, String creator,
                long gappedStart, long gappedEnd, Date creationDate,
                boolean isTransient) {
            super.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
            consensusTagBuilder = new DefaultConsensusAceTag.Builder(id, 
                    type, creator, creationDate, Range.create(gappedStart, gappedEnd), isTransient);

        }

        @Override
        public void visitWholeAssemblyTag(String type, String creator,
                Date creationDate, String data) {
            super.visitWholeAssemblyTag(type, creator, creationDate, data);
            wholeAssemblyTags.add(new DefaultWholeAssemblyAceTag(type, creator, creationDate, data.trim()));
        }
        

        @Override
        public void visitConsensusTagComment(String comment) {
            super.visitConsensusTagComment(comment);
            consensusTagBuilder.addComment(comment);

        }

        @Override
        public void visitConsensusTagData(String data) {
            super.visitConsensusTagData(data);
            consensusTagBuilder.appendData(data);

        }

       

        @Override
        public void visitEndConsensusTag() {
            super.visitEndConsensusTag();
            consensusTags.add(consensusTagBuilder.build());

        }

       

        @Override
        public void visitReadTag(String id, String type, String creator,
                long gappedStart, long gappedEnd, Date creationDate,
                boolean isTransient) {
            super.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
            readTags.add(new DefaultReadAceTag(id, type, creator, creationDate, 
                    Range.create(gappedStart,gappedEnd), isTransient));

        }

    }
}
