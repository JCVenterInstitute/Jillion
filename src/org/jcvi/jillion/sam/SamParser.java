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
package org.jcvi.jillion.sam;

import java.io.IOException;

import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code SamParser}
 * is an interface that can parse
 * SAM or BAM files and call the appropriate
 * methods on the given {@link SamVisitor}.
 * @author dkatzel
 *
 */
public interface SamParser {
	/**
	 * 
	 * @return
	 */
	boolean canAccept();
	/**
	 * Parse the given {@link SamVisitor}
	 * and call the appropriate visit methods
	 * on the given visitor.
	 * @param visitor the {@link SamVisitor}
	 * to call the visit methods on;
	 * can not be null.
	 * @throws IOException
	 * @throws NullPointerException if visitor is null.
	 */
	void accept(SamVisitor visitor) throws IOException;
	/**
	 * Get the {@link SamHeader}
	 * for this SAM or BAM file.
	 * @return
	 */
	SamHeader getHeader() throws IOException;
}
