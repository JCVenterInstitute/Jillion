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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jcvi.jillion.align.AminoAcidSubstitutionMatrix;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrix;
import org.jcvi.jillion.align.SubstitutionMatrix;
import org.jcvi.jillion.core.Range;
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
 * @param <S> the type of {@link org.jcvi.jillion.core.Sequence} (either {@link NucleotideSequence} or {@link ProteinSequence} ).
 * @param <A> the type of {@link PairwiseSequenceAlignment} to build.
 */
public final class PairwiseAlignmentBuilder<R extends Residue, S extends ResidueSequence<R, S, ?>, A extends PairwiseSequenceAlignment<R,S>> {
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
		return new PairwiseAlignmentBuilder<>(query, subject, matrix);
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
	public static PairwiseAlignmentBuilder<AminoAcid, ProteinSequence, ProteinPairwiseSequenceAlignment> createProteinAlignmentBuilder(ProteinSequence query, ProteinSequence subject, AminoAcidSubstitutionMatrix matrix){
		return new PairwiseAlignmentBuilder<>(query, subject, matrix);
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
	 * this is the same as calling {@link #gapPenalty(float, float) gapPenalty(open, 0)}.
	 * @param open the penalty score for creating a new gap; usually a negative number.
	 * @return this
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
	/**
         * Find several alignments.  If this Builder is configured to
         * use local alignments then this method will try to find several alignments by
         * recursively performing alignments on smaller and smaller subject sequences. 
         * <strong>Note</strong> this algorithm will find both good and poor alignments
         * so it is recommended that the returned Stream is filtered using a {@link java.util.function.Predicate}
         * that requires a minimum Score value.
         * 
         * if this Builder is configured to use global alignments, then 
         * the returned list will be of size 1 with the same result as {@link #build()}.
         * 
         * 
         * @return a Stream will never be null.
         */
	public Stream<A> findMultiple(){
            if(!local){
                return Stream.of(build());
            }
            List<A> list = new ArrayList<>();
            if(query instanceof NucleotideSequence){
                findMultiple((NucleotideSequence) subject, 0, list::add);
            }else{
                findMultiple((ProteinSequence)subject, 0, list::add);
            }
            return list.stream();
	}
	
	private void findMultiple(NucleotideSequence currentSubject, int currentShift, Consumer<A> consumer){
	    if(currentSubject.getLength()< query.getLength()){
	        return;
	    }
	    @SuppressWarnings("unchecked")
            A alignment = (A) NucleotideSmithWatermanAligner.align((NucleotideSequence)query, currentSubject, (NucleotideSubstitutionMatrix)matrix, gapOpen, gapExtension, currentShift);
	   
	    consumer.accept(alignment);
	    long endOfAlignment = alignment.getSubjectRange().getEnd()+1 - currentShift;
	    long startOfAlignment = alignment.getSubjectRange().getBegin() - currentShift;
	    //downstream
	    findMultiple(currentSubject.toBuilder()
                                .delete(Range.ofLength(endOfAlignment))
                                .build(),
                                (int)(currentShift+endOfAlignment),
                                consumer);
	    //upstream
	    findMultiple(currentSubject.toBuilder()
                    .trim(Range.ofLength(startOfAlignment))
                    .build(),
                    currentShift,
                    consumer);
	    
	}
	private void findMultiple(ProteinSequence currentSubject, int currentShift, Consumer<A> consumer){
            if(currentSubject.getLength()< query.getLength()){
                return;
            }
            @SuppressWarnings("unchecked")
            A alignment = (A) ProteinSmithWatermanAligner.align((ProteinSequence)query, currentSubject, (AminoAcidSubstitutionMatrix)matrix, gapOpen, gapExtension, currentShift);
           
            consumer.accept(alignment);
            long endOfAlignment = alignment.getSubjectRange().getEnd()+1 - currentShift;
            long startOfAlignment = alignment.getSubjectRange().getBegin() - currentShift;
            //downstream
            findMultiple(currentSubject.toBuilder()
                                .delete(Range.ofLength(endOfAlignment))
                                .build(),
                                (int)(currentShift+endOfAlignment),
                                consumer);
            //upstream
            findMultiple(currentSubject.toBuilder()
                    .trim(Range.ofLength(startOfAlignment))
                    .build(),
                    currentShift,
                    consumer);
            
        }
	
	
    /**
     * Helper method to programmatically use Local or Global Alignment.
     * 
     * @param useGlobal
     *            flag to say which kind of alignment to use. If set to
     *            {@code true}, this is the same as
     *            {@link #useGlobalAlignment()}, If set to {@code false}, this is
     *            the same as {@link #useLocalAlignment()}
     * @return this
     * 
     * @since 5.3
     */
    public PairwiseAlignmentBuilder<R,S,A> useGlobalAlignment(boolean useGlobal) {
        if(useGlobal){
            return useGlobalAlignment();
        }
        return useLocalAlignment();
    }

    /**
     * Helper method to programmatically use Local or Global Alignment.
     * 
     * @param useLocal
     *            flag to say which kind of alignment to use. If set to
     *            {@code true}, this is the same as
     *            {@link #useLocalAlignment()}, If set to {@code false}, this is
     *            the same as {@link #useGlobalAlignment()}
     * @return this
     * 
     * @since 5.3
     */
    public PairwiseAlignmentBuilder<R,S,A> useLocalAlignment(boolean useLocal) {
        if(useLocal){            
            return useLocalAlignment();
        }
        return useGlobalAlignment();
    }
	
	
}
