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
package org.jcvi.jillion.assembly.consed.ace;


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
	interface AceFileVisitorMemento{
		
	}
	/**
	 * Is this callback capable of
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
