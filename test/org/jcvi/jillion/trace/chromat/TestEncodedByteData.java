/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import java.util.Arrays;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestEncodedByteData {

    private byte[] data = new byte[]{10,20,30,40,50,80};
    EncodedByteData sut = new EncodedByteData(data);
    @Test
    public void arrayConstructor(){
        assertTrue(Arrays.equals(data, sut.getData()));
    }
    @Test
    public void nullArrayConstrcutorShouldThrowIllegalArgumentException(){
        try{
           new EncodedByteData((byte[])null);
           fail("should throw illegal argument excpetion when constructor passed null");
        }
        catch(IllegalArgumentException expected){
            assertEquals("data can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullStringConstrcutorShouldThrowIllegalArgumentException(){
        try{
           new EncodedByteData((String)null);
           fail("should throw illegal argument excpetion when constructor passed null");
        }
        catch(IllegalArgumentException expected){
            assertEquals("data can not be null", expected.getMessage());
        }
    }
    @Test
    public void invalidEncodedDataShouldThrowNumberFormatException(){
        try{
            new EncodedByteData("not a list of bytes");
            fail("should throw NumberFormatException when can't parse out byte values");
         }
         catch(NumberFormatException expected){
         }
    }
    @Test
    public void encodeValuesTooBig(){
        try{
            new EncodedByteData("100,200,300,400,500");
            fail("should throw NumberFormatException when values out of byte Range");
         }
         catch(NumberFormatException expected){
         }
    }
    @Test
    public void StringConstructor(){
        EncodedByteData encoded = new EncodedByteData(encodeData(data));
        assertArrayEquals(data, encoded.getData());
    }
    @Test
    public void StringWithNoWhiteSpaceConstructor(){
        final String noWhiteSpace = encodeData(data).replaceAll(", ", ",");
        EncodedByteData sut = new EncodedByteData(noWhiteSpace);
        assertArrayEquals(data, sut.getData());
    }
    @Test
    public void StringExtraNoWhiteSpaceConstructor(){
        final String extraWhiteSpace = encodeData(data).replaceAll(", ", ",\t  \t");
        EncodedByteData sut = new EncodedByteData(extraWhiteSpace);
        assertArrayEquals(data, sut.getData());
    }
    @Test
    public void StringCarriageReturnsConstructor(){
        final String withCarriageReturns = encodeData(data).replaceAll(", ", ",\n");
        EncodedByteData sut = new EncodedByteData(withCarriageReturns);
        assertArrayEquals(data, sut.getData());
    }

    @Test
    public void encode(){
        String expectedEncodedData = encodeData(data);
        EncodedByteData sut = new EncodedByteData(data);
        assertEquals(expectedEncodedData, sut.encodeData());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void notEqualsNotEncodedData(){
        assertFalse(sut.equals("not EncodedData"));
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }

    @Test
    public void equalsSameValues(){
        EncodedByteData sameValues = new EncodedByteData(data);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentData(){
        byte[] differentData =Arrays.copyOf(data, data.length-1);
        EncodedByteData differentValues = new EncodedByteData(differentData);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    private String encodeData(byte[] array){
        StringBuilder result = new StringBuilder();
        for(int i=0 ; i < array.length-1; i++){
            result.append(array[i]);
            result.append(", ");
        }
        result.append(array[array.length-1]);
        return result.toString();
    }
}
