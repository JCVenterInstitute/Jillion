/*
 * Created on May 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestIOUtil_UnsignedByteArray {

    private short[] expectedShortArray = new short[]{255, 200, 0, 14, 128,56,254,127};
   
    @Test
    public void read() throws IOException{
        ByteBuffer buf = ByteBuffer.allocate(expectedShortArray.length);
        for(int i=0; i<expectedShortArray.length; i++){
            buf.put((byte) expectedShortArray[i]);
        }
        short[] actualShortArray = IOUtil.readUnsignedByteArray(new ByteArrayInputStream(buf.array()), expectedShortArray.length);
        assertTrue(Arrays.equals(expectedShortArray, actualShortArray));
    }
    
    @Test
    public void put(){
        ByteBuffer mockBuffer = createMock(ByteBuffer.class);
        for(int i=0; i<expectedShortArray.length; i++){
            expect(mockBuffer.put((byte) expectedShortArray[i])).andReturn(mockBuffer);
        }
        replay(mockBuffer);
        IOUtil.putUnsignedByteArray(mockBuffer, expectedShortArray);
        verify(mockBuffer);
    }
}
