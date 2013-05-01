package org.jcvi.jillion.assembly.consed.phd;

import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * {@code AbstractPhdReadTagVisitor} is a {@link PhdReadTagVisitor}
 * that collects the information about a single read tag
 * and then calls {@link #visitPhdReadTag(String, String, Range, Date, String, String)}
 * when the entire tag has been visited (this is known because {@link #visitEnd()}
 * as been called).
 * Subclasses are required to implement the abstract class 
 * {@link #visitPhdReadTag(String, String, Range, Date, String, String)}
 * to handle the completely visited read tag.
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractPhdReadTagVisitor implements PhdReadTagVisitor{

	private String type;
	private String source;
	private Range ungappedRange;
	private Date date;
	private String comment;
	private StringBuilder freeFormDataBuilder = new StringBuilder();
	
	@Override
	public final void visitType(String type) {
		this.type = type;
	}

	@Override
	public final void visitSource(String source) {
		this.source = source;
		
	}

	@Override
	public final void visitUngappedRange(Range ungappedRange) {
		this.ungappedRange = ungappedRange;
	}

	@Override
	public final void visitDate(Date date) {
		this.date = new Date(date.getTime());		
	}

	@Override
	public final void visitComment(String comment) {
		this.comment=comment;
		
	}

	@Override
	public final void visitFreeFormData(String data) {
		this.freeFormDataBuilder.append(data);
		
	}

	@Override
	public final void visitEnd() {
		final String freeFormData;
		if(freeFormDataBuilder.length() ==0){
			//no free form data
			freeFormData =null;
		}else{
			freeFormData= freeFormDataBuilder.toString();
		}
		visitPhdReadTag(type, source,ungappedRange, date, comment, freeFormData);
		
	}
	/**
	 * 
	 * @param type
	 * @param source
	 * @param ungappedRange
	 * @param date
	 * @param comment
	 * @param freeFormData
	 */
	protected abstract void visitPhdReadTag(String type, String source,
			Range ungappedRange, Date date, String comment, String freeFormData);
	/**
	 * Ignored by default, please
	 * override to get halted notification.
	 * {@inheritDoc}
	 */
	@Override
	public void halted() {
		//no-op		
	}

}
