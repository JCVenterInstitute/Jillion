package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.internal.seq.fasta.AbstractFastaRecordBuilder;
import org.jcvi.jillion.core.internal.seq.fasta.aa.CommentedAminoAcidSequenceFastaRecord;
import org.jcvi.jillion.core.internal.seq.fasta.aa.UnCommentedAminoAcidSequenceFastaRecord;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
/**
 * {@code AminoAcidSequenceFastaRecordBuilder} is a Builder class
 * that makes instances of {@link AminoAcidSequenceFastaRecord}s.
 * Depending on the different parameters, this builder might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class AminoAcidSequenceFastaRecordBuilder extends AbstractFastaRecordBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord>{
	/**
	 * Convenience constructor that converts a String into
	 * a {@link AminoAcidSequence}.  This is the same
	 * as {@link #AminoAcidSequenceBuilder(String, AminoAcidSequence)
	 * new AminoAcidSequenceBuilder(id, new AminoAcidSequenceBuilder(sequence).build())}.
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the amino acid sequence as a string.  May contain whitespace
	 * which will get removed. can not be null.
	 * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link AminoAcid}.
     * @throws NullPointerException if either id or sequence are null.
     * @see AminoAcidSequenceBuilder
	 */
	public AminoAcidSequenceFastaRecordBuilder(String id,
			String sequence) {
		this(id, new AminoAcidSequenceBuilder(sequence).build());
	}
	/**
	 * Create a new {@link AminoAcidSequenceFastaRecordBuilder}
	 * instance that has the given id and sequence.  
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 * @return a new instance, will never be null.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	public AminoAcidSequenceFastaRecordBuilder(String id,
			AminoAcidSequence sequence) {
		super(id, sequence);
	}

	@Override
	protected AminoAcidSequenceFastaRecord createNewInstance(String id,
			AminoAcidSequence sequence, String optionalComment) {
		if(optionalComment==null){
			return new UnCommentedAminoAcidSequenceFastaRecord(id, sequence);
		}
		return new CommentedAminoAcidSequenceFastaRecord(id, sequence,optionalComment);
	
	}
}
