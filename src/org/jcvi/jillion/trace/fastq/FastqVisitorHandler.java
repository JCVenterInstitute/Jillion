package org.jcvi.jillion.trace.fastq;

import java.io.IOException;

import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;
/**
 * {@code FastqVisitorHandler} is an interface that will
 * traverse a FASTQ formatted structure and call the appropriate 
 * visit methods on the given {@link FastqVisitor}.
 * @author dkatzel
 *
 */
public interface FastqVisitorHandler {

	/**
	 * Can this handler accept new visit requests
	 * via accept() calls.
	 * 
	 * Some implementations of {@link FastqVisitorHandler}
	 * may only allow one accept call in its lifetime 
	 * (for example, if the fastq structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new visit requests; {@code false} otherwise.
	 */
	boolean canAccept();
	
	/**
	 * Traverse the fastq structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link FastqVisitor}.
	 * @param visitor the {@link FastqVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the fastq.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(FastqVisitor visitor) throws IOException;
	
	
	/**
	 * Traverse the fastq structure starting from 
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
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(FastqVisitor visitor, FastqVisitorMemento memento) throws IOException;
	
}
