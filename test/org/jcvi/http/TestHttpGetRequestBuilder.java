/*
 * Created on Sep 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestHttpGetRequestBuilder {

    HttpGetRequestBuilder sut;
    String urlBase = "http://example.com";
    @Before
    public void setup(){
        sut = new HttpGetRequestBuilder(urlBase);
    }
    @Test(expected = NullPointerException.class)
    public void nullKeyShouldThrowNPE() throws UnsupportedEncodingException{
        sut.addVariable(null);
    }
    @Test
    public void addVarible() throws IOException{
        sut.addVariable("var");
        assertEquals(urlBase+"?var",
                sut.build().getURL().toString());
    }
    
    @Test
    public void addVaribleWithValue() throws IOException{
        sut.addVariable("key","value");
        assertEquals(urlBase+"?key=value",
                sut.build().getURL().toString());
    }
    @Test
    public void add2VariblesWithValue() throws IOException{
        sut.addVariable("key1","value1")
        .addVariable("key2", "value2");
        assertEquals(urlBase+"?key1=value1&key2=value2",
                sut.build().getURL().toString());
    }
    @Test
    public void addEncodedVariable() throws IOException{
        sut.addVariable("$variable&That/Needs+To,Be;Encoded","@value=is?encoded");
        assertEquals(urlBase+"?%24variable%26That%2FNeeds%2BTo%2CBe%3BEncoded=%40value%3Dis%3Fencoded",
                sut.build().getURL().toString());
    }
}
