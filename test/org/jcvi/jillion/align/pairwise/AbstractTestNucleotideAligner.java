package org.jcvi.jillion.align.pairwise;

import java.util.Iterator;

import org.jcvi.jillion.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.jillion.align.pairwise.DefaultNucleotideScoringMatrix;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignmentImpl;
import org.jcvi.jillion.align.pairwise.NucleotideScoringMatrix;
import org.jcvi.jillion.align.pairwise.PairwiseSequenceAlignmentWrapper;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class AbstractTestNucleotideAligner {

	protected final NucleotideScoringMatrix matrix;
	public AbstractTestNucleotideAligner(){
		DefaultNucleotideScoringMatrix.Builder builder = new DefaultNucleotideScoringMatrix.Builder(-1F);
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
