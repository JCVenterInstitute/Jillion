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

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;

/**
 * {@code NavigationElement} is an interface
 * that represents a single record of a 
 * Consed Navigation File.
 * @author dkatzel
 *
 *
 */
public interface NavigationElement {
    /**
     * There are several different types of Navigation Element
     * that tell consed what kind of feature this element is.
     * @author dkatzel
     *
     *
     */
    public enum Type{
        /**
         * This Navigation Element navigates
         * to a particular region of a single read.
         */
        READ,
        /**
         * This Navigation Element navigates
         * to a particular region of a consensus.
         */
        CONSENSUS;
    }
    /**
     * Get the {@link Type} of this element.
     * @return the Element's type; never null.
     */
    Type getType();
    /**
     * Get the ungapped position range of this 
     * element on the target coordinate system.
     * For example, if this element is a {@link Type#CONSENSUS}
     * type, then this range refers to the consensus positions;
     * if this element is a {@link Type#READ} then this range
     * refers to the read positions.
     * @return the {@link Range} of this element (probably in 
     * {@link CoordinateSystem#RESIDUE_BASED}); never null.
     */
    Range getUngappedPositionRange();
    /**
     * Get the comment that will show up in the navigation window
     * of consed that describes this element.
     * @return a String, may be null.
     */
    String getComment();
    /**
     * Get the id of the target to be navigated to
     * (usually in consed this is the contig id)
     * @return the id as a String, will never be null.
     */
    String getTargetId();
}
