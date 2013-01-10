package org.jcvi.common.core.seq.trace.sff.visitor;

public interface SffParserCallback<T> {

	long getPosition();
	
	T stopParsing();
}
