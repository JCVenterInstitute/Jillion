package org.jcvi.common.core.seq.fastx.fasta.nt;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code NucleotideSequenceFastaRecordFactory} is a factory class
 * that makes instances of {@link NucleotideSequenceFastaRecord}s.
 * Depending on the different parameters, the factory might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceFastaRecordFactory {

	private NucleotideSequenceFastaRecordFactory(){
		//can not instantiate
	}
	/**
	 * Create a new {@link NucleotideSequenceFastaRecord}
	 * instance that has the given id and sequence and
	 * has no comment.  The returning instance's 
	 * {@link NucleotideSequenceFastaRecord#getComment()}
	 * will return null.
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 * @return a new instance, will never be null.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	public static NucleotideSequenceFastaRecord create(String id, NucleotideSequence sequence){
		return new UnCommentedNucleotideSequenceFastaRecord(id, sequence);
	}
	/**
	 * Create a new {@link NucleotideSequenceFastaRecord}
	 * instance that has the given id, sequence and comment.
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 * @param comment the comment to this fasta record, may be null 
	 * if no comment exists.
	 * @return a new instance, will never be null.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	public static NucleotideSequenceFastaRecord create(String id, NucleotideSequence sequence, String comment){
		if(comment==null){
			return create(id,sequence);
		}
		return new CommentedNucleotideSequenceFastaRecord(id, sequence,comment);
	}
}
