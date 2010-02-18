/*
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import org.jcvi.trace.TraceDecoderException;
/**
 * <code>ChunkException</code> is the Exception that should
 * be thrown whenver a problem encoding/decoding a {@link Chunk}
 * occurs.
 * @author dkatzel
 *
 *
 */
@SuppressWarnings("serial")
public class ChunkException extends TraceDecoderException {

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
