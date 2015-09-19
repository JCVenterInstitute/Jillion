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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;

public class TestClipChunk {

   Chunk sut = Chunk.CLIP;
   static final Range expectedClip = Range.of(12345678, 987654321);

    private static final byte[] encodedClip;
    static{    	
    	encodedClip = encode(expectedClip);
    }
	private static byte[] encode(Range clip) {
		ByteBuffer buf = ByteBuffer.allocate(9);
        buf.put((byte)0); // clip chunk
        buf.putInt((int)clip.getBegin());
        buf.putInt((int)clip.getEnd());
        byte[] temp = buf.array();
		return temp;
	}
    @Test
    public void validParse() throws IOException{
       
        
        ZtrChromatogramBuilder mockStruct = new ZtrChromatogramBuilder("id");
        sut.parseData(encodedClip,mockStruct);
        assertEquals(expectedClip, mockStruct.clip());
    }
    
    @Test
    public void encode() throws IOException{
    	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
    	expect(mockChromatogram.getClip()).andReturn(expectedClip);
    	replay(mockChromatogram);
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encodedClip, actual);
    	verify(mockChromatogram);
    }
    @Test
    public void encodeNullClipShouldEncodeZeroZero() throws IOException{
    	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
    	expect(mockChromatogram.getClip()).andReturn(null);
    	replay(mockChromatogram);
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encode(Range.of(0,0)), actual);
    	verify(mockChromatogram);
    }
    @Test
    public void invalidLengthTooSmallShouldThrowIOException(){
        try{
            sut.parseData(new byte[8], (ZtrChromatogramBuilder)null);
            fail("should throw exception if array length < 9");
        }catch(IOException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 8", e.getMessage());
        }
    }
    @Test
    public void invalidLengthTooBigShouldThrowIOException(){
        try{
            sut.parseData(new byte[10], (ZtrChromatogramBuilder)null);
            fail("should throw exception if array length > 9");
        }catch(IOException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 10", e.getMessage());
        }
    }
}
