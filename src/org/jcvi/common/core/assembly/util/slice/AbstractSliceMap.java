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
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;

public abstract class  AbstractSliceMap implements SliceMap{

    protected List<IdedSliceElement> createSliceElementsFor(
            CoverageRegion<? extends PlacedRead> region,
            long offset, DataStore<? extends Sequence<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy) {
        List<IdedSliceElement> sliceElements = new ArrayList<IdedSliceElement>(region.getCoverage());
        for(PlacedRead read : region){
            
            Sequence<PhredQuality> qualities;
            try {
                final String id = read.getId();
                
                qualities=null;
                if(qualityDataStore !=null && qualityDataStore.contains(id)){
                	qualities =qualityDataStore.get(id);
                }
                
                int indexIntoRead = (int) (offset - read.getBegin());
                final IdedSliceElement sliceElement = createSliceElementFor(
                        qualityValueStrategy, indexIntoRead, read,
                         qualities);
                sliceElements.add(sliceElement);
            } catch (DataStoreException e) {
               //not found ignore?
            }

        }
        return sliceElements;
    }
    protected IdedSliceElement createSliceElementFor(
            QualityValueStrategy qualityValueStrategy, int gappedIndex,
            PlacedRead realRead,
            final Sequence<PhredQuality> qualities) {

        final Nucleotide calledBase = realRead.getNucleotideSequence().get(gappedIndex);
        try{
            PhredQuality qualityValue =qualityValueStrategy.getQualityFor(realRead, qualities, gappedIndex);
        
        return new DefaultSliceElement(realRead.getId(), calledBase, qualityValue, realRead.getDirection());
        }
        catch(NullPointerException e){
            throw e;
        }
        }
    
    static class  SliceIterator implements Iterator<IdedSlice>{
        private final Iterator<Long> iter;
        private final SliceMap sliceMap;
        SliceIterator(Iterator<Long> offsetIterator,SliceMap sliceMap){
            this.iter = offsetIterator;
            this.sliceMap = sliceMap;
        }
        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public IdedSlice next() {
            return sliceMap.getSlice(iter.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();            
        }
        
    }
}
