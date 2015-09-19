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
package org.jcvi.jillion.align;

import org.jcvi.jillion.core.residue.Residue;

/**
 * {@code SubstitutionMatrix} is a matrix 
 * that describes the rate at which
 * one Residue changes into another
 * over time.  Substitution matrices are used
 * in bioinformatics and evolutionary biology
 * to determine how how likely sequences are 
 * to be derived from a common ancestor (homologous).
 * 
 * @author dkatzel
 */
public interface SubstitutionMatrix<R extends Residue> {
	/**
	 * Get the substitution value between the given pair of 
	 * {@link Residue}s.
	 * @param a the first residue.
	 * @param b the second residue.
	 * @return the score as a float, could be positive,
	 * negative, zero, whole numbers or fractional numbers.
	 */
	float getValue(R a, R b);
}
