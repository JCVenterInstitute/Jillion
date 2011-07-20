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
package org.jcvi.common.internal;


import java.util.Arrays;

import static org.junit.Assert.*;

import org.jcvi.common.core.symbol.IllegalEncodedValueException;
import org.jcvi.common.internal.TigrQualitiesEncoder;
import org.junit.ComparisonFailure;
import org.junit.Test;


/**
* @author jsitz
* @author dkatzel
*
*/
public class TestTigrQualitiesEncoder
{
 

   private short[] byteArrayToShort(byte[] bytes)
   {
       if(bytes ==null){
           throw new NullPointerException("array can not be null");
       }
       final short[] shorts = new short[bytes.length];
       for (int i = 0; i < bytes.length; i++)
       {
           shorts[i] = bytes[i];
       }
       return shorts;
   }


   private void assertEncodedBytesCorrectly(final byte[] qualities,final String expected){
       //convert to short array first
       this.assertEncodedShortsCorrectly(byteArrayToShort(qualities), expected);
   }

   private void assertEncodedShortsCorrectly(final short[] qualities,final String expected){
       if(expected ==null){
           throw new NullPointerException("expected can not be null");
       }
       final String actual = TigrQualitiesEncoder.encode(qualities);
       assertNotNull(actual);
       assertEquals(expected, actual);
   }
   @Test
   public void testByteArrayToShort(){
       assertTrue(Arrays.equals(new short[]{10, 20, 30, 40},
               byteArrayToShort(new byte[]{10, 20, 30, 40})));
   }
   @Test
   public void testByteArrayToShort_null(){
       try{
          byteArrayToShort(null);
          fail("expected NullPointerException");
       }
       catch(NullPointerException e){
           //expected
       }
   }
   @Test
   public void testByteArrayToShort_empty(){
       assertTrue(Arrays.equals(new short[]{},
               byteArrayToShort(new byte[]{})));
   }

   @Test
   public void testAssertEncodedBytesCorrectly_nullExpected_shouldThrowNullPointerException(){
       try{
       assertEncodedBytesCorrectly(new byte[]{10, 20, 30, 40},null);
       fail("should throw null pointer if expected is null");
       }
       catch(NullPointerException e){
           //expected
       }
   }
   @Test
   public void testAssertEncodedBytesCorrectly_emptyArray(){

       assertEncodedBytesCorrectly(new byte[]{},"");

   }
   @Test
   public void testAssertEncodedBytesCorrectly(){

       final byte[] qual = {10, 20, 30, 40};
       assertEncodedBytesCorrectly(qual,":DNX");

   }
   @Test
   public void testAssertEncodedBytesCorrectly_notEqual_shouldThrowComparisonFailure(){

       final byte[] qual = {10, 20, 30, 40};
       try{
           assertEncodedBytesCorrectly(qual,"Not equal");
           fail("not equal should throw assertion error");
       }
       catch(ComparisonFailure cf){
           //expected
       }


   }

   @Test
   public void testByteEncode()
   {
       final byte[] qual = {10, 20, 30, 40};
       assertEncodedBytesCorrectly(qual,":DNX");
   }

   @Test
   public void testShortEncode()
   {
       final short[] qual = {10, 20, 30, 40};

       assertEncodedShortsCorrectly(qual,":DNX");

   }
   @Test
   public void testShortEncode_actualNull_ExpectEmptyString()
   {

       assertEncodedShortsCorrectly(null,"");
   }
   @Test
   public void testShortEncode_MinQuality()
   {
       final short[] qual = {TigrQualitiesEncoder.MIN_QUALITY};
       assertEncodedShortsCorrectly(qual,"0");
   }
   @Test
   public void testShortEncode_BelowMinQuality_encodesToMinQuality()
   {
       final short[] qual = {TigrQualitiesEncoder.MIN_QUALITY-1};
       assertEncodedShortsCorrectly(qual,"0");
   }
   @Test
   public void testShortEncode_MaxQuality()
   {
       final short[] qual = {TigrQualitiesEncoder.MAX_QUALITY};
       assertEncodedShortsCorrectly(qual,"l");
   }
   @Test
   public void testShortEncode_OverMaxQuality_encodesToMaxQuality()
   {
       final short[] qual = {TigrQualitiesEncoder.MAX_QUALITY+1};
       assertEncodedShortsCorrectly(qual,"l");
   }
   @Test
   public void testShortEncode_LegacyEditedQuality_encodesToEditedQuality()
   {
       final short[] qual = {TigrQualitiesEncoder.LEGACY_EDITED_QUALITY};
       assertEncodedShortsCorrectly(qual,"X");
   }
   @Test
   public void testShortEncode_EditedQuality()
   {
       final short[] qual = {TigrQualitiesEncoder.EDITED_QUALITY};
       assertEncodedShortsCorrectly(qual,"X");
   }



   @Test
   public void testDecode() throws Exception
   {
       final String test = "IaMFLiM";
       final byte[] expected = { 25, 49, 29, 22, 28, 57, 29 };

       final byte[] actual = TigrQualitiesEncoder.decode(test);
       assertNotNull(actual);
       assertTrue(Arrays.equals(expected,actual));
   }

   @Test 
   public void testIllegalDecode_shouldThrowIllegalEncodedValueException()
   {
       final String test = "IaMFLiMx123";

       try
       {
           TigrQualitiesEncoder.decode(test);

           // We should throw an exception
           fail("Failed to throw an exception on invalid input.");
       }
       catch (IllegalEncodedValueException e)
       {
           // Do nothing.  We expect this exception.
       }

   }
   @Test 
   public void testDecode_AboveMaxQual_shouldThrowIllegalEncodedValueException()
   {
       final String test = new String(new char[]{TigrQualitiesEncoder.MAX_QUALITY+TigrQualitiesEncoder.ENCODING_ORIGIN+1});

       try
       {
           TigrQualitiesEncoder.decode(test);

           // We should throw an exception
           fail("Failed to throw an exception on invalid input.");
       }
       catch (IllegalEncodedValueException e)
       {
           // Do nothing.  We expect this exception.
       }

   }
   @Test 
   public void testDecode_BelowMinQual_shouldThrowIllegalEncodedValueException()
   {
       final String test = new String(new char[]{(char)(TigrQualitiesEncoder.MIN_QUALITY -1)});

       try
       {
           TigrQualitiesEncoder.decode(test);

           // We should throw an exception
           fail("Failed to throw an exception on invalid input.");
       }
       catch (IllegalEncodedValueException e)
       {
           // Do nothing.  We expect this exception.
       }

   }

   @Test 
   public void testDecodeEncoded() throws Exception{
       String expected = "IaMFLiM";
       assertEquals(
               "re-encoding decoded string should return same result",
               expected,
               TigrQualitiesEncoder.encode(TigrQualitiesEncoder.decode(expected)));
   }
}

