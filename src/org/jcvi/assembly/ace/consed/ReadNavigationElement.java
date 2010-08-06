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
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public class ReadNavigationElement extends AbstractNavigationElement{

    /**
     * @param type
     * @param elementId
     * @param ungappedPositionRange
     * @param comment
     */
    public ReadNavigationElement(String readId,
            Range ungappedPositionRange, String comment) {
        super(Type.READ, readId, ungappedPositionRange, comment);
    }
    public ReadNavigationElement(String readId,
            Range ungappedPositionRange){
        super(Type.READ, readId, ungappedPositionRange);
    }
    
    public ReadNavigationElement buildReadNavigationElementFrom(PlacedRead read, 
            Range gappedFeatureValidRange, 
            int fullLength,
            String comment){
        final NucleotideEncodedGlyphs encodedGlyphs = read.getEncodedGlyphs();
        Range ungappedRange = AssemblyUtil.convertGappedRangeIntoUngappedRange(encodedGlyphs, gappedFeatureValidRange);
        if(read.getSequenceDirection() == SequenceDirection.REVERSE){
            ungappedRange =AssemblyUtil.reverseComplimentValidRange(ungappedRange, fullLength);
        }
        return new ReadNavigationElement(read.getId(), ungappedRange, comment);
    }
   
    
    
}
