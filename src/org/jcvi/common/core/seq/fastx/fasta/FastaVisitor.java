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

import org.jcvi.common.core.io.TextFileVisitor;
/**
 * {@code FastaVisitor} is a {@link TextFileVisitor}
 * that is used to visit Fasta Records.
 * @author dkatzel
 *
 *
 */
public interface FastaVisitor extends TextFileVisitor{
	/**
	 * Allowable return values
	 * for {@link FastaVisitor#visitDefline(String)}.
	 * @author dkatzel
	 *
	 */
	enum DeflineReturnCode{
		/**
		 * Skip the current fasta record.
		 * Calls to {@link FastaVisitor#visitLine(String)}
		 * will still be called but
		 * {@link FastaVisitor#visitBodyLine(String)}
		 * and {@link FastaVisitor#visitEndOfBody()}
		 * will not be called.
		 */
		SKIP_CURRENT_RECORD,
		/**
		 * Parse the current fasta record and 
		 * make appropriate calls to
		 * {@link FastaVisitor#visitBodyLine(String)}
		 * and {@link FastaVisitor#visitEndOfBody()}.
		 * Calls to {@link FastaVisitor#visitLine(String)}
		 * will also still be called.
		 */
		VISIT_CURRENT_RECORD,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link FastaVisitor#visitEndOfFile()}.
		 */
		STOP_PARSING
	}
	/**
	 * Allowable return values
	 * for {@link FastaVisitor#visitEndOfBody()}.
	 * @author dkatzel
	 *
	 */
	enum EndOfBodyReturnCode{
		/**
		 * Continue parsing the file,
		 * if there are still more records
		 * to be parsed then
		 * {@link FastaVisitor#visitDefline(String)}
		 * will get called next;
		 * otherwise {@link FastaVisitor#visitEndOfFile()}.
		 * will get called if we have reached the end 
		 * of the file.
		 */
		KEEP_PARSING,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link FastaVisitor#visitEndOfFile()}.
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
     * Visit a line of the body of the fasta record.
     * This line is only called if {@link #visitDefline(String)}
     * returns {@link DeflineReturnCode#VISIT_CURRENT_RECORD}.
     * @param bodyLine the current line as a String (including
     * white space).  Will never be null and shouldn't
     * be empty.
     */
    void visitBodyLine(String bodyLine);
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
  
}
