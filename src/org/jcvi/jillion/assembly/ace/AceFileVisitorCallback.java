package org.jcvi.jillion.assembly.ace;


public interface AceFileVisitorCallback {
	/**
	 * {@code AceFileVisitorMemento} is a marker
	 * interface that {@link AceFileParser}
	 * instances can use to "rewind" back
	 * to the position in its ace file
	 * in order to revisit portions of the ace file. 
	 * {@link AceFileVisitorMemento} should only be used
	 * by the {@link AceFileParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	public interface AceFileVisitorMemento{
		
	}
	/**
	 * Is this callback capabable of
	 * creating {@link AceFileVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Create a {@link AceFileVisitorMemento}
	 * 
	 * @return a {@link AceFileVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	AceFileVisitorMemento createMemento();
	/**
	 * Tell the {@link AceFileParser} to stop parsing
	 * the ace file.  {@link AceFileVisitor#halted()}
	 * will still be called.
	 */
	void haltParsing();
}
