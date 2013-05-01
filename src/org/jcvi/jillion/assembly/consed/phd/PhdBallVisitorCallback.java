package org.jcvi.jillion.assembly.consed.phd;


public interface PhdBallVisitorCallback {
	/**
	 * {@code PhdBallVisitorMemento} is a marker
	 * interface that {@link PhdBallParser}
	 * instances can use to "rewind" back
	 * to the position in its ace file
	 * in order to revisit portions of the ace file. 
	 * {@link PhdBallVisitorMemento} should only be used
	 * by the {@link PhdBallParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	interface PhdBallVisitorMemento{
		
	}
	/**
	 * Is this callback capabable of
	 * creating {@link PhdBallVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Create a {@link PhdBallVisitorMemento}.
	 * 
	 * @return a {@link PhdBallVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	PhdBallVisitorMemento createMemento();
	/**
	 * Tell the {@link PhdBallParser} to stop parsing
	 * the ace file.  {@link AceFileVisitor#halted()}
	 * will still be called.
	 */
	void haltParsing();
}
