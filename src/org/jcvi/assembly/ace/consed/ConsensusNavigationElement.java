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

package org.jcvi.assembly.ace.consed;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.Contig;

/**
 * @author dkatzel
 *
 *
 */
public class ConsensusNavigationElement extends AbstractNavigationElement{

    /**
     * @param type
     * @param elementId
     * @param ungappedPositionRange
     * @param comment
     */
    public ConsensusNavigationElement(String contigId,
            Range ungappedPositionRange, String comment) {
        super(Type.CONSENSUS, contigId, ungappedPositionRange, comment);
    }

    public ConsensusNavigationElement(String contigId,
            Range ungappedPositionRange){
        super(Type.CONSENSUS, contigId, ungappedPositionRange);
    }
    
    public static <C extends Contig<?>> ConsensusNavigationElement buildConsensusNavigationElement(C contig,
            Range gappedFeatureRange){
        return buildConsensusNavigationElement(contig, gappedFeatureRange,null);
    }
    
    public static <C extends Contig<?>> ConsensusNavigationElement buildConsensusNavigationElement(C contig,
            Range gappedFeatureRange,String comment){
        Range ungappedRange = AssemblyUtil.convertGappedRangeIntoUngappedRange(contig.getConsensus(), gappedFeatureRange);
        return new ConsensusNavigationElement(contig.getId(), ungappedRange,comment);
    }
}
