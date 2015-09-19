/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.pos;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.internal.trace.chromat.scf.header.pos.BytePositionStrategy;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
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
