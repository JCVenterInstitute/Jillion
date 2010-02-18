/*
 * Created on Mar 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.util.List;

import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTigrQualitiesEncoderCodec {

    byte[] qualitiesAsBytes = new byte[]{10,20,30,40,50,60,23,55};
    List<PhredQuality> qualities = PhredQuality.valueOf(qualitiesAsBytes);
    String encodedQualities = ":DNXblGg";
    
    TigrQualitiesEncodedGyphCodec sut = TigrQualitiesEncodedGyphCodec.getINSTANCE();
    
    @Test
    public void encode(){
        assertEquals(encodedQualities, new String(sut.encode(qualities)));
    }
    
    @Test
    public void decode(){
        assertEquals(qualities, sut.decode(encodedQualities.getBytes()));
    }
    @Test
    public void length(){
        assertEquals(qualitiesAsBytes.length, sut.decodedLengthOf(encodedQualities.getBytes()));
    }
    @Test
    public void indexedDecode(){
        byte[] encodedBytes = encodedQualities.getBytes();
        for(int i=0; i<encodedQualities.length(); i++){
            assertEquals(qualities.get(i), sut.decode(encodedBytes, i));
        }
    }
}
