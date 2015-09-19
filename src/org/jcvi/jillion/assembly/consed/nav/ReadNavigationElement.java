/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.nav;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

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
     * @param readId the id of the target of that is to be navigated.
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
     * @param readId the id of the target of that is to be navigated.
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
     * range into an ungapped range and reverse complementing required by the consed.
     * This is the same as {@link #buildReadNavigationElement(AssembledRead, Range, int,String)
     * buildReadNavigationElementFrom(read, gappedFeatureValidRange, fullLength,null)}
     * @param read the read to make a {@link ReadNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureValidRange the gapped feature range coordinates; cannot be null.
     * @param fullLength the full length 
     * of the untrimmed read (used if needed to convert to reverse complement).
     * 
     * @return a new ReadNavigationElement.
     * 
     * 
     * @see #buildReadNavigationElement(AssembledRead, Range, int,String)
     */
    public ReadNavigationElement buildReadNavigationElement(AssembledRead read, 
            Range gappedFeatureValidRange, 
            int fullLength){
        return buildReadNavigationElement(read, gappedFeatureValidRange, fullLength,null);
    }
    /**
     * Build a new {@link ReadNavigationElement} for the given
     * PlacedRead, that will navigate to the given GAPPED range.  This
     * is a convenience method that handles converting the gapped
     * range into an ungapped range and reverse complementing required by the consed.
     * @param read the read to make a {@link ReadNavigationElement}
     * for; cannot be null.
     * @param gappedFeatureValidRange the gapped feature range coordinates; cannot be null.
     * @param comment a comment that describes why this element exists
     * (may be null).
     * @param fullLength the full length 
     * of the untrimmed read (used if needed to convert to reverse complement).
     * @return a new ReadNavigationElement.
     */
    public ReadNavigationElement buildReadNavigationElement(AssembledRead read, 
            Range gappedFeatureValidRange, 
            int fullLength,
            String comment){
        Range ungappedRange = AssemblyUtil.toUngappedRange(read.getNucleotideSequence(), gappedFeatureValidRange);
        if(read.getDirection() == Direction.REVERSE){
            ungappedRange =AssemblyUtil.reverseComplementValidRange(ungappedRange, fullLength);
        }
        return new ReadNavigationElement(read.getId(), ungappedRange, comment);
    }
   
    
    
}
