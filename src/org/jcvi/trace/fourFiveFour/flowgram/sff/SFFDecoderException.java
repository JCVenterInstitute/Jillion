/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.trace.TraceDecoderException;

public class SFFDecoderException extends TraceDecoderException {

    /**
     * 
     */
    private static final long serialVersionUID = 5849079908602188978L;

    /**
     * @param message
     * @param cause
     */
    public SFFDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SFFDecoderException(String message) {
        super(message);
    }

}
