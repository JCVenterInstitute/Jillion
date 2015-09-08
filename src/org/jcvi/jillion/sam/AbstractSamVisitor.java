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
package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.header.SamHeader;

/**
 * {@code AbstractSamVisitor} is an implementation
 * of {@link SamVisitor} that implements all the methods
 * of {@link SamVisitor} as empty methods.
 * 
 * Users may subclass {@code AbstractSamVisitor} and 
 * override the methods they want to implement.
 * @author dkatzel
 *
 */
public abstract class AbstractSamVisitor implements SamVisitor{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record,
			VirtualFileOffset start, VirtualFileOffset end) {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record) {
		//no-op
	}
	
	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEnd() {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void halted() {
		//no-op
	}
}
