/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestShortGlyphFactory {
    ShortGlyphFactory sut = ShortGlyphFactory.getInstance();
    
    short[] shortArray = new short[]{10,20,30,40,27,66,127, -120};
    @Test
    public void getGlyphsForArray(){
        List<ShortGlyph> actual =sut.getGlyphsFor(shortArray);
        List<ShortGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }
    
    @Test
    public void getGlyphsForList(){
        List<ShortGlyph> actual =sut.getGlyphsFor(convertToArray(shortArray));
        List<ShortGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }

    private List<ShortGlyph> buildExpectedGlyphList() {
        List<ShortGlyph> expected = new ArrayList<ShortGlyph>(shortArray.length);
        for(int i=0; i<shortArray.length; i++){
            expected.add(sut.getGlyphFor(shortArray[i]));
        }
        return expected;
    }
    
    private static List<Short> convertToArray(short[] array){
        List<Short> result = new ArrayList<Short>(array.length);
        for(int i=0; i<array.length; i++){
            result.add(Short.valueOf(array[i]));
        }
        return result;
    }
}
