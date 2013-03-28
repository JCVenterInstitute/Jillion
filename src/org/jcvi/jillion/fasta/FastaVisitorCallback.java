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
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.trace.fastq.FastqVisitor;



/**
 * {@code FastqVisitorCallback}
 * is a callback mechanism for the {@link FastaVisitor}
 * instance to communicate with the parser
 * that is parsing the fasta data.
 * @author dkatzel
 *
 */
public interface FastaVisitorCallback {
	/**
	 * {@code FastaVisitorMemento} is a marker
	 * interface that {@link FastaFileParser}
	 * instances can use to "rewind" back
	 * to the position in its fasta file
	 * in order to revisit portions of the fasta file. 
	 * {@link FastaVisitorMemento} should only be used
	 * by the {@link FastaFileParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	interface FastaVisitorMemento{
		
	}
	/**
	 * Is this callback capable of
	 * creating {@link FastaVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Create a {@link FastaVisitorMemento}
	 * 
	 * @return a {@link FastaVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	FastaVisitorMemento createMemento();
	/**
	 * Tell the {@link FastaFileParser} to stop parsing
	 * the fasta file.  {@link FastqVisitor#visitEnd()}
	 * will still be called.
	 */
	void haltParsing();
}
