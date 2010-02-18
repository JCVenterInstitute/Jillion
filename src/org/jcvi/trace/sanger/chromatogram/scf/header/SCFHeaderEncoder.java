/*
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.header;

import java.nio.ByteBuffer;
/**
 * <code>SCFHeaderEncoder</code> is used to encode
 * a {@link SCFHeader} into the format specified by the SCF File
 * Format.
 * @author dkatzel
 *
 *
 */
public interface SCFHeaderEncoder {
    /**
     * Encodes the given {@link SCFHeader} into
     * the format specified by the SCF File Format.
     * @param header the header to encode.
     * @return a {@link ByteBuffer} containing
     * the SCF Header data encoded to the SCF
     * File Format Specification.
     */
    ByteBuffer encode(SCFHeader header);
}
