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

public interface AceHandler {

	/**
	 * Walk over the ace structure and call the appropriate methods on the given AceFileVisitor.
	 * @param visitor the visitor to be visited, can not be null.
	 * @throws IOException if the there is a problem reading
	 * the ace data.
	 * @throws NullPointerException if either the visitor is {@code null}.
	 */
	void accept(AceFileVisitor visitor) throws IOException;

	void accept(AceFileVisitor visitor, AceFileVisitorMemento memento)
			throws IOException;

}
