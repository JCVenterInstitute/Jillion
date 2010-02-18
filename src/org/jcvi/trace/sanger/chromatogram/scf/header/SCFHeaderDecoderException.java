/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.header;

import org.jcvi.trace.sanger.chromatogram.scf.SCFDecoderException;

/**
 * <code>SCFHeaderParserException</code> is a subclass of
 * {@link SCFDecoderException} which is used if an SCF
 * Header fails to parse.
 * @author dkatzel
 *
 *
 */
public class SCFHeaderDecoderException extends SCFDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = 3355691805761753118L;

    /**
     * @param message
     * @param cause
     */
    public SCFHeaderDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SCFHeaderDecoderException(String message) {
        super(message);
    }

}
