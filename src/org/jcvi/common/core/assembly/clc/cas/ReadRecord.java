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

package org.jcvi.common.core.assembly.clc.cas;

import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * {@code ReadRecord} is a marker interface
 * to represent a single read in a cas file
 * @author dkatzel
 *
 *
 */
public interface ReadRecord {
    /**
     * Get the id of this read record.
     * @return the id (external id) should never
     * be null.
     */
    String getId();
    /**
     * Get the ungapped full length basecalls
     * used in as input to the CLC
     * Assembler to generate the cas file.
     * @return a {@link NucleotideSequence}, should
     * never be null.
     */
    NucleotideSequence getBasecalls();
}
