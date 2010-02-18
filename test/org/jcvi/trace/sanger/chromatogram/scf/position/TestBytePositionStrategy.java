/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.position;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.position.BytePositionStrategy;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestBytePositionStrategy {
    BytePositionStrategy sut = new BytePositionStrategy();
    @Test
    public void maxAllowedValue(){
        assertEquals(Byte.MAX_VALUE, sut.getMaxAllowedValue());
    }

    @Test
    public void getSampleSize(){
        assertEquals(1, sut.getSampleSize());
    }

    @Test
    public void getPosition() throws IOException{
        InputStream in = createMock(InputStream.class);
        byte expectedByte = 16;
        expect(in.read()).andReturn((int)expectedByte);
        replay(in);
        assertEquals(expectedByte, sut.getPosition(new DataInputStream(in)));
        verify(in);
    }
    @Test
    public void setPositionTooBigShouldThrowIllegalArgumentException() {
        final short value = (short)(Byte.MAX_VALUE+1);
        try{
            sut.setPosition(value, null);
            fail("should throw illegalArgumentException if value is > max byte");
        }
        catch(IllegalArgumentException expected){
            assertEquals("position to put is too big :"+ value, expected.getMessage());
        }
    }
    @Test
    public void setPosition(){
        final short value = (short)10;
        ByteBuffer mockBuffer = createMock(ByteBuffer.class);
        expect(mockBuffer.put((byte)value)).andReturn(mockBuffer);
        replay(mockBuffer);
        sut.setPosition(value, mockBuffer);
        verify(mockBuffer);
    }

}
