/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.position;

import static org.junit.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.position.ShortPositionStrategy;
import org.junit.Test;

public class TestShortPositionStrategy {
    ShortPositionStrategy sut = new ShortPositionStrategy();
    @Test
    public void maxAllowedValue(){
        assertEquals(Short.MAX_VALUE, sut.getMaxAllowedValue());
    }

    @Test
    public void getSampleSize(){
        assertEquals(2, sut.getSampleSize());
    }

    @Test
    public void getPosition() throws IOException{
        InputStream in = createMock(InputStream.class);
        short expectedShort = 200;
        //a short is 2 consecutive bytes read
        //most sig bits
        expect(in.read()).andReturn(expectedShort>>>8);
        //least sig bits
        expect(in.read()).andReturn(expectedShort & 0xFF);
        replay(in);
        assertEquals(expectedShort, sut.getPosition(new DataInputStream(in)));
        verify(in);
    }

    @Test
    public void setPosition(){
        final short value = (short)200;
        ByteBuffer mockBuffer = createMock(ByteBuffer.class);
        expect(mockBuffer.putShort(value)).andReturn(mockBuffer);
        replay(mockBuffer);
        sut.setPosition(value, mockBuffer);
        verify(mockBuffer);
    }
}
