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
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code DefaultAceFileDataStore} is a AceContigDataStore
 * implementation that stores all the {@link AceContig}s
 * in a Map.  This implementation is not very 
 * memory effiecient and therefore should not be used
 * for large ace files.
 * @author dkatzel
 *
 *
 */
public final class DefaultAceFileDataStore implements AceContigDataStore{
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
    public static AceContigDataStore create(File aceFile) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder();
        AceFileParser.parseAceFile(aceFile, builder);
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
    public static AceContigDataStore create(File aceFile,DataStoreFilter filter) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder(filter);
        AceFileParser.parseAceFile(aceFile, builder);
        return builder.build();
    }
    private final DataStore<AceContig> delegate;

   
    private DefaultAceFileDataStore(DataStore<AceContig> delegate) {
        this.delegate = delegate;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return delegate.getIds();
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
    public int size() throws DataStoreException {
        return delegate.size();
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
    */
    @Override
    public CloseableIterator<AceContig> iterator() {
        return delegate.iterator();
    }

    private static class DefaultAceFileDataStoreBuilder extends AbstractAceContigBuilder implements AceContigDataStoreBuilder{
        private Map<String, AceContig> contigMap;
        private final DataStoreFilter filter;
        
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
        public AceContigDataStore build() {
            return new DefaultAceFileDataStore(new SimpleDataStore<AceContig>(contigMap));
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        protected void visitContig(AceContig contig) {
            contigMap.put(contig.getId(), contig);
            
        }
        
    }
}
