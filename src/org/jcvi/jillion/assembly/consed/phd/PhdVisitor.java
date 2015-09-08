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

import java.util.Map;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

public interface PhdVisitor {

	void visitComments(Map<String,String> comments);
	
	void visitBasecall(Nucleotide base, PhredQuality quality, Integer tracePosition);
	
	PhdReadTagVisitor visitReadTag();
	
	PhdWholeReadItemVisitor visitWholeReadItem();
	
	/**
	 * The phd file has been completely visited.
	 */
	void visitEnd();
	/**
	 * The phd visitation has been halted,
	 * usually by calling {@link PhdBallVisitorCallback#haltParsing()}.
	 */
	void halted();
}
