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
package org.jcvi.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestStringUtilities {

    @Test
    public void testToCamelCase() 
    {
        assertEquals("Standard Title", "thisIsCamelCase", StringUtilities.toCamelCase("This is Camel Case").toString());
        assertEquals("Single Word", "test", StringUtilities.toCamelCase("Test").toString());
        assertEquals("Leading whitespace", "thisIsATest", StringUtilities.toCamelCase("   This is a Test").toString());
        assertEquals("Trailing whitespace", "thisIsATest", StringUtilities.toCamelCase("This is a Test   ").toString());
        assertEquals("Digits", "digit89Test", StringUtilities.toCamelCase("digit 89 test").toString());
        assertEquals("Standard Title (InitialCap)", "ThisIsCamelCase", StringUtilities.toCamelCase("This is Camel Case", true).toString());
        assertEquals("Leading whitespace (InitialCap)", "ThisIsATest", StringUtilities.toCamelCase("   This is a Test", true).toString());
        assertEquals("Trailing whitespace (InitialCap)", "ThisIsATest", StringUtilities.toCamelCase("This is a Test   ", true).toString());
    }
    
    @Test
    public void isNumber(){
        assertTrue("single digit",StringUtilities.isNumber("1"));
        assertTrue("multiple digits",StringUtilities.isNumber("12345"));
        assertTrue("decimal point",StringUtilities.isNumber("12.345"));
        assertTrue("leading decimal point",StringUtilities.isNumber(".345"));
        assertFalse("letter",StringUtilities.isNumber("A"));
        assertFalse("word",StringUtilities.isNumber("nope"));
        assertFalse("sentence",StringUtilities.isNumber("not a number"));
    }
    
    @Test
    public void joinBuilderStringsWithNoGlue(){
        assertEquals("LarryMoeCurly",
                new StringUtilities.JoinedStringBuilder("Larry","Moe","Curly").build());
    }
    
    @Test
    public void joinBuilderStringsWithGlue(){
        assertEquals("Larry,Moe,Curly",
                new StringUtilities.JoinedStringBuilder("Larry","Moe","Curly")
                        .glue(",")
                        .build());
    }
    
    @Test
    public void joinBuilderObjectsWithNoGlue(){
        assertEquals("LarryMoeCurly",
                new StringUtilities.JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"}).build());
    }
    
    @Test
    public void joinBuilderObjectsWithGlue(){
        assertEquals("Larry,Moe,Curly",
                new StringUtilities.JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .build());
    }
    @Test
    public void joinBuilderObjectsWithPrefix(){
        assertEquals("Stooges=Larry,Moe,Curly",
                new StringUtilities.JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .prefix("Stooges=")
                        .build());
    }
    @Test
    public void joinBuilderObjectsWithSuffix(){
        assertEquals("Larry,Moe,Curly were the best stooges",
                new StringUtilities.JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .suffix(" were the best stooges")
                        .build());
    }
    

}
