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

import java.io.IOException;

import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback.AceFileVisitorMemento;
/**
 * {@code AceParser} is an interface that will
 * parse an Ace structure and call the appropriate 
 * visit methods on the given {@link AceFileVisitor}.
 * @author dkatzel
 *
 */
public interface AceParser {
	/**
	 * Can this handler accept new parse requests
	 * via one of the parse() methods.
	 * calls.
	 * 
	 * Some implementations of {@link AceParser}
	 * may only allow one parse call in its lifetime 
	 * (for example, if the ace structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Parse the ace structure and call the appropriate methods on the given AceFileVisitor.
	 * @param visitor the visitor to be visited, can not be null.
	 * @throws IOException if the there is a problem reading
	 * the ace data.
	 * @throws NullPointerException if either the visitor is {@code null}.
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(AceFileVisitor visitor) throws IOException;
	/**
	 * Parse the ace structure 
	 * starting at the location in the structure
	 * that the {@link AceFileVisitorMemento}
	 * specifies, and call the appropriate methods on the given AceFileVisitor.
	 * @param visitor the visitor to be visited, can not be null.
	 * @param memento the {@link AceFileVisitorMemento} that tells this
	 * handler where to start in the ace structure.
	 * 
	 * @throws IOException if the there is a problem reading
	 * the ace data.
	 * @throws NullPointerException if either the visitor is {@code null}.
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(AceFileVisitor visitor, AceFileVisitorMemento memento)	throws IOException;

}
