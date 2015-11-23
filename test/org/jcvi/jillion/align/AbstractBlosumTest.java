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
package org.jcvi.jillion.align;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.align.pairwise.ProteinPairwiseSequenceAlignment;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.junit.Test;

public abstract class AbstractBlosumTest {

	private final AminoAcidSubstitutionMatrix matrix;

	public AbstractBlosumTest(AminoAcidSubstitutionMatrix matrix) {
		this.matrix = matrix;
	}
	
	protected AminoAcidSubstitutionMatrix getMatrix() {
		return matrix;
	}

	@Test
	public abstract void spotCheck();
	
	@Test
	public void hasSequencesHaveStopCodon(){
		ProteinSequence seq1 = new ProteinSequenceBuilder("LSGIREE*")
									.build();
		
		
		ProteinPairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(seq1, seq1, matrix)
															.gapPenalty(-1, -2)	
															.useGlobalAlignment()
															.build();
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq1,alignment.getGappedSubjectAlignment());
	
	}
	@Test
	public void alignSimilarSequences(){
		ProteinSequence seq1 = new ProteinSequenceBuilder("LSGIREE*")
									.build();
		ProteinSequence seq2 = new ProteinSequenceBuilder("LSGVREE*")
									.build();
		
		ProteinPairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(seq1, seq2, matrix)
																	.gapPenalty(-1, -2)	
																	.useGlobalAlignment()
																	.build();
		
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq2,alignment.getGappedSubjectAlignment());
	
	}
	
	@Test
	public void PyrrolysinematchesLysine(){
		for(AminoAcid a : AminoAcid.values()){
			float p =matrix.getValue(a, AminoAcid.Pyrrolysine);
			float k =matrix.getValue(a, AminoAcid.Lysine);
			
			assertEquals(a.getCharacter().toString(), p, k, 0.0001F);
		}
	}
}
