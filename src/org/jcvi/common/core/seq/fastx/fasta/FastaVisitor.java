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
 * Created on Apr 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta;

import org.jcvi.io.TextFileVisitor;
/**
 * {@code FastaVisitor} is a {@link TextFileVisitor}
 * that is used to visit Fasta Records.
 * @author dkatzel
 *
 *
 */
public interface FastaVisitor extends TextFileVisitor{
    /**
     * Visit the definition line of the current fasta record.
     * @param defline a string containing all the text of
     * the def line including any comments and white space.
     * @return {@code true} if the parser should keep parsing
     * {@code false} if it should stop parsing.
     */
    boolean visitDefline(String defline);
    /**
     * Visit a line of the body of the fasta record.
     * @param bodyLine
     * @return {@code true} if the parser should keep parsing
     * {@code false} if it should stop parsing.
     */
    boolean visitBodyLine(String bodyLine);
    /**
     * Visit the entire current fasta record which
     * includes information parsed from the most recent 
     * call to {@link #visitDefline(String)} and any
     * {@link #visitBodyLine(String)}s.
     * @param id the id of the fasta record.
     * @param comment the comment if there is one (will be null
     * if no comment exists.
     * @param entireBody the entire body of the fasta record
     * which might include new lines.
     * @return {@code true} if the parser should keep parsing
     * {@code false} if it should stop parsing.
     */
    boolean visitRecord(String id, String comment, String entireBody);
}
