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
package org.jcvi.jillion.assembly.tigr.contig;
/**
 * {@code TigrContigFileVisitor} is a 
 * visitor interface to visit
 * contigs of a TIGR {@literal .contig}
 * encoded file.
 * @author dkatzel
 *
 */
public interface TigrContigFileVisitor {
	/**
	 * {@code TigrContigVisitorCallback}
	 * is a callback mechanism to allow the
	 * {@link TigrContigVisitor} instances
	 * to communicate with the parser
	 * that is parsing the contig file.
	 * @author dkatzel
	 *
	 */
	interface TigrContigVisitorCallback{
		/**
		 * {@code TigrContigVisitorMemento} is a marker
		 * interface that {@link TigrContigFileParser}
		 * instances can use to "rewind" back
		 * to the position in its contig file
		 * in order to revisit portions of the contig file. 
		 * {@link TigrContigVisitorMemento} should only be used
		 * by the {@link TigrContigFileParser} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		interface TigrContigVisitorMemento{
			
		}
		/**
		 * Is this callback capable of
		 * creating {@link TigrContigVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link TigrContigVisitorMemento}
		 * 
		 * @return a {@link TigrContigVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		TigrContigVisitorMemento createMemento();
		/**
		 * Tell the {@link TigrContigFileParser} to stop parsing
		 * the contig file this will cause {@link TigrContigFileVisitor#halted()}
		 * to be called but no other visit methods will be called.
		 */
		void haltParsing();
	}
	
	/**
	 * A new Contig has been detected in the contig file.
	 * @param callback an instance of {@link TigrContigVisitorCallback};
	 * will never be null.
	 * @param contigId the contig id of this contig.
	 * @return a {@link TigrContigVisitor} instance
	 * if this contig should be visited;
	 * if {@code null} is returned, then
	 * this contig will not be visited.
	 */
	TigrContigVisitor visitContig(TigrContigVisitorCallback callback, String contigId);
	/**
	 * The parser has stopped 
	 * parsing but has not
	 * actually finished the parsing the file,
	 * this will happen only if 
	 * a visitor calls {@link TigrContigVisitorCallback#haltParsing()}.
	 */
	void halted();
	/**
	 * The entire file was visited.
	 */
	void visitEnd();
}
