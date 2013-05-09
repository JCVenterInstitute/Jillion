/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.tasm;




public interface TasmFileVisitor {
	
	interface TasmContigVisitorCallback{
		/**
		 * {@code TasmContigVisitorMemento} is a marker
		 * interface that {@link TasmFileParser}
		 * instances can use to "rewind" back
		 * to the position in its contig file
		 * in order to revisit portions of the contig file. 
		 * {@link TasmContigVisitorMemento} should only be used
		 * by the {@link TasmFileParser} instance that
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
	 * a visitor calls {@link TasmContigVisitorCallback#halt()}.
	 */
	void halted();
	/**
	 * The entire tasm file has been visited.
	 */
	void visitEnd();
}
