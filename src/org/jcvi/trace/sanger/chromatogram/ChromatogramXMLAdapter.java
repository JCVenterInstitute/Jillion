/*
 * Created on Oct 29, 2007
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jcvi.trace.Trace;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.TraceCodec;

/**
 * <code>ChromatogramXMLAdapter</code> is an adapts
 * {@link ChromatogramXMLSerializer} to conform to the interface
 * for {@link TraceCodec}.  This will allow {@link BasicChromatogram}
 * objects which have been serialized to XML by  {@link ChromatogramXMLSerializer}
 * to be parsed by an instance of {@link TraceCodec}.
 * @author dkatzel
 *
 *
 */
public class ChromatogramXMLAdapter implements TraceCodec {

    public Trace decode(InputStream inputStream) throws TraceDecoderException {
       return ChromatogramXMLSerializer.fromXML(inputStream);
    }

    @Override
    public void encode(Trace trace, OutputStream out) throws IOException {
        ChromatogramXMLSerializer.toXML((Chromatogram)trace , out);
        
    }

}
