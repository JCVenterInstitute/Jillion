/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
/**
 * {@code AbstractPhdBallVisitor} is a {@link PhdBallVisitor}
 * that implements all the methods with default
 * implementations that don't do anything.  This code
 * is meant to be extended so users don't have to implement
 * any methods that they do not care about (users must override
 * any method that want to handle).
 *  
 * @author dkatzel
 *
 */
public abstract class AbstractPhdBallVisitor implements PhdBallVisitor {
	/**
	 * Ignores the file comment.
	 * {@inheritDoc}
	 */
	@Override
	public void visitFileComment(String comment) {
		//no-op

	}
	/**
	 * Always skips this read.
	 * @return null 
	 * {@inheritDoc}
	 */
	@Override
	public PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id,
			Integer version) {
		//always skip
		return null;
	}

	
	@Override
	public void visitEnd() {
		//no-op
	}

	@Override
	public void halted() {
		//no-op
	}

}
