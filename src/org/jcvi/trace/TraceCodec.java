/*
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TraceCodec {

    /**
     * parse the trace in the inputStream file and return
     * the Chromatogram object that the file represents.
     *
     * @return A populated <code>Chromatogram</code> object.
     */
    Trace decode(InputStream inputStream) throws TraceDecoderException;
    
    /**
     * Encodes the given {@link Trace}
     * and writes the encoded data to the given {@link OutputStream}.
     * @param trace the {@link Trace} to encode.
     * @param out the outputStream to write to.
     * @throws IOException if there are any problems encoding the trace
     * or any problems writing to the {@link OutputStream}.
     */
    void encode(Trace trace, OutputStream out) throws IOException;
    
}
