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
 * Created on Dec 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.util.CloseableIterator;
import org.jcvi.util.CloseableIteratorAdapter;

public abstract class AbstractArtificialDataStoreFromContig<T> extends AbstractDataStore<T>{

    private final DataStore<? extends Contig> contigs;
    /**
     * @param contig
     */
    public AbstractArtificialDataStoreFromContig(DataStore<? extends Contig> contigDataStore) {
        this.contigs = contigDataStore;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        for(Contig contig : contigs){
            if(contig.containsPlacedRead(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public T get(String id) throws DataStoreException {
        super.get(id);
        for(Contig contig : contigs){
            if(contig.containsPlacedRead(id)){
                return createArtificalTypefor(contig.getPlacedReadById(id));
            }
        }
        return null;
    }

    protected abstract T createArtificalTypefor(PlacedRead read);
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        super.getIds();
        List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
        for(Contig contig : contigs){
            iterators.add(new ContigReadIdIterator(contig));
        }
        return CloseableIteratorAdapter.adapt(IteratorUtils.chainedIterator(iterators));
    }

    @Override
    public int size() throws DataStoreException {
        super.size();
        int size=0;
        for(Contig contig : contigs){
            size +=contig.getNumberOfReads();
        }
        return size;
    }

    private static class ContigReadIdIterator implements Iterator<String>{
        private final Iterator<PlacedRead> placedReadIterator;
        public ContigReadIdIterator(Contig contig){
            placedReadIterator = contig.getPlacedReads().iterator();
        }
        @Override
        public boolean hasNext() {
            return placedReadIterator.hasNext();
        }
        @Override
        public String next() {
            return placedReadIterator.next().getId();
        }
        @Override
        public void remove() {
            placedReadIterator.remove();
            
        }
    }
    
}
