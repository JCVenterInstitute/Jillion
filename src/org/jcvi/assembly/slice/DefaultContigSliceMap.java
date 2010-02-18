/*
 * Created on Apr 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.RangeIterator;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultLocation;
import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class DefaultContigSliceMap<T extends PlacedRead> implements ContigSliceMap<T>{

    private final Contig<T> contig;
    Map<Integer, ContigSlice<T>> sliceMap;
    public DefaultContigSliceMap(Contig<T> contig,CoverageMap<CoverageRegion<VirtualPlacedRead<T>>> coverageMap, QualityDataStore qualityMap, QualityValueStrategy qualityValueStrategy){
        this.contig = contig;
        sliceMap = new HashMap<Integer, ContigSlice<T>>();
        for(CoverageRegion<VirtualPlacedRead<T>> region : coverageMap){
            for(int consensusIndex=(int)region.getStart(); consensusIndex<=region.getEnd(); consensusIndex++ ){
                List<SliceLocation<T>> sliceLocations = new ArrayList<SliceLocation<T>>();
                Location<EncodedGlyphs<NucleotideGlyph>> consensusLocation = getConsensusLocationFor(contig, consensusIndex);
                for(VirtualPlacedRead<T> virtualPlacedRead : region.getElements()){
                    T realRead = virtualPlacedRead.getRealPlacedRead();
                    try {
                        if(qualityMap.contains(realRead.getId())){
                            EncodedGlyphs<PhredQuality> qualityRecord = qualityMap.get(realRead.getId());
                                
                            DefaultSliceLocation<T> sliceLocation = createSliceLocationFor(
                                    qualityValueStrategy, consensusIndex,
                                    virtualPlacedRead, realRead, qualityRecord);
                            sliceLocations.add(sliceLocation);
                        }
                    } catch (DataStoreException e) {
                        throw new IllegalStateException("error getting qualities from datastore",e);
                    }
                }
                DefaultContigSlice<T> contigSlice = new DefaultContigSlice<T>(consensusLocation, sliceLocations);
                sliceMap.put(Integer.valueOf(consensusIndex), contigSlice);
            }
        }
    }


    protected DefaultSliceLocation<T> createSliceLocationFor(
            QualityValueStrategy qualityValueStrategy, int consensusIndex,
            VirtualPlacedRead<T> virtualPlacedRead, T realRead,
            final EncodedGlyphs<PhredQuality> qualityRecord) {
        final int indexIntoRead = getIndexIntoRealRead(consensusIndex, virtualPlacedRead);
        PhredQuality qualityValue =qualityValueStrategy.getQualityFor(realRead, qualityRecord, indexIntoRead);
        
        DefaultSliceLocation<T> sliceLocation = new DefaultSliceLocation<T>(realRead, indexIntoRead, qualityValue);
        return sliceLocation;
    }


    private int getIndexIntoRealRead(int consensusIndex,
            VirtualPlacedRead<T> virtualPlacedRead) {
        final int indexIntoVirtualRead = consensusIndex-(int)virtualPlacedRead.getStart();
        final int indexIntoRead = virtualPlacedRead.getRealIndexOf(indexIntoVirtualRead);
        return indexIntoRead;
    }


    protected Location<EncodedGlyphs<NucleotideGlyph>> getConsensusLocationFor(
            Contig<T> contig, int consensusIndex) {
        return new DefaultLocation<EncodedGlyphs<NucleotideGlyph>>(contig.getConsensus(), consensusIndex);
    }

    
    @Override
    public Contig<T> getContig() {
        return contig;
    }

    @Override
    public ContigSlice<T> getContigSliceAt(int index) {
        return sliceMap.get(Integer.valueOf(index));
    }

    @Override
    public Iterable<ContigSlice<T>> getContigSlicesWhichIntersect(Range range) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<ContigSlice<T>> getContigSlicesWithin(Range range) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<ContigSlice<T>> iterator() {
        return new ContigSliceIterator<T>(this);
    }
   
    private static class ContigSliceIterator<T extends PlacedRead> implements Iterator<ContigSlice<T>>{
        private final RangeIterator iter;
        private final ContigSliceMap<T> map;
        public ContigSliceIterator(ContigSliceMap<T> map){
            this(map, Range.buildRangeOfLength(0, map.getContig().getConsensus().getLength()));
        }
        
        public ContigSliceIterator(ContigSliceMap<T> map, Range range){
            this.iter = new RangeIterator(range);
            this.map = map;

        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public ContigSlice<T> next() {
            return map.getContigSliceAt(iter.next().intValue());
        }

        @Override
        public void remove() {
            iter.remove();
            
        }
    }

}
