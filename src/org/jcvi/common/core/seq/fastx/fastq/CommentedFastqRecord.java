package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

class CommentedFastqRecord extends UncommentedFastqRecord{

	private final String comment;
	public CommentedFastqRecord(String id, NucleotideSequence nucleotides,
			QualitySequence qualities, String comment) {
		super(id, nucleotides, qualities);
		this.comment = comment;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public int hashCode() {
		// superclass already uses getComment()
		//so we can just delegate to super
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		// superclass already uses getComment()
		//so we can just delegate to super
		return super.equals(obj);
	}
	
	

}
