/*
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcvi.trace.TraceDecoderException;

public interface SangerTraceCodec<T extends SangerTrace> {
    /**
     * Encodes the given {@link SangerTrace}
     * and writes the encoded data to the given {@link OutputStream}.
     * @param trace the {@link SangerTrace} to encode.
     * @param out the outputStream to write to.
     * @throws IOException if there are any problems encoding the trace
     * or any problems writing to the {@link OutputStream}.
     */
    void encode(T trace, OutputStream out) throws IOException;
    /**
     * Decodes the given encoded trace, decode into a {@link SangerTrace}.
     * @param in the {@link DataInputStream} which has the trace Data.
     * @return a {@link SangerTrace}.
     * @throws TraceDecoderException if there are any problems decoding
     * the trace.
     */
    T decode(InputStream in) throws TraceDecoderException;
}
