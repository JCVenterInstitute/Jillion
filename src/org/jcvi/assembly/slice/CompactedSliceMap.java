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

package org.jcvi.assembly.slice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.util.ArrayIterator;

/**
 * @author dkatzel
 * 
 * 
 */
public class CompactedSliceMap implements SliceMap {
    private final CompactedSlice[] slices;

    public <PR extends PlacedRead> CompactedSliceMap(
            Contig<PR> contig, QualityDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy) throws DataStoreException {
        CoverageMap<CoverageRegion<PR>> coverageMap = DefaultCoverageMap
                .buildCoverageMap(contig.getPlacedReads());
        this.slices = createSlices(coverageMap,qualityDataStore,qualityValueStrategy);
    }
    protected CompactedSlice[] createSlices(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
                    QualityDataStore qualityDataStore,
                    QualityValueStrategy qualityValueStrategy) throws DataStoreException {
        int size = (int)coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()+1;
        CompactedSlice[] slices = new CompactedSlice[size];
        for(CoverageRegion<?  extends PlacedRead> region : coverageMap){
            Map<String,EncodedGlyphs<PhredQuality>> qualities = new HashMap<String,EncodedGlyphs<PhredQuality>>(region.getCoverage());
            for(PlacedRead read :region.getElements()){
                final String id = read.getId();
                qualities.put(id,qualityDataStore.get(id));
            }
            for(int i=(int)region.getStart(); i<=region.getEnd(); i++ ){
                
                slices[i] =createSlice(region, qualities,qualityValueStrategy,i);                
            }
        }
        return slices;
    }
    /**
     * {@inheritDoc}
     */
    protected CompactedSlice createSlice(
            CoverageRegion<? extends PlacedRead> region, 
            Map<String,EncodedGlyphs<PhredQuality>> qualities,
            QualityValueStrategy qualityValueStrategy,
            int i) {
        CompactedSlice.Builder builder = new CompactedSlice.Builder();
        for (PlacedRead read : region.getElements()) {
            String id=read.getId();
            int indexIntoRead = (int) (i - read.getStart());
            PhredQuality quality = qualityValueStrategy.getQualityFor(read, qualities.get(id), indexIntoRead);
            builder.addSliceElement(id,
                    read.getEncodedGlyphs().get(indexIntoRead),
                    quality, read.getSequenceDirection());
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
