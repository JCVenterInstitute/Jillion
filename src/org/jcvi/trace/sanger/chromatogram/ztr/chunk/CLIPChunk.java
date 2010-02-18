/*
 * Created on Nov 6, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.Range;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;

/**
 * <code>CLIPChunk</code> contains the suggested quality clip points (0- based).
 * @author dkatzel
 *
 *
 */
public class CLIPChunk extends Chunk {
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
            throws TraceDecoderException {
        if(unEncodedData.length !=9){
            throw new TraceDecoderException("Invalid DefaultClip size, num of bytes = " +unEncodedData.length );
        }
        ByteBuffer buf = ByteBuffer.wrap(unEncodedData);
        buf.position(1); //skip padding
        builder.clip(Range.buildRange(buf.getInt(), buf.getInt()));
    }

}
