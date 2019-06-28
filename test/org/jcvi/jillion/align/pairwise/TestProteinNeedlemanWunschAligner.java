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

import java.util.Iterator;

import org.jcvi.jillion.align.AminoAcidSubstitutionMatrix;
import org.jcvi.jillion.align.BlosumMatrices;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.internal.align.ProteinSequenceAlignmentBuilder;
import org.junit.Test;

public class TestProteinNeedlemanWunschAligner {

	@Test
	public void exampleFromBook(){
		AminoAcidSubstitutionMatrix blosom50 = BlosumMatrices.blosum50();
		ProteinSequence subject = new ProteinSequenceBuilder("HEAGAWGHEE")
									.build();
		ProteinSequence query = new ProteinSequenceBuilder("PAWHEAE")
										.build();
		ProteinPairwiseSequenceAlignment expected = createExpectedAlignment("--P-AW-HEAE","HEAGAWGHE-E", 1F);
		
		
		ProteinPairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createProteinAlignmentBuilder(query, subject, blosom50)
															.gapPenalty(-8, -8)	
															.useGlobalAlignment()
															.build();
	
		assertEquals(expected, actual);
	}
	
	protected ProteinPairwiseSequenceAlignment createExpectedAlignment(String gappedSeq1, String gappedSeq2, float score){
		ProteinSequenceAlignmentBuilder builder = new ProteinSequenceAlignmentBuilder();
		ProteinSequence seq1 = new ProteinSequenceBuilder(gappedSeq1).build();
		ProteinSequence seq2 = new ProteinSequenceBuilder(gappedSeq2).build();
		Iterator<AminoAcid> seq1Iter = seq1.iterator();
		Iterator<AminoAcid> seq2Iter = seq2.iterator();
		
		while(seq1Iter.hasNext()){
			AminoAcid base1 = seq1Iter.next();
			AminoAcid base2 = seq2Iter.next();
			if(base1==base2){
				builder.addMatch(base1);
			}else if (base1==AminoAcid.Gap || base2 == AminoAcid.Gap){
				builder.addGap(base1,base2);
			}else{
				builder.addMismatch(base1, base2);
			}
		}
		if(seq2Iter.hasNext()){
			throw new IllegalArgumentException("seq2 is longer than seq1");
		}
		return new ProteinPairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(builder.build(), score));
		
	}
}
