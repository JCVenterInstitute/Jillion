package org.jcvi.common.core.seq.read.trace.sanger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestDefaultPositionCodec {
   
    private static final short[] shorts = new short[]{12345,10,0,Short.MAX_VALUE, 60,99,256,5000};

    private static final List<Position> decodedGlyphs;
    
    
    
    private static byte[] encodedShortsAsByteArray;
    
    static{
    	decodedGlyphs = new ArrayList<Position>(shorts.length);
    	for(int i=0; i<shorts.length; i++){
    		decodedGlyphs.add(Position.valueOf(shorts[i]));
    	}
    }
    
    @BeforeClass
    public static void createByteArray(){
        ByteBuffer buf = ByteBuffer.allocate(shorts.length *2);
        for(int i=0; i<shorts.length; i++){
            buf.putShort(shorts[i]);
        }
        encodedShortsAsByteArray = buf.array();
    }
    
    DefaultPositionCodec sut = DefaultPositionCodec.INSTANCE;
    @Test
    public void decode(){
        List<Position> actualGlyphs =sut.decode(encodedShortsAsByteArray);
        assertEquals(decodedGlyphs, actualGlyphs);
    }
    
    @Test
    public void encode(){
        byte[] actualEncodedBytes =sut.encode(decodedGlyphs);
        assertArrayEquals(encodedShortsAsByteArray, actualEncodedBytes);
    }
    
    @Test
    public void length(){
        assertEquals(shorts.length, sut.decodedLengthOf(encodedShortsAsByteArray));
    }
    
    @Test
    public void indexedDecode(){
        for(int i=0; i<shorts.length; i++){
            assertEquals( decodedGlyphs.get(i), sut.decode(encodedShortsAsByteArray, i));
        }
    }
    
    @Test
    public void encodeValuesUptoUnsignedShortMax(){
    	List<Position> positions = Arrays.asList(
    						Position.valueOf(1234),
    						Position.valueOf(Short.MAX_VALUE),
    						Position.valueOf(Short.MAX_VALUE+1),
    						Position.valueOf(2*Short.MAX_VALUE - 1));
    	
    	byte[] actual = sut.encode(positions);
    	assertEquals(positions, sut.decode(actual));
    }
}
