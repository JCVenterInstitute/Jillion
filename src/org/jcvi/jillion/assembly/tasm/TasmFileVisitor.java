package org.jcvi.jillion.assembly.tasm;




public interface TasmFileVisitor {
	
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
		 * Tell the {@link TasmFileParser} to stop parsing
		 * the contig file this will cause {@link TasmFileVisitor#halted()}
		 * to be called but no other visit methods will be called.
		 */
		void halt();
	}
	
	/**
	 * A new Contig has been detected in the tasm file.
	 * @param callback an instance of {@link TasmContigVisitorCallback};
	 * will never be null.
	 * @param contigId the contig id of this contig.
	 * @return a {@link TasmContigVisitor} instance
	 * if this contig should be visited;
	 * if {@code null} is returned, then
	 * this contig will not be visited.
	 */
	TasmContigVisitor visitContig(TasmContigVisitorCallback callback, String contigId);
	
	/**
	 * The parser has stopped 
	 * parsing but has not
	 * actually finished the parsing this tasm file.
	 * This will happen only if 
	 * a visitor calls {@link TasmContigVisitorCallback#haltParsing()}.
	 */
	void halted();
	/**
	 * The entire tasm file has been visited.
	 */
	void visitEnd();
}
