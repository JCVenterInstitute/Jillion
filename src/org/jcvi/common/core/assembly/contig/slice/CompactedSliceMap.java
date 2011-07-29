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

package org.jcvi.common.core.assembly.contig.slice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.QualityValueStrategy;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.util.ArrayIterator;
import org.jcvi.common.core.util.CloseableIterator;

/**
 * @author dkatzel
 * 
 * 
 */
public class CompactedSliceMap<PR extends PlacedRead, R extends CoverageRegion<PR>, M extends CoverageMap<R>> implements SliceMap {
    private final CompactedSlice[] slices;

    public static <PR extends PlacedRead, C extends Contig<PR>> CompactedSliceMap create(C contig,QualityDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException{
        CoverageMap<CoverageRegion<PR>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig);
        return new CompactedSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }
    public static <PR extends PlacedRead, R extends CoverageRegion<PR>, M extends CoverageMap<R>> CompactedSliceMap create(M coverageMap,QualityDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException{
        return new CompactedSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }
    public static <PR extends PlacedRead, R extends CoverageRegion<PR>, M extends CoverageMap<R>> CompactedSliceMap create(M coverageMap,PhredQuality defaultQuality) throws DataStoreException{
        return new CompactedSliceMap(coverageMap, new NullQualityDataStore(defaultQuality), new FakeQualityValueStrategy(defaultQuality));
    }
    private static final class NullQualityDataStore implements QualityDataStore{
        final QualitySequence fakeQualities;
        public NullQualityDataStore(final PhredQuality defaultQuality){
            fakeQualities = new QualitySequence(){
                @Override
                public List<PhredQuality> decode() {
                    // TODO Auto-generated method stub
                    return null;
                }

                /**
                * {@inheritDoc}
                */
                @Override
                public PhredQuality get(int index) {
                    return defaultQuality;
                }

                /**
                * {@inheritDoc}
                */
                @Override
                public long getLength() {
                    // TODO Auto-generated method stub
                    return 0;
                }

                /**
                * {@inheritDoc}
                */
                @Override
                public List<PhredQuality> decode(Range range) {
                    // TODO Auto-generated method stub
                    return null;
                }
                
            };
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public CloseableIterator<String> getIds() throws DataStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public QualitySequence get(String id) throws DataStoreException {

            return fakeQualities;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean contains(String id) throws DataStoreException {
            // TODO Auto-generated method stub
            return false;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int size() throws DataStoreException {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isClosed() throws DataStoreException {
            // TODO Auto-generated method stub
            return false;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public CloseableIterator<QualitySequence> iterator() {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    private static final class FakeQualityValueStrategy implements QualityValueStrategy{
        private final PhredQuality defaultQuality;
        
        private FakeQualityValueStrategy(PhredQuality defaultQuality) {
            super();
            this.defaultQuality = defaultQuality;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PhredQuality getQualityFor(PlacedRead placedRead,
                Sequence<PhredQuality> fullQualities, int gappedReadIndex) {
            return defaultQuality;
        }
        
    }
    private  CompactedSliceMap(
            M coverageMap, QualityDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException {
        int size = (int)coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()+1;
        this.slices = new CompactedSlice[size];
        for(CoverageRegion<?  extends PlacedRead> region : coverageMap){
            Map<String,Sequence<PhredQuality>> qualities = new HashMap<String,Sequence<PhredQuality>>(region.getCoverage());
            for(PlacedRead read :region){
                final String id = read.getId();
                if(qualityDataStore==null){
                    qualities.put(id,null);
                }else{
                    qualities.put(id,qualityDataStore.get(id));
                }
            }
            for(int i=(int)region.getStart(); i<=region.getEnd(); i++ ){
                
                this.slices[i] =createSlice(region, qualities,qualityValueStrategy,i);                
            }
        }
    }
   
    /**
     * {@inheritDoc}
     */
    protected CompactedSlice createSlice(
            CoverageRegion<? extends PlacedRead> region, 
            Map<String,Sequence<PhredQuality>> qualities,
            QualityValueStrategy qualityValueStrategy,
            int i) {
        CompactedSlice.Builder builder = new CompactedSlice.Builder();
        for (PlacedRead read : region) {
            String id=read.getId();
            int indexIntoRead = (int) (i - read.getStart());
          //  if()
            Sequence<PhredQuality> fullQualities = qualities.get(id);
            final PhredQuality quality;
            if(fullQualities==null){
                quality = PhredQuality.valueOf(30);
            }else{
                quality = qualityValueStrategy.getQualityFor(read, fullQualities, indexIntoRead);
            }
            builder.addSliceElement(id,
                    read.getSequence().get(indexIntoRead),
                    quality, read.getDirection());
        }
        return builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Slice> iterator() {
        return new ArrayIterator<Slice>(slices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Slice getSlice(long offset) {
        return slices[(int) offset];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return slices.length;
    }

}
