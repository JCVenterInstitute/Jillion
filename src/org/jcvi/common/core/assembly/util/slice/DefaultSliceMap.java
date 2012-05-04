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
 * Created on Jun 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;

public class DefaultSliceMap extends AbstractSliceMap{

    public static SliceMap create(Contig<? extends AssembledRead> contig, QualityDataStore qualityDataStore,
                        QualityValueStrategy qualityValueStrategy){
        return new DefaultSliceMap(DefaultCoverageMap.buildCoverageMap(contig), qualityDataStore, qualityValueStrategy);
    }
    
    public static <PR extends AssembledRead> DefaultSliceMap create(CoverageMap<PR> coverageMap,QualityDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy){
        return new DefaultSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }
    private Map<Long, IdedSlice> sliceMap = new HashMap<Long, IdedSlice>();
    private long size;
    protected PhredQuality defaultQuality;
    public DefaultSliceMap(CoverageMap<? extends AssembledRead> coverageMap, 
                        QualityDataStore qualityDataStore,
                        QualityValueStrategy qualityValueStrategy){
        this(coverageMap,qualityDataStore, qualityValueStrategy,null);
    }
    protected DefaultSliceMap(CoverageMap<? extends AssembledRead> coverageMap, 
            QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, PhredQuality defaultQuality){
        this.defaultQuality = defaultQuality;
        this.size = coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd()+1;
        for(CoverageRegion<?  extends AssembledRead> region : coverageMap){
        	Range range = region.asRange();
            for(long i=range.getBegin(); i<=range.getEnd(); i++ ){
                List<IdedSliceElement> sliceElements = createSliceElementsFor(region, i, qualityDataStore, qualityValueStrategy);
                sliceMap.put(Long.valueOf(i),new DefaultSlice.Builder()
                                            .addAll(sliceElements)
                                            .build());
            
            }
        }
    }

    
    
    /**
     * @return the defaultQuality
     */
    protected PhredQuality getDefaultQuality() {
        return defaultQuality;
    }

    public DefaultSliceMap(List<IdedSlice> slices){
        size = slices.size();
        for(int i=0; i< size; i++){
            sliceMap.put(Long.valueOf(i), slices.get(i));
        }
    }
    @Override
    public IdedSlice getSlice(long offset) {
        return sliceMap.get(Long.valueOf(offset));
    }
    @Override
    public long getSize() {
        return size;
    }
    @Override
    public Iterator<IdedSlice> iterator() {
        return new SliceIterator(sliceMap.keySet().iterator(), this);
    }

    
   
}
