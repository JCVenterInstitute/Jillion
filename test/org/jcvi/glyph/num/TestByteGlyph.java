/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestByteGlyph {
    byte value = (byte)99;
    ByteGlyph sut = new ByteGlyph(value);
    
    @Test
    public void getNumber(){
        assertEquals(Byte.valueOf(value), sut.getNumber());
    }
    @Test
    public void getName(){
        assertEquals(Byte.valueOf(value).toString(), sut.getName());
    }
}
