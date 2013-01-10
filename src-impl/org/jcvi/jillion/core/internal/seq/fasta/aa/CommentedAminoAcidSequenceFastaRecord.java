package org.jcvi.jillion.core.internal.seq.fasta.aa;

import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

public class CommentedAminoAcidSequenceFastaRecord extends UnCommentedAminoAcidSequenceFastaRecord{

	private final String comments;
	
	public CommentedAminoAcidSequenceFastaRecord(String id,
			AminoAcidSequence sequence, String comments) {
		super(id, sequence);
		this.comments = comments;
	}

	@Override
	public String getComment() {
		return comments;
	}

	@Override
	public int hashCode() {
		//delegate to super since comments aren't taken into account
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		//delegate to super since comments aren't taken into account
		return super.equals(obj);
	}

	
}
