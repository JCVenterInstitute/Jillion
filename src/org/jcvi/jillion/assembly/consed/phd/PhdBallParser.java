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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.phd;

import java.io.IOException;

import org.jcvi.jillion.assembly.consed.phd.PhdBallVisitorCallback.PhdBallVisitorMemento;
/**
 * {@code PhdBallParser} is an interface that will
 * parse a phd formatted structures and call the appropriate 
 * visit methods on the given {@link PhdBallVisitor}.
 * @author dkatzel
 *
 */
public interface PhdBallParser {
	/**
	 * Can this handler accept new parse requests
	 * via parse() calls.
	 * 
	 * Some implementations of {@link PhdBallParser}
	 * may only allow one parse call in its lifetime 
	 * (for example, if the phd structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new parse requests; {@code false} otherwise.
	 */
	boolean canParse();
	/**
	 * Parse the phd or phd ball structure starting from the beginning 
	 * and call the appropriate
	 * visit methods on the given {@link PhdBallVisitor}.
	 * @param visitor the {@link PhdBallVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the phd.
	 * @throws NullPointerException if visitor is null.
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void accept(PhdBallVisitor visitor) throws IOException;	
	/**
	 * Parse the phd or phd ball structure starting from 
	 * location provided by the {@link PhdBallVisitorMemento}
	 * and call the appropriate
	 * visit methods on the given {@link PhdBallVisitor}.
	 * @param visitor the {@link PhdBallVisitor} instance to call
	 * visit methods on; can not be null;
	 * @param memento the {@link PhdBallVisitorMemento} instance
	 * to tell the parser where to start parsing from;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the phd file.
	 * @throws NullPointerException if either the visitor or memento are null.
	 * @throws UnsupportedOperationException if mementos are not supported by this
	 * parser implementation (for example when parsing an {@link InputStream}
	 * instead of a {@link File}).
	 * @throws IllegalStateException if this handler can not accept
	 * any new parse requests.
	 * @see #canParse()
	 */
	void accept(PhdBallVisitor visitor, PhdBallVisitorMemento memento) throws IOException;
	
}
