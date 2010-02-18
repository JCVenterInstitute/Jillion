/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import org.jcvi.trace.sanger.chromatogram.scf.SCFDecoderException;
/**
 * <code>SectionDecoderException</code> is a subclass
 * of {@link SCFDecoderException} that will be thrown
 * if there is a problem parsing an SCF {@link Section}.
 * @author dkatzel
 *
 *
 */
public class SectionDecoderException extends SCFDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = 7865788143804921065L;

    /**
     * @param message
     * @param cause
     */
    public SectionDecoderException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param message
     * @param cause
     */
    public SectionDecoderException(String message) {
        super(message);
    }

}
