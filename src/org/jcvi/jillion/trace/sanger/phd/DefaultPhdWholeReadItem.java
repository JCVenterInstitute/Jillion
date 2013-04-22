package org.jcvi.jillion.trace.sanger.phd;

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
	
}
