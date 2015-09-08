/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta;

import java.io.IOException;

import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
/**
 * {@code FastaParser} is an interface that will
 * parse a FASTA formatted structure and call the appropriate 
 * visit methods on the given {@link FastaVisitor}.
 * @author dkatzel
 *
 */
public interface FastaParser {

	/**
	 * Can this parser accept new parse requests
	 * via parse() calls.
	 * 
	 * Some implementations of {@link FastaParser}
	 * may only allow one parse call in its lifetime 
	 * (for example, if the fasta structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Is this {@link FastaParser}'s callbacks capable of
	 * creating {@link FastaVisitorMemento}s.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Can the {@link #parse(FastaVisitor)}
	 * or {@link #parse(FastaVisitor, FastaVisitorMemento)}
	 * methods be called multiple times.
	 * Some implementations are working off of an
	 * InputStream that can't be rewound
	 * or reset we can only read Once.
	 * @return {@code true} if the data
	 * can only be parsed once;
	 * {@code false} otherwise.
	 */
	boolean isReadOnceOnly();
	
	/**
	 * Parse the fasta structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link FastaVisitor}.
	 * @param visitor the {@link FastaVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the fasta.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(FastaVisitor visitor) throws IOException;
	
	
	/**
	 * Parse the fasta structure starting from 
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
	 * any new parse requests.
	 * @see #canParse()
	 */
	void parse(FastaVisitor visitor, FastaVisitorMemento memento) throws IOException;
	
}
