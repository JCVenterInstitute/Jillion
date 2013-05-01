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

import java.util.Date;
/**
 * {@code AbstractAceContigReadVisitor} is an {@link AceContigReadVisitor}
 * that implements all the methods as stubs.  By default
 * all non-void methods return {@code null}.  Users should
 * create subclasses that override the methods they wish to handle.
 * @author dkatzel
 *
 */
public abstract class AbstractAceContigReadVisitor implements AceContigReadVisitor{

	@Override
	public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
			int alignRight) {
		//no-op		
	}

	@Override
	public void visitTraceDescriptionLine(String traceName, String phdName,
			Date date) {
		//no-op
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		//no-op
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
