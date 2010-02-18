/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

public class TestIOUtil_readShortArray {

    short[] shortArray = new short[]{130, 20,260, 6500};
    byte[] expectedByteArray;
    @org.junit.Before
    public void setup(){
        ByteBuffer buf = ByteBuffer.allocate(shortArray.length *2);
        for(int i=0; i< shortArray.length; i++){
            buf.putShort(shortArray[i]);
        }
        expectedByteArray = buf.array();
    }
    @Test
    public void valid() throws IOException{
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(expectedByteArray));
        short[] actualArray = IOUtil.readShortArray(in, shortArray.length);
        assertTrue(Arrays.equals(shortArray, actualArray));
    }

    @Test
    public void didNotReadEnough(){
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(expectedByteArray));
        try {
            IOUtil.readShortArray(in, shortArray.length+1);
            fail("if did not read exected length should throw IOException");
        } catch (IOException expected) {

        }

    }
}
