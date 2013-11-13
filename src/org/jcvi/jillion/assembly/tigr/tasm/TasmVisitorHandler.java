package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.IOException;

import org.jcvi.jillion.assembly.tigr.tasm.TasmVisitor.TasmVisitorCallback.TasmVisitorMemento;
/**
 * {@code TasmVisitorHandler} is an interface that will
 * traverse a TIGR Assembler (tasm) formatted structure and call the appropriate 
 * visit methods on the given {@link FastaVisitor}.
 * @author dkatzel
 *
 */
public interface TasmVisitorHandler {
	/**
	 * Can this handler accept new visit requests
	 * via accept() calls.
	 * 
	 * Some implementations of {@link TasmVisitorHandler}
	 * may only allow one accept call in its lifetime 
	 * (for example, if the tasm structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new visit requests; {@code false} otherwise.
	 */
	boolean canAccept();
	
	/**
	 * Traverse the tasm structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link TasmVisitor}.
	 * @param visitor the {@link TasmVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the tasm.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(TasmVisitor visitor) throws IOException;
	
	
	/**
	 * Traverse the tasm structure starting from 
	 * location provided by the {@link TasmVisitorMemento}
	 * and call the appropriate
	 * visit methods on the given {@link TasmVisitor}.
	 * @param visitor the {@link TasmVisitor} instance to call
	 * visit methods on; can not be null;
	 * @param memento the {@link TasmVisitorMemento} instance
	 * to tell the parser where to start parsing from;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the tasm file.
	 * @throws NullPointerException if either the visitor or memento are null.
	 * @throws UnsupportedOperationException if mementos are not supported by this
	 * parser implementation (for example when parsing an {@link InputStream}
	 * instead of a {@link File}).
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(TasmVisitor visitor, TasmVisitorMemento memento) throws IOException;
	
}
