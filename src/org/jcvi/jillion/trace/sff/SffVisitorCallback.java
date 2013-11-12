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
package org.jcvi.jillion.trace.sff;
/**
 * {@code SffVisitorCallback}
 * is a callback mechanism for the {@link SffVisitor}
 * instance to communicate with the parser
 * that is parsing the sff data.
 * @author dkatzel
 *
 */
public interface SffVisitorCallback {
	/**
	 * {@code SffVisitorMemento} is a marker
	 * interface that {@link SffVisitorHandler}
	 * instances can use to "rewind" back
	 * to the position in its sff structure
	 * in order to revisit portions of the data. 
	 * {@link SffVisitorMemento} should only be used
	 * by the {@link SffVisitorHandler} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	interface SffVisitorMemento{
		
	}
	/**
	 * Is this callback capable of
	 * creating {@link SffVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean mementoSupported();
	/**
	 * Create a {@link SffVisitorMemento}
	 * 
	 * @return a {@link SffVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	SffVisitorMemento createMemento();
	/**
	 * Tell the {@link SffVisitorHandler} to stop parsing
	 * the sff.  {@link SffVisitor#visitEnd()}
	 * will still be called.
	 */
	void haltParsing();
}
