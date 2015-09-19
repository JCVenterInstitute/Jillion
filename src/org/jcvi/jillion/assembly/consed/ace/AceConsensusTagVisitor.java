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
package org.jcvi.jillion.assembly.consed.ace;

public interface AceConsensusTagVisitor {

	/**
     * The current consensus tag contains a comment (which might span multiple lines).
     * @param comment the full comment as a string.
     */
    void visitComment(String comment);
    /**
     * The current consensus tag contains a data.
     * @param data the data as a string.
     */
    void visitData(String data);
    /**
     * The current consensus tag has been completely parsed.
     */
    void visitEnd();
    
    void halted();
}
