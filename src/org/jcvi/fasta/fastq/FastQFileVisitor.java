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
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.io.TextFileVisitor;
/**
 * {@code FastQFileVisitor} is a {@link TextFileVisitor}
 * implementation for FASTQ files.
 * @author dkatzel
 *
 *
 */
public interface FastQFileVisitor extends TextFileVisitor{
    /**
     * Begin a new FASTQ Record block for the given read.
     * @param id the read id
     * @param optionalComment any optional comments about the read
     * given on the defline.
     * @return a {@code true} if this parser should parse the 
     * read data; {@code false} if this parser should skip this
     * read and continue on to the next read.
     */
    boolean visitBeginBlock(String id, String optionalComment);
    /**
     * The current FastQRecord Block is done visiting.
     * @return {@code true} if the fastq file should continue to be parsed;
     * {@code false} if the fastq parsing should stop.
     */
    boolean visitEndBlock();
    
    void visitNucleotides(NucleotideEncodedGlyphs nucleotides);
    
    void visitEncodedQualities(String encodedQualities);
}
