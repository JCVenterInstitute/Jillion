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
package org.jcvi.jillion.assembly.tigr.contig;

import java.io.IOException;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileVisitor.TigrContigVisitorCallback.TigrContigVisitorMemento;

/**
 * {@code TigrContigParser} is an interface that will
 * traverse a TIGR Contig formatted structure and call the appropriate 
 * visit methods on the given {@link TigrContigFileVisitor}.
 * @author dkatzel
 *
 */
public interface TigrContigParser {

	/**
	 * Can this handler accept new parse requests
	 * via parse() calls.
	 * 
	 * Some implementations of {@link TigrContigParser}
	 * may only allow one accept call in its lifetime 
	 * (for example, if the contig structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this parser can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	
	/**
	 * Traverse the contig structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link TigrContigFileVisitor}.
	 * @param visitor the {@link TigrContigFileVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the contig(s).
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canParse()
	 */
	void parse(TigrContigFileVisitor visitor) throws IOException;
	
	
	/**
	 * Traverse the contig structure starting from 
	 * location provided by the {@link TigrContigVisitorMemento}
	 * and call the appropriate
	 * visit methods on the given {@link TigrContigFileVisitor}.
	 * @param visitor the {@link TigrContigFileVisitor} instance to call
	 * visit methods on; can not be null;
	 * @param memento the {@link TigrContigVisitorMemento} instance
	 * to tell the parser where to start parsing from;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the contig file.
	 * @throws NullPointerException if either the visitor or memento are null.
	 * @throws UnsupportedOperationException if mementos are not supported by this
	 * parser implementation for example when parsing an {@link java.io.InputStream}
	 * instead of a {@link java.io.File}.
	 * @throws IllegalStateException if this parser can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(TigrContigFileVisitor visitor, TigrContigVisitorMemento memento) throws IOException;
	
}
