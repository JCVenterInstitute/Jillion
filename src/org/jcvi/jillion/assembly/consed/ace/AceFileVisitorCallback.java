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


public interface AceFileVisitorCallback {
	/**
	 * {@code AceFileVisitorMemento} is a marker
	 * interface that {@link AceFileParser}
	 * instances can use to "rewind" back
	 * to the position in its ace file
	 * in order to revisit portions of the ace file. 
	 * {@link AceFileVisitorMemento} should only be used
	 * by the {@link AceFileParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	interface AceFileVisitorMemento{
		
	}
	/**
	 * Is this callback capabable of
	 * creating {@link AceFileVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Create a {@link AceFileVisitorMemento}
	 * 
	 * @return a {@link AceFileVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	AceFileVisitorMemento createMemento();
	/**
	 * Tell the {@link AceFileParser} to stop parsing
	 * the ace file.  {@link AceFileVisitor#halted()}
	 * will still be called.
	 */
	void haltParsing();
}
