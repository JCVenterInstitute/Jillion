/*
 * Created on Nov 3, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;


/**
 * The <code>BPOSChunk</code> Chunk contains the positions of the
 * bases (peaks)stored as ints.
 * @author dkatzel
 *
 *
 */
public class BPOSChunk extends Chunk {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
            throws org.jcvi.trace.TraceDecoderException {
        final int numberOfBases = (unEncodedData.length -1)/4;
        ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
        ByteBuffer input = ByteBuffer.wrap(unEncodedData);
        //skip padding
        input.position(4);
        while(input.hasRemaining()){
            peaks.put((short) input.getInt());
        }
        builder.peaks(peaks.array());

    }

}
