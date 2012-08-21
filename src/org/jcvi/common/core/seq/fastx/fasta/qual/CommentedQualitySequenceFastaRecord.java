package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.symbol.qual.QualitySequence;

class CommentedQualitySequenceFastaRecord extends UncommentedQualitySequenceFastaRecord{
	private final String comment;
	public CommentedQualitySequenceFastaRecord(String id,
			QualitySequence qualities, String comment) {
		super(id, qualities);
		this.comment = comment;
	}
	@Override
	public String getComment() {
		return comment;
	}

	
	
}
