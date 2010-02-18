/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;
import java.util.List;

import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLengthEncodedGlyphCodec {

    static private byte guard = Byte.valueOf((byte)70);
    
    RunLengthEncodedGlyphCodec sut = new RunLengthEncodedGlyphCodec(guard);
    
    static List<PhredQuality> decodedValues = PhredQuality.valueOf(
            new byte[]{10,20,30,40,40,40,40,40,40,50,6,guard,12,15,guard,guard,30});
    static byte[] expected;
    
    @BeforeClass
    public static void setup(){
        int numberOfGuards =3;
        int numberOfRepeatedValues = 1;
        int numberOfNonRepeatedValues = 8;
        final int expectedSize = 4+1+ numberOfNonRepeatedValues + 
                                (numberOfGuards *3) +  (numberOfRepeatedValues *4);
        ByteBuffer buf = ByteBuffer.allocate(expectedSize);
        buf.putInt(decodedValues.size());
        buf.put(guard);
        buf.put((byte)10);
        buf.put((byte)20);
        buf.put((byte)30);
        buf.put(guard);
        buf.putShort((short)6);
        buf.put((byte)40);
        buf.put((byte)50);
        buf.put((byte)6);
        buf.put(guard);
        buf.putShort((short)0);
        buf.put((byte)12);
        buf.put((byte)15);
        buf.put(guard);
        buf.putShort((short)0);
        buf.put(guard);
        buf.putShort((short)0);
        buf.put((byte)30);
        
        expected = buf.array();
    }
    
    @Test
    public void encode(){
        byte[] actual =sut.encode(decodedValues);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void decode(){
        List<PhredQuality> actual =sut.decode(expected);
        assertEquals(decodedValues, actual);
    }
    
    @Test
    public void decodeLengthOf(){
        assertEquals(decodedValues.size(), sut.decodedLengthOf(expected));
    }
    
    @Test
    public void decodeIndex(){
        for(int i=0; i< decodedValues.size(); i++){
            assertEquals(decodedValues.get(i), sut.decode(expected, i));
        }
    }
}
