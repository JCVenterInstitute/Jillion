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
package org.jcvi.jillion.assembly.ca.asm;

import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

/**
 * {@code AsmUtil} is a utility class for working
 * with Celera Assembler ASM encoded data.
 * @author dkatzel
 *
 *
 */
public final  class AsmUtil {
	
	private AsmUtil(){
		//private constructor.
	}
	/**
	 * Add gaps to the given
	 * {@link NucleotideSequenceBuilder} which 
	 * represents an ASM read's ungapped valid range sequence.
	 * @param ungappedSequenceBuilder a {@link NucleotideSequenceBuilder} of the ungapped
	 * valid range sequence to be gapped; this sequence should already
	 * be complemented into the correct orientation.
	 * @param asmEncodedGaps the List of Integers of the ASM del encoded
	 * gaps.
	 * @return a new the same NucleotideSequenceBuilder
	 * that was passed in.
	 */
    public static NucleotideSequenceBuilder computeGappedSequence(NucleotideSequenceBuilder ungappedSequenceBuilder, List<Integer> asmEncodedGaps){
        for(Integer offset : asmEncodedGaps){
        	ungappedSequenceBuilder.insert(offset.intValue(), Nucleotide.Gap);
        }
        return ungappedSequenceBuilder;
    }
}
