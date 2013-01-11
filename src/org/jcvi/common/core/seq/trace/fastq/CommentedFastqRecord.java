package org.jcvi.common.core.seq.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

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
