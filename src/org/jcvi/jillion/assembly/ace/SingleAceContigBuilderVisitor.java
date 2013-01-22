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
package org.jcvi.jillion.assembly.ace;
/**
 * {@code SingleAceContigBuilderVisitor} is an acefile visitor
 * that builds only a single contig.  It will halt the visitor
 * after only seeing 1 complete contig.
 * @author dkatzel
 *
 */
class SingleAceContigBuilderVisitor extends AbstractAceFileVisitorContigBuilder{

	private AceContig contig;

	@Override
	protected void visitContig(AceContigBuilder contigBuilder) {
		contig = contigBuilder.build();		
	}

	public final AceContig getContig() {
		return contig;
	}
	@Override
	public EndContigReturnCode getEndContigReturnCode() {
		if(contig==null){
			return EndContigReturnCode.KEEP_PARSING;
		}
		//we have our built contig no need
		//to keep parsing
		return  EndContigReturnCode.STOP_PARSING;
	}
	
	
}
