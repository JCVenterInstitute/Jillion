/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestIOUtil_readByteArray {

    byte[] array = new byte[]{20,15,30,40};

    @Test
    public void valid() throws IOException{
        InputStream in = new ByteArrayInputStream(array);
        byte[] actualArray = IOUtil.readByteArray(in, array.length);
        assertTrue(Arrays.equals(array, actualArray));
    }
    @Test
    public void didNotReadEnough() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(array.length+1))).andReturn(array.length);
        expect(mockInputStream.read(isA(byte[].class), eq(array.length), eq(1))).andReturn(-1);
        replay(mockInputStream);
        try {
            IOUtil.readByteArray(mockInputStream, array.length+1);
            fail("if did not read exected length should throw IOException");
        } catch (IOException e) {
            String expectedMessage = "only was able to read "
                + array.length + "expected "+ (array.length+1);
            assertEquals(expectedMessage, e.getMessage());
        }

    }
}
