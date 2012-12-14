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
/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * A {@code Trace} is an abstraction of a piece of
 * genomic sequence data that has been
 * sequenced on some kind of sequencing machine.
 * @author dkatzel
 *
 *
 */
public interface Trace{
	/**
     * Get the id of this read.
     * @return the id as a String; will never be null.
     */
    String getId();
    /**
     * Get the {@link NucleotideSequence} of this read.
     * @return the {@link NucleotideSequence} of this read; will
     * never be null.
     */
    NucleotideSequence getNucleotideSequence();
    /**
     * Get the quality data of this trace as a {@link QualitySequence}.
     * @return a {@link QualitySequence}, should never be null.
     */
    QualitySequence getQualitySequence();
}
