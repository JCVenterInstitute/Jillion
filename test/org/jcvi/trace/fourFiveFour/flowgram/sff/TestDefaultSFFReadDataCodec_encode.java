/*
 * Created on Oct 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadData;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
public class TestDefaultSFFReadDataCodec_encode extends AbstractTestSFFReadDataCodec{

    @Test
    public void valid(){
        byte[] expectedBytes = encode(expectedReadData);
        assertArrayEquals(expectedBytes, sut.encode(expectedReadData));
    }

    private byte[] encode(SFFReadData readData){
        int basesLength =readData.getBasecalls().length();
        int numberOfFlows = readData.getFlowgramValues().length;
        int readDataLength = numberOfFlows * 2 + 3*basesLength;
        int padding =SFFUtil.caclulatePaddedBytes(readDataLength);
        ByteBuffer buf = ByteBuffer.wrap(new byte[readDataLength+padding]);
        IOUtil.putShortArray(buf, readData.getFlowgramValues());
        buf.put(readData.getFlowIndexPerBase());
        buf.put(readData.getBasecalls().getBytes());
        buf.put(readData.getQualities());
        return buf.array();
    }
}
