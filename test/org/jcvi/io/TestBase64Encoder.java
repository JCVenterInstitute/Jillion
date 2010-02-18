/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import org.junit.Test;

import sun.misc.BASE64Encoder;
import static org.junit.Assert.*;
public class TestBase64Encoder {
    //example text taken from Wikipedia Base64 article and is text of Thomas Hobbes' Leviathan 
    private final String sampleText = 
        "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";

    private final String expectedBase64 = 
        "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\n"+
        "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\n"+
        "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\n"+
        "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\n"+
        "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
    
    @Test
    public void sunBase64Encode(){
        BASE64Encoder sunEncoder = new BASE64Encoder();
        assertEquals(expectedBase64, sunEncoder.encode(sampleText.getBytes()));
    }
    
    @Test
    public void encoder(){
        assertEquals(expectedBase64, Base64.encode(sampleText.getBytes()));
    }
    
    @Test
    public void twoPadds(){
        String twoPadsInput = sampleText.substring(0, sampleText.length()-1);
        String encoded2Pads = Base64.encode(twoPadsInput.getBytes());
        assertTrue(encoded2Pads.endsWith("c3VyZQ=="));
    }
    @Test
    public void noPadds(){
        String twoPadsInput = sampleText.substring(0, sampleText.length()-2);
        String encoded2Pads = Base64.encode(twoPadsInput.getBytes());
        assertTrue(encoded2Pads.endsWith("c3Vy"));
    }
    @Test
    public void onePadds(){
        String twoPadsInput = sampleText.substring(0, sampleText.length()-3);
        String encoded2Pads = Base64.encode(twoPadsInput.getBytes());
        assertTrue(encoded2Pads.endsWith("c3U="));
    }
    
}
