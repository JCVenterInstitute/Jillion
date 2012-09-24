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
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;

abstract class AbstractSliceMapFactory<P extends AssembledRead> implements SliceMapFactory<P>{

  

    private final QualityValueStrategy qualityValueStrategy;
    /**
     * @param qualityValueStrategy
     */
    public AbstractSliceMapFactory(QualityValueStrategy qualityValueStrategy) {
        this.qualityValueStrategy = qualityValueStrategy;
    }
    @Override
    public SliceMap createNewSliceMap(
            CoverageMap<P> coverageMap,
                    QualitySequenceDataStore qualityDataStore) throws DataStoreException {
        return createNewSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }
    @Override
    public SliceMap createNewSliceMap(
            Contig<P> coverageMap,
                    QualitySequenceDataStore qualityDataStore) throws DataStoreException {
        return createNewSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }
    protected abstract  SliceMap createNewSliceMap(
            CoverageMap<P> coverageMap,
                    QualitySequenceDataStore qualityDataStore, QualityValueStrategy qualityValueStrategy) throws DataStoreException;
    
    protected abstract  SliceMap createNewSliceMap(
            Contig<P> coverageMap,
                    QualitySequenceDataStore qualityDataStore, QualityValueStrategy qualityValueStrategy) throws DataStoreException;
}
