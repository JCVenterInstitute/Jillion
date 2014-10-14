/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TranslationTable {

	/**
	 * Convenience method for {@link #translate(NucleotideSequence, Frame)}
	 * using {@link Frame#ZERO}.
	 * @param sequence
	 * @return
	 */
	ProteinSequence translate(NucleotideSequence sequence);
	
	/**
	 * Convenience method for {@link #translate(NucleotideSequence, Frame)}
	 * using {@link Frame#ZERO}.
	 * @param sequence
	 * @return
	 */
	ProteinSequence translate(NucleotideSequence sequence, boolean substituteStart);
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(NucleotideSequence sequence, Frame frame, boolean substituteStart);
	
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(NucleotideSequence sequence, Frame frame);
	
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @param length the number of elements in the given sequence
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(Iterable<Nucleotide> sequence, Frame frame, int length);
	
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @param length the number of elements in the given sequence
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(Iterable<Nucleotide> sequence, Frame frame, int length, boolean substituteStarts);
}
