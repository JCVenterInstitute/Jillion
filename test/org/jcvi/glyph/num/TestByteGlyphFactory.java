/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestByteGlyphFactory {

    ByteGlyphFactory<ByteGlyph>  sut = new ByteGlyphFactory<ByteGlyph>(){

        @Override
        protected ByteGlyph createNewGlyph(Byte b) {
            return new ByteGlyph(b);
        }
        
    };
    
    byte[] byteArray = new byte[]{10,20,30,40,27,66,127, -120};
    @Test
    public void getGlyphsForArray(){
        List<ByteGlyph> actual =sut.getGlyphsFor(byteArray);
        List<ByteGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }
    
    @Test
    public void getGlyphsForList(){
        List<ByteGlyph> actual =sut.getGlyphsFor(convertToArray(byteArray));
        List<ByteGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }

    private List<ByteGlyph> buildExpectedGlyphList() {
        List<ByteGlyph> expected = new ArrayList<ByteGlyph>(byteArray.length);
        for(int i=0; i<byteArray.length; i++){
            expected.add(sut.getGlyphFor(byteArray[i]));
        }
        return expected;
    }
    
    private static List<Byte> convertToArray(byte[] array){
        List<Byte> result = new ArrayList<Byte>(array.length);
        for(int i=0; i<array.length; i++){
            result.add(Byte.valueOf(array[i]));
        }
        return result;
    }
}
