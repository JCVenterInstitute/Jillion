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

import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * {@code PhdReadTagVisitor} is a Visitor
 * that will visit a single {@link PhdReadTag}.
 * @author dkatzel
 * @see PhdReadTag
 */
public interface PhdReadTagVisitor {
	/**
	 * Visit the type of this tag.
	 * @param type a String describing the type,
	 * will never be null, but may be empty.
	 */
	void visitType(String type);
	/**
	 * Visit the source that generated this tag
	 *  (usually the program name).
	 * @param type a String describing the source,
	 * will never be null, but may be empty.
	 */
	void visitSource(String source);
	/**
	 * Visit the ungapped {@link Range}
	 * into the read that this tag refers.
	 * @param ungappedRange a Range; will never be null.
	 */
	void visitUngappedRange(Range ungappedRange);
	/**
	 * Visit the {@link Date} that 
	 * this tag was created.
	 * @param date a Date; will never be null.
	 */
	void visitDate(Date date);
	/**
	 * Visit the comment that goes along
	 * with this tag.  If a tag
	 * does not have a comment,
	 * then this method will not be visited.
	 * @param comment a String; will never be null;
	 * but may be multi-line.
	 */
	void visitComment(String comment);
	/**
	 * Visit the comment that goes along
	 * with this tag.  If a tag
	 * does not have any free form data,
	 * then this method will not be visited.
	 * @param data a String; will never be null;
	 * but may be multi-line.
	 */
	void visitFreeFormData(String data);
	
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
