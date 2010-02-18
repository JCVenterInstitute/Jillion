/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestEncodedByteGlyph {
    ByteGlyphFactory<ByteGlyph> FACTORY = new ByteGlyphFactory<ByteGlyph>(){

        @Override
        protected ByteGlyph createNewGlyph(Byte b) {
            return new ByteGlyph(b);
        }
        
    };
    byte[] bytes = new byte[]{-20,40,Byte.MAX_VALUE,Byte.MIN_VALUE,21,86,99,0,4};
    List<ByteGlyph> glyphs = FACTORY.getGlyphsFor(bytes);
    
    EncodedByteGlyphs sut = new EncodedByteGlyphs(glyphs);
    
    @Test
    public void decode(){
        assertEquals(glyphs, sut.decode());
    }
    @Test
    public void length(){
        assertEquals(bytes.length, sut.getLength());
    }
    
    @Test
    public void getIndex(){
        assertEquals(glyphs.get(0), sut.get(0));
        assertEquals(glyphs.get(3), sut.get(3));
        assertEquals(glyphs.get(8), sut.get(8));
    }
}
