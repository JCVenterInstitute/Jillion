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
package org.jcvi.jillion.assembly.consed.phd;

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code PhdWriter} is an interface for 
 * writing {@link Phd}s.
 * @author dkatzel
 *
 */
public interface PhdWriter extends Closeable{
	/**
	 * Write the given {@link Phd} without version information.
	 * This is the same as {@link #write(Phd, Integer) write(phd, null)}
	 * All {@link PhdWholeReadItem}s, comments and {@link PhdReadTag}s
	 * for this Phd will also be written out. If the Phd does not contain
	 * any positions ({@link Phd#getPositionSequence()} returns null)
	 * then positions will not be written out.
	 * @param phd the phd to write, can not be null.
	 * @throws IOException if there is a problem
	 * writing this phd.
	 * @throws NullPointerException if phd is null.
	 * @see #write(Phd, Integer)
	 */
	void write(Phd phd) throws IOException ;
	/**
	 * Write the given {@link Phd} with the given version number.
	 * New versions of consed support the version number included 
	 * along with the sequence name inside the phd file.
	 * Previously the version information was only available by
	 * the number suffix in the filename.
	 * All {@link PhdWholeReadItem}s, comments and {@link PhdReadTag}s
	 * for this Phd will also be written out. If the Phd does not contain
	 * any positions ({@link Phd#getPositionSequence()} returns null)
	 * then positions will not be written out.
	 * @param phd the phd to write, can not be null.
	 * @param version the version of this phd; if this value
	 * is null, then no version is written.
	 * @throws IOException if there is a problem
	 * writing this phd.
	 * @throws NullPointerException if phd is null.
	 * @throws IllegalArgumentException if version is not null and
	 * version < 1.
	 */
	void write(Phd phd, Integer version) throws IOException ;

}
