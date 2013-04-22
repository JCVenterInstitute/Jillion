package org.jcvi.jillion.trace.sanger.phd;

import java.util.Date;

import org.jcvi.jillion.core.Range;

public abstract class AbstractPhdReadTagVisitor implements PhdReadTagVisitor2{

	private String type;
	private String source;
	private Range ungappedRange;
	private Date date;
	private String comment;
	private StringBuilder freeFormDataBuilder;
	
	@Override
	public void visitType(String type) {
		this.type = type;
	}

	@Override
	public void visitSource(String source) {
		this.source = source;
		
	}

	@Override
	public void visitUngappedRange(Range ungappedRange) {
		this.ungappedRange = ungappedRange;
	}

	@Override
	public void visitDate(Date date) {
		this.date = new Date(date.getTime());		
	}

	@Override
	public void visitComment(String comment) {
		this.comment=comment;
		
	}

	@Override
	public void visitFreeFormData(String data) {
		this.freeFormDataBuilder.append(data);
		
	}

	@Override
	public void visitEnd() {
		visitPhdReadTag(type, source,ungappedRange, date, comment, freeFormDataBuilder.toString());
		
	}

	protected abstract void visitPhdReadTag(String type, String source,
			Range ungappedRange, Date date, String comment, String freeFormData);
	
	@Override
	public void halted() {
		//no-op		
	}

}
