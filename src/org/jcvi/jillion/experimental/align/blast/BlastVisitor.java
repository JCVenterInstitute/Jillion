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
package org.jcvi.jillion.experimental.align.blast;


/**
 * {@code BlastVisitor} is a visitor 
 * interface to visit {@link BlastHit}s
 * encoded in blast output.
 * 
 * @author dkatzel
 *
 *
 */
public interface BlastVisitor{

    /**
     * The File has been completely visited.
     */
    void visitEnd();

	void visitInfo(String programName, String programVersion,
			String blastDb, String queryId);
	/**
	 * Visit the next {@link BlastHit}
	 * in the blast output.
	 * @param hit the {@link BlastHit} being visited;
	 * will never be null.
	 */
	void visitHit(BlastHit hit);
}
