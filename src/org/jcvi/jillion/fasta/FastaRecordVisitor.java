/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
     * @param line the current line as a String (including
     * white space).  Will never be null and shouldn't
     * be empty.
     */
	void visitBodyLine(String line);
	/**
	 * Visit the end of the current fasta record;
	 */
	void visitEnd();
	
	 /**
     * The parser has stopped parsing the current
     * fasta record
     * due to {@link FastaVisitorCallback#haltParsing()}
     * being called. The end of the fasta record was
     * not yet reached.
     */
    void halted();
}
