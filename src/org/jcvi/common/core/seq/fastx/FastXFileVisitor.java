package org.jcvi.common.core.seq.fastx;

import org.jcvi.common.core.io.TextFileVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fastx.fastq.FastQFileVisitor;
/**
 * {@code FastXVisitor} is an interface which contains
 * common visit methods for both fasta and fastq 
 * sub-interfaces.
 * @author dkatzel
 * @see FastaFileVisitor
 * @see FastQFileVisitor
 *
 */
public interface FastXFileVisitor extends TextFileVisitor{

	/**
	 * Allowable return values
	 * for {@link FastXFileVisitor#visitDefline(String)}.
	 * @author dkatzel
	 *
	 */
	enum DeflineReturnCode{
		/**
		 * Skip the current fasta record.
		 * Calls to {@link FastXVisitor#visitLine(String)}
		 * will still be called but
		 * {@link FastXVisitor#visitBodyLine(String)}
		 * and {@link FastXVisitor#visitEndOfBody()}
		 * will not be called.
		 */
		SKIP_CURRENT_RECORD,
		/**
		 * Parse the current fasta record and 
		 * make appropriate calls to
		 * {@link FastXVisitor#visitBodyLine(String)}
		 * and {@link FastXVisitor#visitEndOfBody()}.
		 * Calls to {@link FastXVisitor#visitLine(String)}
		 * will also still be called.
		 */
		VISIT_CURRENT_RECORD,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link FastXVisitor#visitEndOfFile()}.
		 */
		STOP_PARSING
	}
	/**
	 * Allowable return values
	 * for {@link FastXFileVisitor#visitEndOfBody()}.
	 * @author dkatzel
	 *
	 */
	enum EndOfBodyReturnCode{
		/**
		 * Continue parsing the file,
		 * if there are still more records
		 * to be parsed then
		 * {@link FastXVisitor#visitDefline(String)}
		 * will get called next;
		 * otherwise {@link FastXVisitor#visitEndOfFile()}.
		 * will get called if we have reached the end 
		 * of the file.
		 */
		KEEP_PARSING,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link FastXVisitor#visitEndOfFile()}.
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
}
