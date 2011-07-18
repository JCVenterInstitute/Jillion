/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
/**
 * Aligner.java Created: Aug 13, 2009 - 3:54:18 PM (jsitz) Copyright 2009 J.
 * Craig Venter Institute
 */
package org.jcvi.common.experimental.align;

import org.jcvi.common.core.seq.Glyph;
import org.jcvi.common.core.seq.Sequence;


/**
 * A <code>Aligner</code> is an object capable of generating {@link Alignment}s based on a 
 * given algorithm.  Though implementations may allow for more advanced behaviors, the API 
 * is declared for a standard alignment between just two sequences.  These sequences may be
 * nucleotide sequences (with or without ambiguities), amino acid sequences, or any other 
 * sequence of data fitting the input.
 *
 * @author jsitz@jcvi.org
 */
public interface Aligner<G extends Glyph>
{
    /**
     * Align a query sequence against this 
     * 
     * @param referenceSequence The base sequence for alignment.
     * @param querySequence The sequence to align to the reference.
     * @return The {@link Alignment} of the query against the reference.
     */
    Alignment alignSequence(Sequence<G> querySequence, Sequence<G> referenceSequence);
}
