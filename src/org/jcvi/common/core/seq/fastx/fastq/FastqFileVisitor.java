/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.io.TextFileVisitor;
import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
/**
 * {@code FastQFileVisitor} is a {@link TextFileVisitor}
 * implementation for FASTQ files.
 * @author dkatzel
 *
 *
 */
public interface FastqFileVisitor extends FastXFileVisitor{
    /**
     * Begin a new FASTQ Record block for the given read.
     * @param id the read id
     * @param optionalComment any optional comments about the read
     * given on the defline. Note: if the Fastq records were created using 
     * Casava 1.8, then the comment will contain the mate information.
     * If there is no comment, then this parameter is {@code null).
     * @return a {@code true} if this parser should parse the 
     * read data; {@code false} if this parser should skip this
     * read and continue on to the next read.
     */
    //boolean visitDefline(String id, String optionalComment);
    /**
     * The current FastQRecord Block is done visiting.
     * @return {@code true} if the fastq file should continue to be parsed;
     * {@code false} if the fastq parsing should stop.
     */
   // boolean visitEndOfBody();
    
    void visitNucleotides(NucleotideSequence nucleotides);
    
    void visitEncodedQualities(String encodedQualities);
}
