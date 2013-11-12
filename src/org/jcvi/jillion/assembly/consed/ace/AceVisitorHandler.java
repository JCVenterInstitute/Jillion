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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.IOException;

import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback.AceFileVisitorMemento;
/**
 * {@code AceVisitorHandler} is an interface that will
 * traverse an Ace structure and call the appropriate 
 * visit methods on the given {@link AceFileVisitor}.
 * @author dkatzel
 *
 */
public interface AceVisitorHandler {
	/**
	 * Can this handler accept new visit requests
	 * via {@link #accept(AceFileVisitor)} or {@link #accept(AceFileVisitor, AceFileVisitorMemento)}
	 * calls.
	 * 
	 * Some implementations of {@link AceVisitorHandler}
	 * may only allow one accept call in its lifetime 
	 * (for example, if the ace structure is being parsed via
	 * an InputStream).
	 * @return {@code true} if this handler can handle 
	 * new visit requests; {@code false} otherwise.
	 */
	boolean canAccept();
	/**
	 * Walk over the ace structure and call the appropriate methods on the given AceFileVisitor.
	 * @param visitor the visitor to be visited, can not be null.
	 * @throws IOException if the there is a problem reading
	 * the ace data.
	 * @throws NullPointerException if either the visitor is {@code null}.
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(AceFileVisitor visitor) throws IOException;
	/**
	 * Walk over the ace structure 
	 * starting at the location in the structure
	 * that the {@link AceFileVisitorMemento}
	 * specifies, and call the appropriate methods on the given AceFileVisitor.
	 * @param visitor the visitor to be visited, can not be null.
	 * @param memento the {@link AceFileVisitorMemento} that tells this
	 * handler where to start in the ace structure.
	 * 
	 * @throws IOException if the there is a problem reading
	 * the ace data.
	 * @throws NullPointerException if either the visitor is {@code null}.
	 * @throws IllegalStateException if this handler can not accept
	 * any new visit requests.
	 * @see #canAccept()
	 */
	void accept(AceFileVisitor visitor, AceFileVisitorMemento memento)	throws IOException;

}
