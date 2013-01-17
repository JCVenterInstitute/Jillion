package org.jcvi.jillion.fasta;


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
