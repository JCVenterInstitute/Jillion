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
package org.jcvi.jillion.align.pairwise;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.align.AminoAcidSubstitutionMatrix;
import org.jcvi.jillion.align.BlosumMatrices;
import org.jcvi.jillion.align.pairwise.ProteinPairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.ProteinPairwiseSequenceAlignmentImpl;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.align.pairwise.PairwiseSequenceAlignmentWrapper;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.internal.align.ProteinSequenceAlignmentBuilder;
import org.junit.Test;
public class TestProteinSmithWaterman {

	@Test
	public void exampleFromBook(){
		AminoAcidSubstitutionMatrix blosom50 = BlosumMatrices.blosum50();
		ProteinSequence subject = new ProteinSequenceBuilder("HEAGAWGHEE")
									.build();
		ProteinSequence query = new ProteinSequenceBuilder("PAWHEAE")
										.build();
		ProteinPairwiseSequenceAlignment expected = 
				new ProteinPairwiseSequenceAlignmentImpl(PairwiseSequenceAlignmentWrapper.wrap(new ProteinSequenceAlignmentBuilder()
												.addMatches("AW")
												.addGap('-', 'G')
												.addMatches("HE")
												.setAlignmentOffsets(1, 4)
												.build(),
												28));
		
		ProteinPairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(query, subject, blosom50)
														.gapPenalty(-8, -6)														
														.build();
				
				assertEquals(expected, actual);
	}
}
