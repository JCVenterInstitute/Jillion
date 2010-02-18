/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.io.IOUtil;

public class DefaultSFFReadHeaderCodec implements SFFReadHeaderCodec {
    private static final byte[] EMPTY_CLIP = new byte[]{0,0,0,0};
    @Override
    public SFFReadHeader decodeReadHeader(DataInputStream in)
            throws SFFDecoderException {
        try{
            short headerLength = in.readShort();
            short nameLegnth = in.readShort();
            int numBases = in.readInt();
            short qualLeft = in.readShort();
            short qualRight = in.readShort();
            short adapterLeft = in.readShort();
            short adapterRight = in.readShort();
            String name = readSequenceName(in,nameLegnth);
            int bytesReadSoFar = 16+nameLegnth;
            int padding =SFFUtil.caclulatePaddedBytes(bytesReadSoFar);
            IOUtil.blockingSkip(in, padding);
            return new DefaultSFFReadHeader(headerLength, numBases,
                    Range.buildRange(CoordinateSystem.RESIDUE_BASED, qualLeft, qualRight),
                    Range.buildRange(CoordinateSystem.RESIDUE_BASED, adapterLeft, adapterRight),
                     name);
        }
        catch(IOException e){
            throw new SFFDecoderException("error trying to decode read header",e);
        }
    }


    private String readSequenceName(DataInputStream in, short length) throws IOException, SFFDecoderException {
        byte[] name = new byte[length];
        int bytesRead = in.read(name);
        if(bytesRead != length){
            throw new SFFDecoderException("error decoding seq name");
        }
        return new String(name);
    }

    public byte[] encodeReadHeader(SFFReadHeader readHeader){
        //I wrap a newly allocated byte array
        //so that it is automatically filled with zeros
        //this allows me to not worry about padding.
        ByteBuffer buf = ByteBuffer.wrap(new byte[readHeader.getHeaderLength()]);
        buf.putShort(readHeader.getHeaderLength());
        buf.putShort((short)readHeader.getName().length());
        buf.putInt(readHeader.getNumberOfBases());
        encodeClip(buf, readHeader.getQualityClip());
        encodeClip(buf, readHeader.getAdapterClip());
        buf.put(readHeader.getName().getBytes());
        return buf.array();

    }


    private void encodeClip(ByteBuffer buf, final Range clip) {
        if(clip ==null){
            buf.put(EMPTY_CLIP);
        }
        else{
            buf.putShort((short)clip.getStart());
            buf.putShort((short)clip.getEnd());
        }
    }
}
