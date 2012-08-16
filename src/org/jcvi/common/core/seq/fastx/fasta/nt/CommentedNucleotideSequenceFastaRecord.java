package org.jcvi.common.core.seq.fastx.fasta.nt;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public class CommentedNucleotideSequenceFastaRecord extends UnCommentedNucleotideSequenceFastaRecord{

	private final String comment;
	public CommentedNucleotideSequenceFastaRecord(String id,
			NucleotideSequence sequence, String comment) {
		super(id, sequence);
		this.comment = comment;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public int hashCode() {
		//delegating to super since comment doesn't impact
		//equality checks
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		//delegating to super since comment doesn't impact
		//equality checks
		return super.equals(obj);
	}
	
	

}
