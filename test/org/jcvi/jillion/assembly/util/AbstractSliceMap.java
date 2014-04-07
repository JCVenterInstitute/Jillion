/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
/*
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

public abstract class  AbstractSliceMap implements SliceMap{

    protected List<SliceElement> createSliceElementsFor(
            CoverageRegion<? extends AssembledRead> region,
            long offset, QualitySequenceDataStore qualityDataStore,
            GapQualityValueStrategy qualityValueStrategy) {
        List<SliceElement> sliceElements = new ArrayList<SliceElement>(region.getCoverageDepth());
        for(AssembledRead read : region){
            
            QualitySequence qualities;
            try {
                final String id = read.getId();
                
                qualities=null;
                if(qualityDataStore !=null && qualityDataStore.contains(id)){
                	qualities =qualityDataStore.get(id);
                }
                
                int indexIntoRead = (int) (offset - read.getGappedStartOffset());
                final SliceElement sliceElement = createSliceElementFor(
                        qualityValueStrategy, indexIntoRead, read,
                         qualities);
                sliceElements.add(sliceElement);
            } catch (DataStoreException e) {
               throw new RuntimeException("error getting quality data for "+ read,e);
            }

        }
        return sliceElements;
    }
    protected SliceElement createSliceElementFor(
            GapQualityValueStrategy qualityValueStrategy, int gappedIndex,
            AssembledRead read,
            final QualitySequence qualities) {

        final Nucleotide calledBase = read.getNucleotideSequence().get(gappedIndex);
        	
            PhredQuality qualityValue =qualityValueStrategy
        									.getGappedValidRangeQualitySequenceFor(read, qualities)
            								.get(gappedIndex);
        
        return new DefaultSliceElement(read.getId(), calledBase, qualityValue, read.getDirection());
       
   }
    
    static class  SliceIterator implements Iterator<Slice>{
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
        public Slice next() {
            return sliceMap.getSlice(iter.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();            
        }
        
    }
}
