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
package org.jcvi.jillion.assembly.consed.phd;


public interface PhdBallVisitorCallback {
	/**
	 * {@code PhdBallVisitorMemento} is a marker
	 * interface that {@link PhdBallFileParser}
	 * instances can use to "rewind" back
	 * to the position in its ace file
	 * in order to revisit portions of the ace file. 
	 * {@link PhdBallVisitorMemento} should only be used
	 * by the {@link PhdBallFileParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	interface PhdBallVisitorMemento{
		
	}
	/**
	 * Is this callback capable of
	 * creating {@link PhdBallVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Create a {@link PhdBallVisitorMemento}.
	 * 
	 * @return a {@link PhdBallVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	PhdBallVisitorMemento createMemento();
	/**
	 * Tell the {@link PhdBallFileParser} to stop parsing
	 * the ace file.  {@link AceFileVisitor#halted()}
	 * will still be called.
	 */
	void haltParsing();
}
