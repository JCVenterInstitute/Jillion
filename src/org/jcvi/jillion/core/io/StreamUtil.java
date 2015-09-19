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
package org.jcvi.jillion.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
/**
 * Utility class for working
 * with Java 8 Streams.
 * @author dkatzel
 *
 */
public final class StreamUtil {

	private StreamUtil(){
		//can not instantiate
	}
	/**
     * Create a new Runnable that will close
     * the given {@link Closeable} and rethrow any
     * IOException as an {@link UncheckedIOException}.
     * This is supposed to be used as input to 
     * {@link java.util.stream.Stream#onClose(Runnable)}.
     * @param c
     * @return
     */
    public static Runnable newOnCloseRunnableThatThrowsUncheckedIOExceptionIfNecessary(Closeable c){
    	return () ->{
    		try {
				c.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
    	};
    }
}
