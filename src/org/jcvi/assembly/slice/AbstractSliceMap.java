/*
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class  AbstractSliceMap implements SliceMap{

    protected List<SliceElement> createSliceElementsFor(
            CoverageRegion<? extends PlacedRead> region,
            long offset, DataStore<? extends EncodedGlyphs<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy) {
        List<SliceElement> sliceElements = new ArrayList<SliceElement>(region.getCoverage());
        for(PlacedRead read : region.getElements()){
            
            EncodedGlyphs<PhredQuality> qualities;
            try {
                final String id = read.getId();
                qualities = qualityDataStore.get(id);
                int indexIntoVirtualRead = (int) (offset - read.getStart());
                final SliceElement sliceElement = createSliceElementFor(
                        qualityValueStrategy, indexIntoVirtualRead, read,
                         qualities);
                sliceElements.add(sliceElement);
            } catch (DataStoreException e) {
               //not found ignore?
            }

        }
        return sliceElements;
    }
    protected DefaultSliceElement createSliceElementFor(
            QualityValueStrategy qualityValueStrategy, int gappedIndex,
            PlacedRead realRead,
            final EncodedGlyphs<PhredQuality> qualities) {

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
