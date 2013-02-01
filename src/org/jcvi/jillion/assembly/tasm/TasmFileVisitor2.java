package org.jcvi.jillion.assembly.tasm;


public interface TasmFileVisitor2 {
	
	interface TasmContigVisitorCallback{
		/**
		 * {@code TasmContigVisitorMemento} is a marker
		 * interface that {@link TasmContigFileParser}
		 * instances can use to "rewind" back
		 * to the position in its contig file
		 * in order to revisit portions of the contig file. 
		 * {@link TasmContigVisitorMemento} should only be used
		 * by the {@link TasmContigFileParser} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		interface TasmContigVisitorMemento{
			
		}
		/**
		 * Is this callback capable of
		 * creating {@link TasmContigVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link TasmContigVisitorMemento}
		 * 
		 * @return a {@link TasmContigVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		TasmContigVisitorMemento createMemento();
		/**
		 * Tell the {@link TasmContigFileParser} to stop parsing
		 * the tasm file.  {@link TasmContigFileVisitor#visitEnd()}
		 * will still be called.
		 */
		void stopParsing();
	}
	
	
	TasmContigVisitor visitContig(TasmContigVisitorCallback callback, String contigId);
	
	void visitIncompleteEnd();
	
	void visitEnd();
}
