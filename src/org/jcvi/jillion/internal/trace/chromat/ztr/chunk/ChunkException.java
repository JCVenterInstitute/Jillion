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
/*
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import java.io.IOException;
/**
 * <code>ChunkException</code> is the Exception that should
 * be thrown whenever a problem encoding/decoding a {@link Chunk}
 * occurs.
 * @author dkatzel
 *
 *
 */
@SuppressWarnings("serial")
public class ChunkException extends IOException {

    /**
     * Constructor.
     * @param message the error message
     */
    public ChunkException(String message) {
        super(message);
    }
    /**
     * Constructor.
     * @param message the error message
     * @param cause the cause of the error.
     */
    public ChunkException(String message, Throwable cause) {
        super(message, cause);
    }


}
