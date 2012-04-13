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

package org.jcvi.common.core.align;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class GappedAlignmentDataStore implements NucleotideDataStore{

    public static GappedAlignmentDataStore createFromAlnFile(File alnFile) throws IOException{
        GappedAlignmentDataStoreBuilder builder = new GappedAlignmentDataStoreBuilder();
        AlnParser.parse(alnFile, builder);
        return builder.build();
    }
    private final NucleotideDataStore delegate;

    private GappedAlignmentDataStore(NucleotideDataStore delegate) {
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
    public NucleotideSequence get(String id) throws DataStoreException {
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
    */
    @Override
    public CloseableIterator<NucleotideSequence> iterator() {
        return delegate.iterator();
    }
    
    private static class GappedAlignmentDataStoreBuilder implements AlnVisitor, Builder<GappedAlignmentDataStore>{
        private final Map<String, StringBuilder> builders = new LinkedHashMap<String, StringBuilder>();
       
        
        /**
        * {@inheritDoc}
        */
        @Override
        public GappedAlignmentDataStore build() {
            Map<String, NucleotideSequence> map = new LinkedHashMap<String, NucleotideSequence>(builders.size());
            for(Entry<String, StringBuilder> entry : builders.entrySet()){
                map.put(entry.getKey(), new NucleotideSequenceBuilder(entry.getValue().toString()).build());
            }
            builders.clear();
            return new GappedAlignmentDataStore(
                    new NucleotideDataStoreAdapter(
                            new SimpleDataStore<NucleotideSequence>(map)));
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitLine(String line) {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfFile() {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginGroup() {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndGroup() {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitAlignedSegment(String id, String gappedAlignment) {
            if(!builders.containsKey(id)){
                builders.put(id, new StringBuilder());
            }
            builders.get(id).append(gappedAlignment);
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConservationInfo(
                List<ConservationInfo> conservationInfos) {
            // TODO Auto-generated method stub
            
        }
        
    }
  
}
