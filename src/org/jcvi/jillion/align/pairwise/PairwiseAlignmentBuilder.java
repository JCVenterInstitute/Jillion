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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.align.AminoAcidSubstitutionMatrix;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrix;
import org.jcvi.jillion.align.SubstitutionMatrix;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code PairwiseAlignmentBuilder} is a class
 * that can create {@link PairwiseSequenceAlignment}s
 * using the given query and subject sequences,
 * and the given {@link SubstitutionMatrix} and gap penalties.
 * This Builder can handle both local and global alignments.
 * 
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} (either {@link Nucleotide} or {@link AminoAcid} ). 
 * @param <S> the type of {@link Sequence} (either {@link NucleotideSequence} or {@link ProteinSequence} ).
 * @param <A> the type of {@link PairwiseSequenceAlignment} to build.
 */
public final class PairwiseAlignmentBuilder<R extends Residue, S extends ResidueSequence<R>, A extends PairwiseSequenceAlignment<R,S>> {
	private final S query,  subject;
	private final SubstitutionMatrix<R> matrix;
	private float gapOpen=0;
	private float gapExtension =0;
	
	private boolean local=true;
	/**
	 * Create a new PairwiseAlignmentBuilder to align
	 * 2 {@link NucleotideSequence}s.
	 * @param query the query sequence, any gaps
	 * in the sequence will be ignored by the alignment; may not be null.
	 * @param subject the subject sequence, any gaps
	 * in the sequence will be ignored by the alignment; may not be null.
	 * @param matrix the {@link SubstitutionMatrix}; can not be null.
	 * @return a new {@link PairwiseAlignmentBuilder} instance;
	 * will never be null.
	 * @throws NullPointerException if any parameters are null.
	 */
	public static PairwiseAlignmentBuilder<Nucleotide, NucleotideSequence, NucleotidePairwiseSequenceAlignment> createNucleotideAlignmentBuilder(NucleotideSequence query, NucleotideSequence subject, NucleotideSubstitutionMatrix matrix){
		return new PairwiseAlignmentBuilder<Nucleotide, NucleotideSequence,NucleotidePairwiseSequenceAlignment>(query, subject, matrix);
	}
	/**
	 * Create a new PairwiseAlignmentBuilder to align
	 * 2 {@link ProteinSequence}s.
	 * @param query the query sequence, any gaps
	 * in the sequence will be ignored by the alignment; may not be null.
	 * @param subject the subject sequence, any gaps
	 * in the sequence will be ignored by the alignment; may not be null.
	 * @param matrix the {@link SubstitutionMatrix}; can not be null.
	 * @return a new {@link PairwiseAlignmentBuilder} instance;
	 * will never be null.
	 * @throws NullPointerException if any parameters are null.
	 */
	public static PairwiseAlignmentBuilder<AminoAcid, ProteinSequence, ProteinPairwiseSequenceAlignment> createProtienAlignmentBuilder(ProteinSequence query, ProteinSequence subject, AminoAcidSubstitutionMatrix matrix){
		return new PairwiseAlignmentBuilder<AminoAcid, ProteinSequence, ProteinPairwiseSequenceAlignment>(query, subject, matrix);
	}
	
	private PairwiseAlignmentBuilder(S query, S subject, SubstitutionMatrix<R> matrix){
		if(query==null){
			throw new NullPointerException("query can not be null");
		}
		if(subject==null){
			throw new NullPointerException("subject can not be null");
		}
		if(matrix==null){
			throw new NullPointerException("matrix can not be null");
		}
		this.query = query;
		this.subject = subject;
		this.matrix = matrix;
	}
	/**
	 * Set the gap penalty without an extension penalty,
	 * this is the same as calling {@link #gapPenalty(float, float) gapPenalty(open, 0)}
	 * @see #gapPenalty(float, float)
	 */
	public PairwiseAlignmentBuilder<R,S,A> gapPenalty(float open){
		return gapPenalty(open, 0F);
	}
			
	/**
	 * Set the affine gap penalties to use when scoring the alignments.
	 * @param open the penalty score for creating a new gap; usually a negative number.
	 * @param extension the penalty for extending an already open gap;
	 * usually a negative number.
	 * @return this
	 */
	public PairwiseAlignmentBuilder<R,S,A> gapPenalty(float open, float extension){
		this.gapOpen = open;
		this.gapExtension = extension;
		return this;
	}
	/**
	 * Set this pairwise alignment algorithm to use a global
	 * alignment.  This will force the alignment for both sequences
	 * to go from end to end.  If not set, then the default is 
	 * a local alignment.
	 * @return this.
	 */
	public PairwiseAlignmentBuilder<R,S,A> useGlobalAlignment(){
		local=false;
		return this;
	}
	/**
	 * Set this pairwise alignment algorithm to use a local
	 * alignment.  A local alignment will only include the best
	 * subsequence alignment and will not force the entire
	 * sequence to be aligned.  If not set, this is the default
	 * alignment used.
	 * @return this.
	 */
	public PairwiseAlignmentBuilder<R,S,A> useLocalAlignment(){
		local=true;
		return this;
	}
	/**
	 * Compute the actual pairwise alignment.
	 * This method may be computationally expensive
	 * if the sequences are long.
	 * @return a new {@link PairwiseSequenceAlignment}
	 * instance, will never be null.
	 */
	@SuppressWarnings("unchecked")
	public A build(){
		//we know that we can only be either an ProteinSequence
		//or a NucleotideSequence so these casts should all be safe.
		//The casts are so the user's interface is clean
		//all ugliness is hidden here
		
		if(query instanceof NucleotideSequence){
			if(local){
				 return (A)NucleotideSmithWatermanAligner.align((NucleotideSequence)query, (NucleotideSequence)subject, (NucleotideSubstitutionMatrix)matrix, gapOpen, gapExtension);
			}
			return (A) NucleotideNeedlemanWunschAligner.align((NucleotideSequence)query, (NucleotideSequence)subject, (NucleotideSubstitutionMatrix)matrix, gapOpen, gapExtension);
		}
		if(local){
			 return (A) ProteinSmithWatermanAligner.align((ProteinSequence)query, (ProteinSequence)subject, (AminoAcidSubstitutionMatrix)matrix, gapOpen, gapExtension);
		}
		return (A) ProteinNeedlemanWunschAligner.align((ProteinSequence)query, (ProteinSequence)subject, (AminoAcidSubstitutionMatrix)matrix, gapOpen, gapExtension);

	}
	
	
	
}
