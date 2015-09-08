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
/**
 * {@code PhdBallVisitor} is a visitor
 * for a phd.ball file or phd file that contains
 * one or more phd records.
 * 
 * @author dkatzel
 *
 */
public interface PhdBallVisitor {
	/**
	 * Optional comment at the beginning
	 * of new versions of phd.ball files.
	 * The comment is often the path to the 
	 * corresponding wrapped input file
	 * (path to fastq or sff file).
	 * @param comment
	 */
	void visitFileComment(String comment);
	/**
	 * Denotes when a new phd record has been detected.
	 * @param callback the {@link PhdBallVisitorCallback} instance
	 * that can be used to call pack to the parser.
	 * @param id the id of this phd record.
	 * @param version the version of this phd record; if the version
	 * is not specified, then this value will be {@code null}. More recent versions
	 * of the phdball file format specification allow
	 * the version to be included for each record but not all files
	 * contain this information. 
	 * @return a non-null {@link PhdVisitor} instance if this
	 * record should be parsed; or {@code null} if this phd record
	 * should be skipped.
	 */
	PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id , Integer version);

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
