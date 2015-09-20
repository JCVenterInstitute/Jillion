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
package org.jcvi.jillion;

import org.jcvi.jillion.align.NucleotideSubstitutionMatrices;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrix;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class AlignWholeSubstring {

	public static void main(String[] args) {
		NucleotideSequence A = new NucleotideSequenceBuilder("AATCGGATATAG").build();
		NucleotideSequence B = new NucleotideSequenceBuilder("CGATA").build();
		
		NucleotideSubstitutionMatrix matrix = NucleotideSubstitutionMatrices.getNuc44();
		
		NucleotidePairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(A,B, matrix)
																			.gapPenalty(0)
																			.build();
		
		System.out.println(alignment);
	}

}
