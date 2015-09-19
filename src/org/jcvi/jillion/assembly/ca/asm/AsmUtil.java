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
