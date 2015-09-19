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
 * {@code FastaVisitorCallback}
 * is a callback mechanism for the {@link FastaVisitor}
 * instance to communicate with the parser
 * that is parsing the fasta data.
 * @author dkatzel
 *
 */
public interface FastaVisitorCallback {
	/**
	 * {@code FastaVisitorMemento} is a marker
	 * interface that {@link FastaParser}
	 * instances can use to "rewind" back
	 * to the position in its fasta file
	 * in order to revisit portions of the fasta file. 
	 * {@link FastaVisitorMemento} should only be used
	 * by the {@link FastaParser} instance that
	 * generated it.
	 * @author dkatzel
	 *
	 */
	public interface FastaVisitorMemento{
		
	}
	/**
	 * Is this callback capable of
	 * creating {@link FastaVisitorMemento}s
	 * via {@link #createMemento()}.
	 * @return {@code true} if this callback
	 * can create mementos; {@code false} otherwise.
	 */
	boolean canCreateMemento();
	/**
	 * Create a {@link FastaVisitorMemento}
	 * 
	 * @return a {@link FastaVisitorMemento}; never null.
	 * @see #canCreateMemento()
	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
	 * returns {@code false}.
	 */
	FastaVisitorMemento createMemento();
	/**
	 * Tell the {@link FastaParser} to stop parsing
	 * the fasta file.  {@link FastqVisitor#visitEnd()}
	 * will still be called.
	 */
	void haltParsing();
}
