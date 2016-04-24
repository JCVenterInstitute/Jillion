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
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;
/**
 * {@code FastqParser} is an interface that will
 * parse a FASTQ formatted structure and call the appropriate 
 * visit methods on the given {@link FastqVisitor}.
 * @author dkatzel
 *
 */
public interface FastqParser {

	/**
	 * Can this handler accept new parse requests
	 * via parse() calls.
	 * 
	 * Some implementations of {@link FastqParser}
	 * may only allow one parse call in its lifetime 
	 * (for example, if the fastq structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	
	/**
	 * Is this {@link FastqParser}'s callbacks capable of
	 * creating {@link FastqVisitorMemento}s.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Can the {@link #parse(FastqVisitor)}
	 * or {@link #parse(FastqVisitor, FastqVisitorMemento)}
	 * methods be called multiple times.
	 * Some implementations are working off of an
	 * InputStream that can't be rewound
	 * or reset we can only read Once.
	 * @return {@code true} if the data
	 * can only be parsed once;
	 * {@code false} otherwise.
	 */
	boolean isReadOnceOnly();
	/**
	 * Parse the fastq structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link FastqVisitor}.
	 * @param visitor the {@link FastqVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the fastq.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(FastqVisitor visitor) throws IOException;
	
	
	/**
	 * Parse the fastq structure starting from 
	 * location provided by the {@link FastqVisitorMemento}
	 * and call the appropriate
	 * visit methods on the given {@link FastqVisitor}.
	 * @param visitor the {@link FastqVisitor} instance to call
	 * visit methods on; can not be null;
	 * @param memento the {@link FastqVisitorMemento} instance
	 * to tell the parser where to start parsing from;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the fastq file.
	 * @throws NullPointerException if either the visitor or memento are null.
	 * @throws UnsupportedOperationException if mementos are not supported by this
	 * parser implementation (for example when parsing an {@link InputStream}
	 * instead of a {@link File}).
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(FastqVisitor visitor, FastqVisitorMemento memento) throws IOException;
	/**
	 * Get the Fastq encoded file being parsed.
	 * 
	 * @return An {@link Optional} {@link File} if it is known.
	 * 
	 * @since 5.2
	 */
	Optional<File> getFile();
	
}
