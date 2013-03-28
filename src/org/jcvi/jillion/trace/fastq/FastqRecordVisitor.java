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
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
/**
 * {@code FastqRecordVisitor} is a visitor
 * interface to visit a single fastq record
 * inside of a fastq file.
 * @author dkatzel
 *
 */
public interface FastqRecordVisitor {

	/**
     * Visit the {@link NucleotideSequence} of the current 
     * fastq record.
     * @param nucleotides the {@link NucleotideSequence};
     * will never be null.
     */
    void visitNucleotides(NucleotideSequence nucleotides);
    /**
     * Visit the encoded quality values for the current
     * fastq record.  If the fastq file breaks the quality values
     * across multiple lines, then {@code  encodedQualities}
     * will be the concatenation of all of those lines with all
     * whitespace removed.
     * @param encodedQualities the encoded quality values as a single line string;
     * will never be null.
     * @see FastqQualityCodec
     */
    void visitEncodedQualities(String encodedQualities);
    /**
	 * Visit the end of the 
	 * current fastq record.
	 */
    void visitEnd();
    
    /**
     * The parser has stopped parsing the 
     * current fastq record
     * due to {@link FastqVisitorCallback#haltParsing()}
     * being called. The end of the fastq record was
     * not yet reached.
     */
    void halted();
    
    
}
