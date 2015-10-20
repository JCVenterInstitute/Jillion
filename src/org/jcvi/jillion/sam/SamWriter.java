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

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code SamWriter} is an interface for
 * writing SAM or BAM encoded files.
 * @author dkatzel
 *
 */
public interface SamWriter extends Closeable{
	/**
	 * Write the given record to the SAM or BAM file.
	 * Different implementations of {@link SamWriter}
	 * might delay actually writing out the {@link SamRecord}
	 * either to improve disk writing performance
	 * or to re-sort records.
	 * @param record the {@link SamRecord} to write;
	 * can not be null.
	 * @throws IOException if there is a problem writing the {@link SamRecord}
	 * @throws NullPointerException if record is null.
	 */
	void writeRecord(SamRecord record) throws IOException;
}
