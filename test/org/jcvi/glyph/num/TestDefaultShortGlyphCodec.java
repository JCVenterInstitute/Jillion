/*
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDefaultShortGlyphCodec {
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    
    private static final short[] decodedShorts = new short[]{12345,10,0,Short.MAX_VALUE, Short.MIN_VALUE,-255,256,5000};

    private static final List<ShortGlyph> decodedGlyphs = FACTORY.getGlyphsFor(decodedShorts);
    
    private static byte[] encodedShortsAsByteArray;
    
    @BeforeClass
    public static void createByteArray(){
        ByteBuffer buf = ByteBuffer.allocate(decodedShorts.length *2);
        for(int i=0; i<decodedShorts.length; i++){
            buf.putShort(decodedShorts[i]);
        }
        encodedShortsAsByteArray = buf.array();
    }
    
    DefaultShortGlyphCodec sut = DefaultShortGlyphCodec.getInstance();
    @Test
    public void decode(){
        List<ShortGlyph> actualGlyphs =sut.decode(encodedShortsAsByteArray);
        assertEquals(decodedGlyphs, actualGlyphs);
    }
    
    @Test
    public void encode(){
        byte[] actualEncodedBytes =sut.encode(decodedGlyphs);
        assertArrayEquals(encodedShortsAsByteArray, actualEncodedBytes);
    }
    
    @Test
    public void length(){
        assertEquals(decodedShorts.length, sut.decodedLengthOf(encodedShortsAsByteArray));
    }
    
    @Test
    public void indexedDecode(){
        for(int i=0; i<decodedShorts.length; i++){
            assertEquals( decodedGlyphs.get(i).getNumber(), sut.decode(encodedShortsAsByteArray, i).getNumber());
        }
        
    }
}
