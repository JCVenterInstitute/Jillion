package org.jcvi.common.core.seq.fasta.nt;

import org.jcvi.jillion.core.internal.seq.fasta.AbstractFastaRecordBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code NucleotideSequenceFastaRecordBuilder} is a builder class
 * that makes instances of {@link NucleotideSequenceFastaRecord}s.
 * Depending on the different parameters, this builder might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceFastaRecordBuilder extends AbstractFastaRecordBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord>{
	/**
	 * Create a new {@link NucleotideSequenceFastaRecordBuilder}
	 * instance that has the given id and sequence.  
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 * @return a new instance, will never be null.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	public NucleotideSequenceFastaRecordBuilder(String id,
			NucleotideSequence sequence) {
		super(id, sequence);
	}
	/**
	 * Convenience constructor that converts a String into
	 * a {@link NucleotideSequence}.  This is the same
	 * as {@link #NucleotideSequenceFastaRecordBuilder(String, NucleotideSequence)
	 * new NucleotideSequenceFastaRecordBuilder(id, new NucleotideSequenceBuilder(sequence).build())}.
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the nucleotide sequence as a string.  May contain whitespace
	 * which will get removed. can not be null.
	 * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link Nucleotide}.
     * @throws NullPointerException if either id or sequence are null.
     * @see NucleotideSequenceBuilder
	 */
	public NucleotideSequenceFastaRecordBuilder(String id,
			String sequence) {
		super(id, new NucleotideSequenceBuilder(sequence).build());
	}
	
	@Override
	protected NucleotideSequenceFastaRecord createNewInstance(String id,
			NucleotideSequence sequence, String comment) {
		if(comment==null){
			return new UnCommentedNucleotideSequenceFastaRecord(id, sequence);
		}
		return new CommentedNucleotideSequenceFastaRecord(id, sequence,comment);
	}


	
}
