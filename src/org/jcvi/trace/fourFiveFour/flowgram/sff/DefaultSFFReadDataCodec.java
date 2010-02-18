/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;

public class DefaultSFFReadDataCodec implements SFFReadDataCodec {

    @Override
    public SFFReadData decode(DataInputStream in, int numberOfFlows, int numberOfBases) throws SFFDecoderException {
        try{
            short[] values = IOUtil.readShortArray(in, numberOfFlows);
            byte[] indexes = IOUtil.readByteArray(in, numberOfBases);
            String bases = new String(IOUtil.readByteArray(in, numberOfBases));
            byte[] qualities = IOUtil.readByteArray(in, numberOfBases);

            int readDataLength = SFFUtil.getReadDataLength(numberOfFlows, numberOfBases);
            int padding =SFFUtil.caclulatePaddedBytes(readDataLength);
            IOUtil.blockingSkip(in, padding);
            return new DefaultSFFReadData(bases, indexes, values,qualities);
        }
        catch(IOException e){
            throw new SFFDecoderException("error decoding read data", e);
        }

    }

    

    public byte[] encode(SFFReadData readData){
        int basesLength =readData.getBasecalls().length();
        int numberOfFlows = readData.getFlowgramValues().length;
        int readDataLength = SFFUtil.getReadDataLengthIncludingPadding(numberOfFlows, basesLength);
        ByteBuffer buf = ByteBuffer.wrap(new byte[readDataLength]);
        IOUtil.putShortArray(buf, readData.getFlowgramValues());
        buf.put(readData.getFlowIndexPerBase());
        buf.put(readData.getBasecalls().getBytes());
        buf.put(readData.getQualities());
        return buf.array();
    }



}
