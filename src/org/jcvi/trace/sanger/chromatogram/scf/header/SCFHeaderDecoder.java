/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.header;

import java.io.DataInputStream;

/**
 * <code>SCFHeaderDecoder</code> can parse the beginning of an SCF data stream
 * and create an {@link SCFHeader}.
 *
 * @author dkatzel
 *
 *
 */
public interface SCFHeaderDecoder {
    /**
     * Parse the beginning of the SCF data contain in the {@link DataInputStream}
     * and create a {@link SCFHeader}.
     * @param in {@link DataInputStream} of the SCF data.
     * @return a populated {@link SCFHeader}.
     * @throws SCFHeaderDecoderException if there are any
     * problems parsing the {@link SCFHeader}.
     */
    SCFHeader decode(DataInputStream in) throws SCFHeaderDecoderException;
}
