package org.jcvi.jillion.assembly.consed.phd;

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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((freeFormData == null) ? 0 : freeFormData.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((ungappedRange == null) ? 0 : ungappedRange.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DefaultPhdReadTag)) {
			return false;
		}
		DefaultPhdReadTag other = (DefaultPhdReadTag) obj;
		if (comment == null) {
			if (other.comment != null) {
				return false;
			}
		} else if (!comment.equals(other.comment)) {
			return false;
		}
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (freeFormData == null) {
			if (other.freeFormData != null) {
				return false;
			}
		} else if (!freeFormData.equals(other.freeFormData)) {
			return false;
		}
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!source.equals(other.source)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (ungappedRange == null) {
			if (other.ungappedRange != null) {
				return false;
			}
		} else if (!ungappedRange.equals(other.ungappedRange)) {
			return false;
		}
		return true;
	}
	
	
	
	
}
