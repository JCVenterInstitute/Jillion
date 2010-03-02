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
 * Created on Mar 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public abstract class ClonedContigCheckerStruct<R extends PlacedRead, C extends Placed> extends InternalSangerContigCheckerStruct<R> {
    private CoverageMap<CoverageRegion<C>> cloneCoverageMap;
    public ClonedContigCheckerStruct(Contig<R> contig,
            QualityDataStore qualityDataStore, PhredQuality qualityThreshold) {
        super(contig, qualityDataStore,qualityThreshold);
    }
    
    public synchronized CoverageMap<CoverageRegion<C>> getCloneCoverageMap(){
        if(cloneCoverageMap ==null){
            cloneCoverageMap = createCloneCoverageMap();
        }
        return cloneCoverageMap;
    }

    protected abstract CoverageMap<CoverageRegion<C>> createCloneCoverageMap();

}
