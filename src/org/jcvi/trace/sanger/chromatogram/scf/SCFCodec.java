/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcvi.trace.sanger.SangerTrace;
import org.jcvi.trace.sanger.SangerTraceCodec;

/**
 * <code>SCFCodec</code> is used to encode and decode {@link SCFChromatogram}s.
 * @author dkatzel
 *
 *
 */
public interface SCFCodec extends SangerTraceCodec{
    /**
     * Encodes the given {@link SangerTrace} into SCF Format
     * and writes the encoded data to the given {@link OutputStream}.
     * @param chromatogram the {@link SangerTrace} to encode.
     * @param out the outputStream to write to.
     * @throws IOException if there are any problems encoding the chromatogram
     * or any problems writing to the {@link OutputStream}.
     */
    void encode(SangerTrace chromatogram, OutputStream out) throws IOException;
    /**
     * Decodes the given SCF Data Stream into an {@link SCFChromatogram}.
     * @param in the {@link InputStream} which has the SCF Data.
     * @return a {@link SCFChromatogram}.
     * @throws SCFDecoderException if there are any problems decoding
     * the SCF Data.
     */
    SCFChromatogram decode(InputStream in) throws SCFDecoderException;
}
