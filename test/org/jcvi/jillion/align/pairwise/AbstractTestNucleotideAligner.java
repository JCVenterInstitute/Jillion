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

import java.util.Iterator;

import org.jcvi.jillion.align.NucleotideSubstitutionMatrix;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrixBuilder;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignmentImpl;
import org.jcvi.jillion.align.pairwise.PairwiseSequenceAlignmentWrapper;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.align.NucleotideSequenceAlignmentBuilder;

public class AbstractTestNucleotideAligner {

	protected final NucleotideSubstitutionMatrix matrix;
	public AbstractTestNucleotideAligner(){
		NucleotideSubstitutionMatrixBuilder builder = new NucleotideSubstitutionMatrixBuilder(-1F);
		builder.setMatch(2);
		matrix = builder.build();
	}
	protected NucleotidePairwiseSequenceAlignment createExpectedAlignment(String gappedSeq1, String gappedSeq2, float score){
		NucleotideSequenceAlignmentBuilder builder = new NucleotideSequenceAlignmentBuilder();
		NucleotideSequence seq1 = new NucleotideSequenceBuilder(gappedSeq1).build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder(gappedSeq2).build();
		Iterator<Nucleotide> seq1Iter = seq1.iterator();
		Iterator<Nucleotide> seq2Iter = seq2.iterator();
		
		while(seq1Iter.hasNext()){
			Nucleotide base1 = seq1Iter.next();
			Nucleotide base2 = seq2Iter.next();
			if(base1==base2){
				builder.addMatch(base1);
			}else if (base1==Nucleotide.Gap || base2 == Nucleotide.Gap){
				builder.addGap(base1,base2);
			}else{
				builder.addMismatch(base1, base2);
			}
		}
		if(seq2Iter.hasNext()){
			throw new IllegalArgumentException("seq2 is longer than seq1");
		}
		return new NucleotidePairwiseSequenceAlignmentImpl(PairwiseSequenceAlignmentWrapper.wrap(builder.build(), score));
		
	}
}
