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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.qual;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;
import org.junit.BeforeClass;
import org.junit.Test;
public class TestRunLengthEncodedGlyphCodec {

    static private byte guard = Byte.valueOf((byte)70);

	private static final byte[] QUALITY_BYTES = new byte[]{10,20,30,40,40,40,40,40,40,50,6,guard,12,15,guard,guard,30};
    
    RunLengthEncodedQualityCodec sut = new RunLengthEncodedQualityCodec(guard);
    
    static List<PhredQuality> decodedValues = asList(QUALITY_BYTES);
    static byte[] expected;
    
    @BeforeClass
    public static void setup(){
        int numberOfGuards =3;
        int numberOfRepeatedValues = 1;
        int numberOfNonRepeatedValues = 8;
        //longest runlength can easily fit in byte
        final int expectedSize = 6+ numberOfNonRepeatedValues + 
                                (numberOfGuards *2) +  (numberOfRepeatedValues *3);
        ByteBuffer buf = ByteBuffer.allocate(expectedSize);
        buf.putInt(QUALITY_BYTES.length);
        buf.put(guard);
        buf.put((byte)ValueSizeStrategy.BYTE.ordinal());
        buf.put((byte)10);
        buf.put((byte)20);
        buf.put((byte)30);
        buf.put(guard);
        buf.put((byte)6);
        buf.put((byte)40);
        buf.put((byte)50);
        buf.put((byte)6);
        buf.put(guard);
        buf.put((byte)0);
        buf.put((byte)12);
        buf.put((byte)15);
        buf.put(guard);
        buf.put((byte)0);
        buf.put(guard);
        buf.put((byte)0);
        buf.put((byte)30);
        
        expected = buf.array();
    }
    
    @Test
    public void encode(){
        byte[] actual =sut.encode(decodedValues);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void decodeLengthOf(){
        assertEquals(decodedValues.size(), sut.decodedLengthOf(expected));
    }
    
    @Test
    public void decodeIndex(){
        for(int i=0; i< decodedValues.size(); i++){
            assertEquals(Integer.toString(i),decodedValues.get(i), sut.decode(expected, i));
        }
    }
    
    public static List<PhredQuality> asList(byte[] bytes){
        List<PhredQuality> list = new ArrayList<PhredQuality>(bytes.length);
        for(int i=0; i<bytes.length; i++){
            list.add(PhredQuality.valueOf(bytes[i]));
        }
        return list;
    }
    
    @Test
    public void toArray(){
    	byte[] array =sut.toQualityValueArray(expected);
    	assertArrayEquals(QUALITY_BYTES, array);
    }
}
