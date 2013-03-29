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
 * {@code FastaRecordVisitor} is a visitor
 * interface to visit a single fasta record
 * inside of a fasta file.
 * @author dkatzel
 *
 */
public interface FastaRecordVisitor {
	/**
     * Visit a line of the body of the fasta record.
     * @param line the current line as a String (including
     * white space).  Will never be null and shouldn't
     * be empty.
     */
	void visitBodyLine(String line);
	/**
	 * Visit the end of the current fasta record;
	 */
	void visitEnd();
	
	 /**
     * The parser has stopped parsing the current
     * fasta record
     * due to {@link FastaVisitorCallback#haltParsing()}
     * being called. The end of the fasta record was
     * not yet reached.
     */
    void halted();
}
