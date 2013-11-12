package org.jcvi.jillion.fasta;

import java.io.IOException;

import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
/**
 * {@code FastaVisitorHandler} is an interface that will
 * traverse a FASTA formatted structure and call the appropriate 
 * visit methods on the given {@link FastaVisitor}.
 * @author dkatzel
 *
 */
public interface FastaVisitorHandler {

	/**
	 * Can this handler accept new visit requests
	 * via accept() calls.
	 * 
	 * Some implementations of {@link FastaVisitorHandler}
	 * may only allow one accept call in its lifetime 
	 * (for example, if the fasta structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new visit requests; {@code false} otherwise.
	 */
	boolean canAccept();
	
	/**
	 * Traverse the fasta structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link FastaVisitor}.
	 * @param visitor the {@link FastaVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the fasta.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(FastaVisitor visitor) throws IOException;
	
	
	/**
	 * Traverse the fasta structure starting from 
	 * location provided by the {@link org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento}
	 * and call the appropriate
	 * visit methods on the given {@link FastaVisitor}.
	 * @param visitor the {@link FastaVisitor} instance to call
	 * visit methods on; can not be null;
	 * @param memento the {@link FastaVisitorMemento} instance
	 * to tell the parser where to start parsing from;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if either the visitor or memento are null.
	 * @throws UnsupportedOperationException if mementos are not supported by this
	 * parser implementation (for example when parsing an {@link InputStream}
	 * instead of a {@link File}).
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(FastaVisitor visitor, FastaVisitorMemento memento) throws IOException;
	
}
