/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align.pairwise;

import org.jcvi.jillion.core.residue.Residue;

/**
 * {@code ScoringMatrix} is a matrix 
 * that describes a score assigned to each possible
 * pairing of Residue.  Types of Scoring matrices might
 * include distance matrices, substitution matrices etc.
 * 
 * @author dkatzel
 */
public interface ScoringMatrix<R extends Residue> {
	/**
	 * Get the score between the given pair of 
	 * {@link Residue}s.
	 * @param a the first residue.
	 * @param b the second residue.
	 * @return the score as a float, could be positive,
	 * negative, zero, whole numbers or fractional numbers
	 * depending on which type of matrix is used and 
	 * the values in that matrix.
	 */
	float getScore(R a, R b);
}
