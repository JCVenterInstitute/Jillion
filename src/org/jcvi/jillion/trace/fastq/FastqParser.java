package org.jcvi.jillion.trace.fastq;

import java.io.IOException;

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
	boolean canAccept();
	
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
	
}
