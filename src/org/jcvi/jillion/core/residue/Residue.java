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



import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

import java.util.Set;

/**
 * @author dkatzel
 *
 *
 */
public interface Residue<R extends Residue<R>>{

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
     * Is this residue ambiguous (a representation of multiple residues)
     * @return {@code true} if it's ambiguous; {@code false} if not.
     *
     * @since 5.3.3
     */
    boolean isAmbiguity();

    /**
     * Get the non-ambiguous bases that make up this Residue.
     * For example, for the Nucleotide `N` this method would return the set of
     * `A`, `C`, `G` and `T`.
     *
     * @return a Set of non-ambiguous residues.  If this Residue itself
     * is not ambiguous, then the return value is a set containing just
     * this residue. otherwise contains the set of all the residues that
     * could make this ambiguous residue.
     *
     * @since 6.1
     */
    Set<R> getNonAmbiguousBases();

    /**
     * Is this Residue the special value to represent
     * "unknown" such as Nucleotide's N or Amino Acid's X for example.
     *  Only 1 residue per class type should
     * return {@code true} for this.
     * @return {@code true} if this residue is the value for UNKNWON.
     *
     * @since 6.1
     */
    boolean isUnknown();

    /**
     * Two Residues match if one of the Residue's
     * set of unambiguous bases
     * is a complete subset of the other.
     * For example, for Nucleotides: V (which is A,C or G) would
     * match A, C, G, M, R, S and N. However, V would not
     * match W since that could also represent a T.
     * @param other the other Residue to match.
     * @return {@code true} if this Residue matches the other given
     * residue; {@code false} otherwise.
     *
     * @since 6.1
     */
    default boolean matches(R other){
        if(other ==null){
            throw new NullPointerException("other can not be null");
        }
        if(this==other){
            return true;
        }

        Set<R> basesForOther =other.getNonAmbiguousBases();
        Set<R> basesForThis =getNonAmbiguousBases();
        return basesForThis.containsAll(basesForOther)
                    || basesForOther.containsAll(basesForThis);
    }

}
