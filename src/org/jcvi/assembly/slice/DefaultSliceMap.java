/*
 * Created on Jun 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultSliceMap extends AbstractSliceMap{

    private final Map<Long, Slice> sliceMap = new HashMap<Long, Slice>();
    private final long size;
    public DefaultSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
                        DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore,
                        QualityValueStrategy qualityValueStrategy){
        this.size = coverageMap.getRegion(coverageMap.getSize()-1).getEnd()+1;
        for(CoverageRegion<?  extends PlacedRead> region : coverageMap){
            for(long i=region.getStart(); i<=region.getEnd(); i++ ){
                List<SliceElement> sliceElements = createSliceElementsFor(region, i, qualityDataStore, qualityValueStrategy);
                sliceMap.put(Long.valueOf(i),new DefaultSlice(sliceElements));
            }
        }
    }
    
    public DefaultSliceMap(List<Slice> slices){
        size = slices.size();
        for(int i=0; i< size; i++){
            sliceMap.put(Long.valueOf(i), slices.get(i));
        }
    }
    @Override
    public Slice getSlice(long offset) {
        return sliceMap.get(Long.valueOf(offset));
    }
    @Override
    public long getSize() {
        return size;
    }
    @Override
    public Iterator<Slice> iterator() {
        return new SliceIterator(sliceMap.keySet().iterator(), this);
    }

    
   
}
