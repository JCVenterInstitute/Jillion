package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion.fasta.FastaRecordVisitor;

public abstract class AbstractFastaRecordVisitor implements FastaRecordVisitor{
	private final String id;
	private final String comment;
	private final AminoAcidSequenceBuilder sequenceBuilder = new AminoAcidSequenceBuilder();
	
	
	public AbstractFastaRecordVisitor(String id, String comment) {
		this.id = id;
		this.comment = comment;
	}

	@Override
	public final void visitBodyLine(String line) {
		sequenceBuilder.append(line);
		
	}

	@Override
	public final void visitEnd() {
		AminoAcidSequenceFastaRecord record = new AminoAcidSequenceFastaRecordBuilder(id, sequenceBuilder.build())
												.comment(comment)
												.build();
		visitRecord(record);
		
	}
	
	protected abstract void visitRecord(AminoAcidSequenceFastaRecord fastaRecord);
	
}