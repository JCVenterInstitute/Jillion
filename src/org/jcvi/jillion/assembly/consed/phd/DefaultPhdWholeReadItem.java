package org.jcvi.jillion.assembly.consed.phd;

import java.util.Collections;
import java.util.List;

class DefaultPhdWholeReadItem implements PhdWholeReadItem{

	private final List<String> lines;

	public DefaultPhdWholeReadItem(List<String> lines) {
		if(lines ==null){
			throw new NullPointerException("lines can not be null");
		}
		this.lines = Collections.unmodifiableList(lines);
	}

	@Override
	public List<String> getLines() {
		return lines;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lines == null) ? 0 : lines.hashCode());
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
		if (!(obj instanceof DefaultPhdWholeReadItem)) {
			return false;
		}
		DefaultPhdWholeReadItem other = (DefaultPhdWholeReadItem) obj;
		if (lines == null) {
			if (other.lines != null) {
				return false;
			}
		} else if (!lines.equals(other.lines)) {
			return false;
		}
		return true;
	}
	
}
