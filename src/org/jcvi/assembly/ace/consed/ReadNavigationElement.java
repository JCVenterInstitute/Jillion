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
 * {@code ReadNavigationElement} is a {@link NavigationElement}
 * that tells consed how to navigate to a particular feature 
 * of a specific read.
 * @author dkatzel
 *
 *
 */
public class ReadNavigationElement extends AbstractNavigationElement{

    /**
     * Constructs a new {@link ReadNavigationElement}.
     * @param targetId the id of the target of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     */
    public ReadNavigationElement(String readId,
            Range ungappedPositionRange, String comment) {
        super(Type.READ, readId, ungappedPositionRange, comment);
    }
    /**
     * Constructs a new {@link ReadNavigationElement}.
     * @param targetId the id of the target of that is to be navigated.
     * @param ungappedPositionRange the ungapped position
     * range of this element; cannot be null.
     * @throws NullPointerException if type, targetId or 
     * ungappedPositionRange are null.
     */
    public ReadNavigationElement(String readId,
            Range ungappedPositionRange){
        super(Type.READ, readId, ungappedPositionRange);
    }
    /**
     * Build a new {@link ReadNavigationElement} for the given
     * PlacedRead, that will navigate to the given GAPPED range.  This
     * is a convenience method that handles converting the gapped
     * range into an ungapped range and reverse complimenting required by the consed.
     * This is the same as {@link #buildReadNavigationElementFrom(PlacedRead, Range, int,String)
     * buildReadNavigationElementFrom(read, gappedFeatureValidRange, fullLength,null)}
     * @param read the read to make a {@link ReadNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureRange the gapped feature range coordinates; cannot be null.
     * @return a new ReadNavigationElement.
     * @see #buildReadNavigationElementFrom(PlacedRead, Range, int,String)
     */
    public ReadNavigationElement buildReadNavigationElementFrom(PlacedRead read, 
            Range gappedFeatureValidRange, 
            int fullLength){
        return buildReadNavigationElementFrom(read, gappedFeatureValidRange, fullLength,null);
    }
    /**
     * Build a new {@link ReadNavigationElement} for the given
     * PlacedRead, that will navigate to the given GAPPED range.  This
     * is a convenience method that handles converting the gapped
     * range into an ungapped range and reverse complimenting required by the consed.
     * @param read the read to make a {@link ReadNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureRange the gapped feature range coordinates; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @return a new ReadNavigationElement.
     */
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
