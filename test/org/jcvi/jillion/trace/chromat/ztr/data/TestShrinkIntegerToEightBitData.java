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
 * Created on Dec 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ShrinkToEightBitData;
import org.junit.Test;
public class TestShrinkIntegerToEightBitData {
    static int[] intValues = new int[]{20,400,12345678, Short.MAX_VALUE,0, -4, -12345678};
    static byte[] uncompressed = new byte[intValues.length *4];
    static byte[] compressed = new byte[24];
    static byte guard = -128;
    static{
        ByteBuffer buf = ByteBuffer.wrap(uncompressed);
        for(int i =0 ; i<intValues.length; i++){
            buf.putInt(intValues[i]);
        }
        
        ByteBuffer buf2 = ByteBuffer.wrap(compressed);
        buf2.put((byte)71);
        buf2.put((byte)20);
        buf2.put(guard);
        buf2.putInt(400);
        buf2.put(guard);
        buf2.putInt(12345678);
        buf2.put(guard);
        buf2.putInt(Short.MAX_VALUE);
        buf2.put((byte)0);
        buf2.put((byte)-4);
        buf2.put(guard);
        buf2.putInt(-12345678);
    }
    
    @Test
    public void decode() throws IOException{
        Data sut = ShrinkToEightBitData.INTEGER_TO_BYTE;
        byte[] actual =sut.parseData(compressed);
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
    @Test
    public void encode() throws IOException{
    	Data sut = ShrinkToEightBitData.INTEGER_TO_BYTE;
    	byte[] actual = sut.encodeData(uncompressed);
    	assertTrue(Arrays.equals(actual, compressed));
    }
    
}
