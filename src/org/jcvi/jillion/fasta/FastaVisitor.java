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
package org.jcvi.jillion.fasta;


/**
 * {@code FastaFileVisitor} is a visitor
 * interface to visit components of a single
 * fasta file.
 * 
 * @author dkatzel
 *
 */
public interface FastaVisitor {

	/**
     * Visit the definition line of the current fasta record.
     * @param callback a {@link FastaVisitorCallback} that can be used
     * to communicate with the parser object.
     * @param id the id of this record as a String
     * @param optionalComment the comment for this record.  This comment
     * may have white space.  If no comment exists, then this
     * parameter will be null.
     * @return an instance of {@link FastaRecordVisitor};
     * if this method returns null, then that means
     * to skip the current record.
     */
	FastaRecordVisitor visitDefline(FastaVisitorCallback callback, String id, String optionalComment);
	/**
	 * Visit the end of the fasta file.
	 */
	void visitEnd();
	
	 /**
     * The parser has stopped parsing the fasta file
     * due to {@link FastaVisitorCallback#haltParsing()}
     * being called. The end of the fasta file was
     * not yet reached.
     */
    void halted();
}
