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
package org.jcvi.jillion.trace.chromat.ztr.data;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDelta32Data {
    private static byte[] uncompressedArray = new byte[]{16,32,49,16, 0,50,90,-80, 127,127,64,48};
    DeltaEncodedData sut = DeltaEncodedData.INTEGER;
    @Test
    public void level1(){
        IntBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asIntBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(16);
        compressed.put((byte)66);
        compressed.put((byte)1);  //level
        compressed.putShort((short)0); //padding
        int delta=0;
        int prevValue=0;
        while(uncompressed.hasRemaining()){
            delta = prevValue;
            prevValue = uncompressed.get();
            compressed.putInt(prevValue -delta);
        }        
        compressed.flip();
       
        //test

        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
    
    @Test
    public void level2(){
        IntBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asIntBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(16);
        compressed.put((byte)66);
        compressed.put((byte)2);  //level
        compressed.putShort((short)0); //padding
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        while(uncompressed.hasRemaining()){
            delta = 2*prevValue -prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = uncompressed.get();
            compressed.putInt(prevValue -delta);
        }        
        compressed.flip();
       
        //test
        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
    
    @Test
    public void level3(){
        IntBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asIntBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(16);
        compressed.put((byte)66);
        compressed.put((byte)3);  //level
        compressed.putShort((short)0); //padding
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        int prevPrevPrevValue =0;
        while(uncompressed.hasRemaining()){
            delta = 3*prevValue - 3*prevPrevValue + prevPrevPrevValue;
            prevPrevPrevValue= prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = uncompressed.get();
            compressed.putInt(prevValue -delta);
        }        
        compressed.flip();
       
        //test
        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
}
