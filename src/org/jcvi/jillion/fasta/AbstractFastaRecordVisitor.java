package org.jcvi.jillion.fasta;


public abstract class AbstractFastaRecordVisitor implements FastaRecordVisitor{
	private final String id;
	private final String comment;
	private final StringBuilder sequenceBuilder = new StringBuilder();
	
	
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
		visitRecord(id,comment,sequenceBuilder.toString());		
	}
	
	protected abstract void visitRecord(String id, String optionalComment, String fullBody);

}
