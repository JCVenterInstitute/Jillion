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
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code FastQFileVisitor} is a {@link TextFileVisitor}
 * implementation for FASTQ files.
 * @author dkatzel
 *
 *
 */
public interface FastqFileVisitor extends FastXFileVisitor{
    /**
     * Visit the defline of a given fastq record.
     * <strong>Note: </strong>if the Fastq records were created using 
     * Casava 1.8+, then the id will contain a whitespace
     * followed by the mate information and no comment.
     * This is different than most other fastq parsers which separate
     * on whitespace and therefore will create duplicate ids for each
     * mate in the template (but with different values for the "comments").
     * Duplicate ids will break any applications that combine all the reads
     * from multiple fastq files so it was decided that {@link FastqRecord} id
     * contain both the template and mate information to guarantee uniqueness.
     * <p/>
     * {@inheritDoc}
     */
	DeflineReturnCode visitDefline(String id, String optionalComment);
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
}
