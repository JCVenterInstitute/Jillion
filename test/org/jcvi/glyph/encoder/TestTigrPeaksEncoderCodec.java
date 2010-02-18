/*
 * Created on Sep 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jcvi.TestUtil;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTigrPeaksEncoderCodec {

    private static final short[] PEAKS_AS_SHORTS = new short[]{8,15,24,36,49,60};
    
    private static byte[] EXPECTED_ENCODED_PEAKS;
    @BeforeClass
    public static void setup() throws UnsupportedEncodingException{
        EXPECTED_ENCODED_PEAKS =TigrPeaksEncoder.encode(PEAKS_AS_SHORTS).getBytes("US-ASCII");
    }
    
    TigrPeaksEncoderGlyphCodec sut = TigrPeaksEncoderGlyphCodec.getInstance();
    
    List<ShortGlyph> peaks = ShortGlyphFactory.getInstance().getGlyphsFor(
                            PEAKS_AS_SHORTS);
    
    @Test
    public void encode(){
        byte[] actual =sut.encode(peaks);
        assertArrayEquals(EXPECTED_ENCODED_PEAKS, actual);
    }
    
    @Test
    public void decode(){
        assertEquals(peaks, sut.decode(EXPECTED_ENCODED_PEAKS));
    }
    
    @Test
    public void length(){
        assertEquals(peaks.size(), sut.decodedLengthOf(EXPECTED_ENCODED_PEAKS));
    }
    
    @Test
    public void decodeIndex(){
        for(int i=0; i< peaks.size(); i++){
            ShortGlyph actual =sut.decode(EXPECTED_ENCODED_PEAKS, i);
            assertEquals(peaks.get(i), actual);
        }
    }
}
