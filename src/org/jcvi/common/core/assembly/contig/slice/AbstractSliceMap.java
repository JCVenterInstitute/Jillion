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
package org.jcvi.common.core.assembly.contig.slice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.QualityValueStrategy;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.qual.PhredQuality;

public abstract class  AbstractSliceMap implements SliceMap{

    protected List<SliceElement> createSliceElementsFor(
            CoverageRegion<? extends PlacedRead> region,
            long offset, DataStore<? extends Sequence<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy) {
        List<SliceElement> sliceElements = new ArrayList<SliceElement>(region.getCoverage());
        for(PlacedRead read : region){
            
            Sequence<PhredQuality> qualities;
            try {
                final String id = read.getId();
                
                qualities = qualityDataStore==null?null:qualityDataStore.get(id);
                
                int indexIntoRead = (int) (offset - read.getStart());
                final SliceElement sliceElement = createSliceElementFor(
                        qualityValueStrategy, indexIntoRead, read,
                         qualities);
                sliceElements.add(sliceElement);
            } catch (DataStoreException e) {
               //not found ignore?
            }

        }
        return sliceElements;
    }
    protected SliceElement createSliceElementFor(
            QualityValueStrategy qualityValueStrategy, int gappedIndex,
            PlacedRead realRead,
            final Sequence<PhredQuality> qualities) {

        final NucleotideGlyph calledBase = realRead.getEncodedGlyphs().get(gappedIndex);
        try{
            PhredQuality qualityValue =qualityValueStrategy.getQualityFor(realRead, qualities, gappedIndex);
        
        return new DefaultSliceElement(realRead.getId(), calledBase, qualityValue, realRead.getSequenceDirection());
        }
        catch(NullPointerException e){
            throw e;
        }
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
