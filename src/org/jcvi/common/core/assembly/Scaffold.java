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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import java.util.Iterator;
import java.util.Set;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;

public interface Scaffold {

    String getId();
    PlacedContig getPlacedContig(String id);

    boolean hasContig(String contigId);
    Set<PlacedContig> getPlacedContigs();
    CoverageMap<CoverageRegion<PlacedContig>> getContigCoverageMap();
    int getNumberOfContigs();
    long getLength();

    /**
     *  converts contig based coordinates into scaffold coordinates.
     */
    Range convertContigRangeToScaffoldRange(String placedContigId, Range placedContigRange);
    
    Iterator<String> getContigIds();
}
