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
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.io.FileVisitor;
/**
 * {@code SffFileVisitor} is a {@link FileVisitor}
 * implementation for 454 .sff binary encoded files.
 * @author dkatzel
 * @see SffParser
 *
 */
public interface SffFileVisitor extends FileVisitor {
    /**
     * Visit the header information that is common to 
     * all reads in this sff file.  The Boolean
     * return value allows visitors to tell the parser
     * to continue parsing the sff data or to halt parsing
     * entirely.
     * @param commonHeader the {@link SFFCommonHeader} of this sff
     * file being parsed.
     * @return {@code true} to continue parsing the
     * SFF file and move on to the reads;
     * {@code false} to stop parsing the file.
     */
    boolean visitCommonHeader(SFFCommonHeader commonHeader);
    /**
     * Visit the header for the current read.  The Boolean
     * return value allows visitors to tell the parser
     * to visit this read's data or to skip it and move
     * onto the next read.
     * @param readHeader the parsed header for the current read
     * being parsed.
     * @return {@code true} to parse the read data for this 
     * read; {@code false} to skip this read and move on to
     * the next.
     */
    boolean visitReadHeader(SFFReadHeader readHeader);
    /**
     * Visit the read data for the current read.  The Boolean
     * return value allows visitors to tell the parser
     * to continue parsing the sff data or to halt parsing
     * entirely.  (this is useful if a visitor has visited all the 
     * reads it cares about and wants to stop parsing to save time and
     * resources.
     * @param readData the data for the current read
     * being parsed.
     * @return {@code true} to continue parsing the
     * SFF file and move on to the next read header;
     * {@code false} to stop parsing the file.
     */
    boolean visitReadData(SFFReadData readData);
}
