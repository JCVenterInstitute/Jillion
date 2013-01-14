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
package org.jcvi.common.core.seq.fasta;

import org.jcvi.jillion.core.io.TextFileVisitor;
/**
 * {@code FastaFileVisitor} is a {@link TextFileVisitor}
 * that is used to visit Fasta Records.
 * @author dkatzel
 *
 *
 */
public interface FastaFileVisitor extends TextFileVisitor{
	
	
	/**
	 * Allowable return values
	 * for {@link visitDefline(String)}
	 * which tell the parser
	 * how to proceed now that 
	 * the current def line has been visited.
	 * @author dkatzel
	 *
	 */
	enum DeflineReturnCode{
		/**
		 * Skip the current fasta record.
		 * Calls to {@link visitLine(String)}
		 * will still be called but
		 * {@link visitBodyLine(String)}
		 * and {@link visitEndOfBody()}
		 * will not be called.
		 */
		SKIP_CURRENT_RECORD,
		/**
		 * Parse the current fasta record and 
		 * make appropriate calls to
		 * {@link visitBodyLine(String)}
		 * and {@link visitEndOfBody()}.
		 * Calls to {@link visitLine(String)}
		 * will also still be called.
		 */
		VISIT_CURRENT_RECORD,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link visitEndOfFile()}.
		 */
		STOP_PARSING
	}
	/**
	 * Allowable return values
	 * for {@link visitEndOfBody()}
	 * which tell the parser how to proceed
	 * now that a complete fasta record has been visited.
	 * @author dkatzel
	 *
	 */
	enum EndOfBodyReturnCode{
		/**
		 * Continue parsing the file,
		 * if there are still more records
		 * to be parsed then
		 * {@link visitDefline(String)}
		 * will get called next;
		 * otherwise {@link visitEndOfFile()}.
		 * will get called if we have reached the end 
		 * of the file.
		 */
		KEEP_PARSING,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link visitEndOfFile()}.
		 */
		STOP_PARSING
	}
    /**
     * Visit the definition line of the current fasta record.
     * @param id the id of this record as a String
     * @param optionalComment the comment for this record.  This comment
     * may have white space.  If no comment exists, then this
     * parameter will be null.
     * @return a non-null instance of {@link DeflineReturnCode}
     * telling the parser how it should proceed. 
     * Returning null will throw an {@link IllegalStateException}.
     * @see DeflineReturnCode
     */
	DeflineReturnCode visitDefline(String id, String optionalComment);
	
	/**
     * The current fasta record body has been completely
     * visited.  This method is only called
     * if {@link #visitDefline(String)}
     * returns {@link DeflineReturnCode#VISIT_CURRENT_RECORD}.
     * @return a non-null instance of {@link EndOfBodyReturnCode}
     * which tells the parser how it should proceed.
     * Returning null will throw an {@link IllegalStateException}.
     * @see EndOfBodyReturnCode
     */
    EndOfBodyReturnCode visitEndOfBody();
	
	
	
    /**
     * Visit a line of the body of the fasta record.
     * This line is only called if {@link #visitDefline(String)}
     * returns {@link DeflineReturnCode#VISIT_CURRENT_RECORD}.
     * @param bodyLine the current line as a String (including
     * white space).  Will never be null and shouldn't
     * be empty.
     */
    void visitBodyLine(String bodyLine);
    
  
}
