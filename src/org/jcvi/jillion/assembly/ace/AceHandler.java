package org.jcvi.jillion.assembly.ace;

import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AceFileVisitorCallback.AceFileVisitorMemento;

public interface AceHandler {

	/**
	 * Walk over the ace structure and call the appropriate methods on the given AceFileVisitor.
	 * @param visitor the visitor to be visited, can not be null.
	 * @throws IOException if the there is a problem reading
	 * the ace data.
	 * @throws NullPointerException if either the visitor is {@code null}.
	 */
	void accept(AceFileVisitor visitor) throws IOException;

	void accept(AceFileVisitor visitor, AceFileVisitorMemento memento)
			throws IOException;

}