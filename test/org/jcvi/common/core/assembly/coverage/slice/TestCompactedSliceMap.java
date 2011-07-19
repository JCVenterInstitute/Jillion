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

package org.jcvi.common.core.assembly.coverage.slice;

import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.QualityValueStrategy;
import org.jcvi.common.core.assembly.contig.slice.CompactedSliceMap;
import org.jcvi.common.core.assembly.contig.slice.SliceMap;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.qual.QualityDataStore;

/**
 * @author dkatzel
 *
 *
 */
public class TestCompactedSliceMap extends AbstractTestSliceMap{

    /**
    * {@inheritDoc}
    */
    @Override
    protected SliceMap createSliceMapFor(Contig<PlacedRead> contig,
            QualityDataStore qualityDatastore,
            QualityValueStrategy qualityValueStrategy) {

        try {
            return CompactedSliceMap.create(contig, qualityDatastore, qualityValueStrategy);
        } catch (DataStoreException e) {
            throw new IllegalStateException("error creating compacted sliceMap",e);
        }
    }

}
