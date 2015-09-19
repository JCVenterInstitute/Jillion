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
package org.jcvi.jillion.trace.sff;

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code SffWriter} is an inteface for
 * writing sff encoded files.
 * @author dkatzel
 *
 */
public interface SffWriter extends Closeable{

	/**
	 * Write the given Flowgram.
	 * @param flowgram the flowgram to write
	 * can not be null.
	 * @throws IOException if there is a problem writing this 
	 * flowgram to the writer.
	 */
	void write(SffFlowgram flowgram) throws IOException;
	/**
	 * Write out an encoded Flowgram using the data
	 * contained in the given {@link SffReadHeader} and {@link SffReadData}.
	 * @param header the {@link SffReadHeader} to write
	 * can not be null.
	 * @param data the {@link SffReadData} to write
	 * can not be null.
	 * @throws IOException if there is a problem writing this 
	 * flowgram to the writer.
	 */
	void write(SffReadHeader header, SffReadData data) throws IOException;
	/**
	 * Complete writing out the sff file and close the file.
	 * <strong>Users must always close the writer to guarantee that all the 
	 * sff data has been written. </strong>
	 * {@link SffWriter} implementations may delay writing out all the data
	 * to the output until the writer is closed. (For example, 
	 * certain header information such as the number of reads in the file will not be known
	 * until all the records have been written).
	 */
	@Override
	void close() throws IOException;
}
