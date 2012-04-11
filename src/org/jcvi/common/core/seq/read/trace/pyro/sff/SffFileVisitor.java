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

import org.jcvi.common.core.io.FileVisitor;
/**
 * {@code SffFileVisitor} is a {@link FileVisitor}
 * implementation for 454 .sff binary encoded files.
 * @author dkatzel
 * @see SffFileParser
 *
 */
public interface SffFileVisitor extends FileVisitor {
	/**
	 * Tells this parser how to proceed
	 * parsing the sff file after
	 * the common header has been visited.
	 * @author dkatzel
	 *
	 */
	enum CommonHeaderReturnCode{
		/**
		 * Continue to parse the 
		 * sff encoded file in order
		 * to parse the read data.
		 */
		PARSE_READS,
		/**
		 * Stop parsing the file.
		 */
		STOP;
	}
	/**
	 * Tells this parser how to proceed
	 * parsing the sff file after
	 * the header for the current read
	 * has been visited.
	 * @author dkatzel
	 *
	 */
	enum ReadHeaderReturnCode{
		/**
		 * Skip this read but continue parsing
		 * the file and read the next read.
		 * The next callback for the visitor
		 * will be {@link SffFileVisitor#visitReadData(SffReadData)}
		 * for the next read.
		 */
		SKIP_CURRENT_READ,
		/**
		 * Parse the read data.
		 * this will cause the {@link SffFileVisitor#visitReadData(SffReadData)}
		 * to get called.
		 */
		PARSE_READ_DATA,
		/**
		 * Stop parsing the file.
		 */
		STOP;
	}
	/**
	 * Tells this parser how to proceed
	 * parsing the sff file after
	 * the current read data
	 * has been visited.
	 * @author dkatzel
	 *
	 */
	enum ReadDataReturnCode{
		/**
		 * Continue parsing
		 * the file and read the next read.
		 * The next callback for the visitor
		 * will be {@link SffFileVisitor#visitReadData(SffReadData)}
		 * for the next read.
		 */
		PARSE_NEXT_READ,
		/**
		 * Stop parsing the file.
		 */
		STOP;
	}
    /**
     * Visit the header information that is common to 
     * all reads in this sff file.  The Boolean
     * return value allows visitors to tell the parser
     * to continue parsing the sff data or to halt parsing
     * entirely.
     * @param commonHeader the {@link SffCommonHeader} of this sff
     * file being parsed.
     * @return an instance of {@link CommonHeaderReturnCode};
     * can not be null.
     */
	CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader);
    /**
     * Visit the header for the current read.  The Boolean
     * return value allows visitors to tell the parser
     * to visit this read's data or to skip it and move
     * onto the next read.
     * @param readHeader the parsed header for the current read
     * being parsed.
     * @return an instance of {@link visitReadHeaderReturnCode};
     * can not be null.
     */
	ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader);
    /**
     * Visit the read data for the current read.  The Boolean
     * return value allows visitors to tell the parser
     * to continue parsing the sff data or to halt parsing
     * entirely.  (this is useful if a visitor has visited all the 
     * reads it cares about and wants to stop parsing to save time and
     * resources.
     * @param readData the data for the current read
     * being parsed.
     * @return an instance of {@link visitReadDataReturnCode};
     * can not be null.
     */
	ReadDataReturnCode visitReadData(SffReadData readData);
}
