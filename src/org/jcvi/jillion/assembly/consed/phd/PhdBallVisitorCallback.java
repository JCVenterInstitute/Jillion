/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.phd;


public interface PhdBallVisitorCallback {
	/**
	 * {@code PhdBallVisitorMemento} is a marker
	 * interface that {@link PhdBallFileParser}
	 * instances can use to "rewind" back
	 * to the position in its ace file
	 * in order to revisit portions of the ace file. 
	 * {@link PhdBallVisitorMemento} should only be used
	 * by the {@link PhdBallFileParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	interface PhdBallVisitorMemento{
		
	}
	/**
	 * Is this callback capable of
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
	 * Tell the {@link PhdBallFileParser} to stop parsing
	 * the ace file.  {@link org.jcvi.jillion.assembly.consed.ace.AceFileVisitor#halted()}
	 * will still be called.
	 */
	void haltParsing();
}
