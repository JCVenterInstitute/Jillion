/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestBase64Encoder {
    //example text taken from Wikipedia Base64 article and is text of Thomas Hobbes' Leviathan 
    //permanent link:
    //http://en.wikipedia.org/w/index.php?title=Base64&oldid=363359790
    private final String sampleText = 
        "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";

    private final String expectedBase64 = 
        "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz"+ String.format("%n")+
        "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg"+ String.format("%n")+
        "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu"+ String.format("%n")+
        "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo"+ String.format("%n")+
        "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
    
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
