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
package org.jcvi.jillion.assembly.tigr.tasm;



/**
 * {@code TasmVisitor} is a visitor
 * interface to visit components of a single
 * TIGR Assembler (tasm) file.
 * 
 * @author dkatzel
 *
 */
public interface TasmVisitor {
	
	interface TasmVisitorCallback{
		/**
		 * {@code TasmVisitorMemento} is a marker
		 * interface that {@link TasmFileParser}
		 * instances can use to "rewind" back
		 * to the position in its contig file
		 * in order to revisit portions of the contig file. 
		 * {@link TasmVisitorMemento} should only be used
		 * by the {@link TasmFileParser} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		interface TasmVisitorMemento{
			
		}
		/**
		 * Is this callback capable of
		 * creating {@link TasmVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link TasmVisitorMemento}
		 * 
		 * @return a {@link TasmVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		TasmVisitorMemento createMemento();
		/**
		 * Tell the {@link TasmFileParser} to stop parsing
		 * the contig file this will cause {@link TasmVisitor#halted()}
		 * to be called but no other visit methods will be called.
		 */
		void halt();
	}
	
	/**
	 * A new Contig has been detected in the tasm file.
	 * @param callback an instance of {@link TasmVisitorCallback};
	 * will never be null.
	 * @param contigId the contig id of this contig.
	 * @return a {@link TasmContigVisitor} instance
	 * if this contig should be visited;
	 * if {@code null} is returned, then
	 * this contig will not be visited.
	 */
	TasmContigVisitor visitContig(TasmVisitorCallback callback, String contigId);
	
	/**
	 * The parser has stopped 
	 * parsing but has not
	 * actually finished the parsing this tasm file.
	 * This will happen only if 
	 * a visitor calls {@link TasmVisitorCallback#halt()}.
	 */
	void halted();
	/**
	 * The entire tasm file has been visited.
	 */
	void visitEnd();
}
