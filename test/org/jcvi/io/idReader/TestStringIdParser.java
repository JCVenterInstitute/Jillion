/*
 * Created on Jun 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestStringIdParser {

    StringIdParser sut = new StringIdParser();
    
    @Test
    public void nullIsNotValid(){
        assertFalse(sut.isValidId(null));
    }
    @Test
    public void nonNullIsValid(){
        assertTrue(sut.isValidId("not null"));
    }
    
    @Test
    public void parseReturnsParameter(){
        String string = "something";
        assertEquals(string, sut.parseIdFrom(string));
    }
    @Test(expected = NullPointerException.class)
    public void parsedNullThrowsNullPointerException(){
        sut.parseIdFrom(null);
    }
}
