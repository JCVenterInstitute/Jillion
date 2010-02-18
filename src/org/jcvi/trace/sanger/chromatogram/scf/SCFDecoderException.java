/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import org.jcvi.trace.TraceDecoderException;
/**
 * <code>SCFParserException</code> is a subclass of
 * {@link TraceDecoderException} which is used if an SCF
 * file fails to parse.
 * @author dkatzel
 *
 *
 */
public class SCFDecoderException extends TraceDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = -8636660736340790019L;

    /**
     * @param message
     * @param cause
     */
    public SCFDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SCFDecoderException(String message) {
        super(message);
    }

}
