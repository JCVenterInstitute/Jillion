/*
 * Created on Nov 3, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;



/**
 * The <code>BASEChunk</code> contains the actual base calls
 * for this Chromatogram.
 * @author dkatzel
 *
 *
 */
public class BASEChunk extends Chunk {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
            throws TraceDecoderException {
        //first byte is padding
        final int numberOfBases = unEncodedData.length -1;
        ByteBuffer buf = ByteBuffer.allocate(numberOfBases);
        buf.put(unEncodedData, 1, numberOfBases);
        builder.basecalls(new String(buf.array()));

    }

}
