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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align.pairwise;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion_experimental.align.SequenceAlignment;
/**
 * {@code PairwiseSequenceAlignment} is a {@link SequenceAlignment}
 * between two {@link Sequence}s.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} in the sequence.
 * @param <S> the type of {@link Sequence} in this alignment.
 */
public interface PairwiseSequenceAlignment<R extends Residue, S extends Sequence<R>> extends SequenceAlignment<R, S> {
	/**
	 * Get the score of this alignment that 
	 * was computed from the {@link ScoringMatrix}
	 * used to make the alignment.
	 * @return the score as a float depending
	 * on the type of alignment and values
	 * of the scoring matrix this could negative.
	 */
	float getScore();
}
