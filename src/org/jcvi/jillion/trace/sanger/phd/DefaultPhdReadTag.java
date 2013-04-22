package org.jcvi.jillion.trace.sanger.phd;

import java.util.Date;

import org.jcvi.jillion.core.Range;

class DefaultPhdReadTag implements PhdReadTag {
	
	private final String type;
	private final String source;
	private final Range ungappedRange;
	private final Date date;
	private final String comment;
	private final String freeFormData;
	
	
	
	public DefaultPhdReadTag(String type, String source, Range ungappedRange,
			Date date, String comment, String freeFormData) {
		this.type = type;
		this.source = source;
		this.ungappedRange = ungappedRange;
		this.date = date;
		this.comment = comment;
		this.freeFormData = freeFormData;
	}
	
	
	@Override
	public final String getType() {
		return type;
	}
	@Override
	public final String getSource() {
		return source;
	}
	@Override
	public final Range getUngappedRange() {
		return ungappedRange;
	}
	@Override
	public final Date getDate() {
		return date;
	}
	@Override
	public final String getComment() {
		return comment;
	}
	@Override
	public final String getFreeFormData() {
		return freeFormData;
	}
	
	
	
	
}
