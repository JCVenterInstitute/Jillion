package org.jcvi.common.core.seq.fasta.nt;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

class CommentedNucleotideSequenceFastaRecord extends UnCommentedNucleotideSequenceFastaRecord{

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
