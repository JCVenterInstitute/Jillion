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
package org.jcvi.jillion.core.residue;


/**
 * @author dkatzel
 *
 *
 */
public interface Residue{

	byte getOrdinalAsByte();
	
	 /**
     * Return the single Character equivalent of this
     * {@link Residue}.  
     * @return the Character equivalent of this.
     */
	 Character getCharacter();
    /**
     * Is this Residue a gap?
     * @return {@code true} if it is a gap;
     * {@code false} otherwise.
     */
    boolean isGap();

    /**
     * Is this residue ambigiuous (a representation of multiple residues)
     * @return {@code true} if it's ambgious; {@code false} if not.
     *
     * @since 5.3.3
     */
    boolean isAmbiguity();
}
