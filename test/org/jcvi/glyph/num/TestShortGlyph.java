/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestShortGlyph {
    short value = 5000;
    ShortGlyph sut = new ShortGlyph(value);
    
    @Test
    public void getNumber(){
        assertEquals(Short.valueOf(value), sut.getNumber());
    }
    @Test
    public void getName(){
        assertEquals(Short.valueOf(value).toString(), sut.getName());
    }
}
