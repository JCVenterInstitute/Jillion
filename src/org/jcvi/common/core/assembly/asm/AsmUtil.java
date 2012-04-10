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

package org.jcvi.common.core.assembly.asm;

import java.util.List;

import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

/**
 * {@code AsmUtil} is a utility class for working
 * with Celera Assembler ASM encoded data.
 * @author dkatzel
 *
 *
 */
public final  class AsmUtil {
	/**
	 * Generate a gapped sequence string from the ungapped valid range
	 * sequence and the list of ASM encoded gap offsets (also known as
	 * ASM del encoding).
	 * @param ungappedSequence a List of {@link Nucleotide}s of the ungapped
	 * valid range sequence to be gapped; this sequence should already
	 * be complimented into the correct orientation.
	 * @param asmEncodedGaps the List of Integers of the ASM del encoded
	 * gaps.
	 * @return a new String representing the gapped sequence.
	 */
    public static String computeGappedSequence(List<Nucleotide> ungappedSequence, List<Integer> asmEncodedGaps){
        NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(ungappedSequence);
        for(Integer offset : asmEncodedGaps){
            builder.insert(offset.intValue(), Nucleotide.Gap);
        }
        
        return builder.toString();
    }
}
