/*
 * Created on Oct 13, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFCommonHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultSFFCommonHeaderCodec_encode extends AbstractTestDefaultSFFCommonHeaderCodec {


    @Test
    public void validEncode(){
        byte[] expectedBytes = encode(expectedHeader);
        byte[] actualBytes =sut.encodeHeader(expectedHeader);
        assertArrayEquals(expectedBytes, actualBytes);
    }

    private byte[] encode(DefaultSFFCommonHeader expectedHeader) {
        final short keyLength =(short) (expectedHeader.getKeySequence().length());
        int size = 31+expectedHeader.getNumberOfFlowsPerRead()+ keyLength;
        int padding =SFFUtil.caclulatePaddedBytes(size);
        ByteBuffer buf = ByteBuffer.wrap(new byte[size+padding]);
        buf.put(".sff".getBytes());
        buf.put(new byte[]{0,0,0,1});
        buf.put(IOUtil.convertUnsignedLongToByteArray(expectedHeader.getIndexOffset()));
        buf.put(IOUtil.convertUnsignedIntToByteArray(expectedHeader.getIndexLength()));
        buf.put(IOUtil.convertUnsignedIntToByteArray(expectedHeader.getNumberOfReads()));
        buf.putShort((short)(size+padding));
        buf.putShort(keyLength);
        buf.put(IOUtil.convertUnsignedShortToByteArray(expectedHeader.getNumberOfFlowsPerRead()));
        buf.put((byte)1);
        buf.put(expectedHeader.getFlow().getBytes());
        buf.put(expectedHeader.getKeySequence().getBytes());
        return buf.array();
    }
}
