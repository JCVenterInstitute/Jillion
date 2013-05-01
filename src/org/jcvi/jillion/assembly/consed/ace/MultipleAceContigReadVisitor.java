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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MultipleAceContigReadVisitor implements AceContigReadVisitor{

	private final List<AceContigReadVisitor> delegates;
	
	public MultipleAceContigReadVisitor(AceContigReadVisitor... delegates){
		this(Arrays.asList(delegates));
	}
	public MultipleAceContigReadVisitor(
			List<? extends AceContigReadVisitor> delegates) {
		this.delegates = new ArrayList<AceContigReadVisitor>(delegates.size());
		for(AceContigReadVisitor visitor : delegates){
			if(visitor !=null){
				this.delegates.add(visitor);
			}
		}
	}

	@Override
	public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
			int alignRight) {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitQualityLine(qualLeft, qualRight, alignLeft, alignRight);
		}
		
	}

	@Override
	public void visitTraceDescriptionLine(String traceName, String phdName,
			Date date) {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitTraceDescriptionLine(traceName, phdName, date);
		}
		
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitBasesLine(mixedCaseBasecalls);
		}
		
	}

	@Override
	public void visitEnd() {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitEnd();
		}
		
	}

	@Override
	public void halted() {
		for(AceContigReadVisitor visitor : delegates){
			visitor.halted();
		}
		
	}

}
