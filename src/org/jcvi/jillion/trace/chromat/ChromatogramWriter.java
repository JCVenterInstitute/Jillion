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
package org.jcvi.jillion.trace.chromat;

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code ChromatogramWriter} can write
 * {@link Chromatogram} objects to some kind out
 * output.
 * Implementations may encode the chromatogram
 * in different ways.
 * @author dkatzel
 *
 *
 */
public interface ChromatogramWriter extends Closeable{
	 /**
     * Writes the given {@link Chromatogram}.
     * @param chromatogram the {@link Chromatogram} to write.
     * @throws IOException if there are any problems encoding the chromatogram
     * or any problems writing to the output.
     */
	void write(Chromatogram c) throws IOException;
}
