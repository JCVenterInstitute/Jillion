package org.jcvi.jillion.fasta;

/**
 * {@code FastaRecordVisitor} is a visitor
 * interface to visit a single fasta record
 * inside of a fasta file.
 * @author dkatzel
 *
 */
public interface FastaRecordVisitor {
	/**
     * Visit a line of the body of the fasta record.
     * @param bodyLine the current line as a String (including
     * white space).  Will never be null and shouldn't
     * be empty.
     */
	void visitBodyLine(String line);
	/**
	 * Visit the end of the current fasta record;
	 */
	void visitEnd();
}
