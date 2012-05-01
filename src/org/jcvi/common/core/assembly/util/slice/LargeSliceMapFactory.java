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
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice;

import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.symbol.qual.QualityDataStore;

public class LargeSliceMapFactory<P extends AssembledRead, R extends CoverageRegion<P>, M extends CoverageMap<R>> extends AbstractSliceMapFactory<P,R,M>{

    private final int cacheSize;
    public LargeSliceMapFactory(QualityValueStrategy qualityValueStrategy,int cacheSize) {
        super(qualityValueStrategy);
        this.cacheSize = cacheSize;
    }
    public LargeSliceMapFactory(QualityValueStrategy qualityValueStrategy){
        this(qualityValueStrategy, LargeSliceMap.DEFAULT_CACHE_SIZE);
    }

    @Override
    protected SliceMap createNewSliceMap(
            M coverageMap,
                    QualityDataStore qualityDataStore, QualityValueStrategy qualityValueStrategy){
        return LargeSliceMap.<P,R,M>create(coverageMap, qualityDataStore, qualityValueStrategy, cacheSize);
    }

}
