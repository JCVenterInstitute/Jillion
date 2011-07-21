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
package org.jcvi.common.core.symbol.pos;

/*
 * Created on Jul 30, 2007
 *
 * @author dkatzel
 */


import java.util.Arrays;

import org.jcvi.common.core.symbol.IllegalEncodedValueException;
import org.jcvi.common.core.symbol.pos.TigrPeaksEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTigrPeaksEncoder {


    /**
     * @param expected
     * @param encodedSequence
     * @throws IllegalEncodedValueException
     */
    private void assertPositionsDecodedCorrectly(short[] expected, String encodedSequence) throws IllegalEncodedValueException {
        short[] actual =TigrPeaksEncoder.decode(encodedSequence);

        assertTrue("expected "+Arrays.toString(expected) + " did  not equal actual " +
                Arrays.toString(actual),
                Arrays.equals(expected, actual));

    }

    private void assertPositionsDecodedThrowsIllegalEncodedValueException(String encodedSequence){
        assertNotNull(encodedSequence);
        try{
            TigrPeaksEncoder.decode(encodedSequence);
            fail("decoder should throw IllegalEncodedValueException");
        }
        catch(IllegalEncodedValueException e){
          //expected
        }


    }
    @Test
    public void testEncode(){
        short positions[] = new short[]{20};
        String expected = ""+(char)(20 +TigrPeaksEncoder.ENCODING_ORIGIN) +TigrPeaksEncoder.TERMINATOR_CHAR;

        String actual = TigrPeaksEncoder.encode(positions);
        assertEquals(expected,actual);

    }
    @Test
    public void testEncode_Empty(){
        short positions[] = new short[]{};
        String expected = ""+TigrPeaksEncoder.TERMINATOR_CHAR;

        String actual = TigrPeaksEncoder.encode(positions);
        assertEquals(expected,actual);
    }
    @Test
    public void testEncode_twoPositions(){
        short positions[] = new short[]{20,30};
        String expected = ""+(char)(20 +TigrPeaksEncoder.ENCODING_ORIGIN)+(char)(10 +TigrPeaksEncoder.ENCODING_ORIGIN) +TigrPeaksEncoder.TERMINATOR_CHAR;

        String actual = TigrPeaksEncoder.encode(positions);
        assertEquals(expected,actual);

    }
    @Test
    public void testEncode_negativeDelta_shouldBeUuencoded(){
        short positions[] = new short[]{-1};
        String expected = new String(
                new char[]{
                        TigrPeaksEncoder.FLAG_CHAR,
                        '_',
                        '_',
                        '_',
                        '_',
                        TigrPeaksEncoder.TERMINATOR_CHAR
                });

        String actual = TigrPeaksEncoder.encode(positions);
        assertEquals(expected,actual);
    }
    @Test
    public void testEncode_GreaterThanMaxBareDelta_shouldBeUuencoded(){
        short positions[] = new short[]{TigrPeaksEncoder.MAX_BARE_DELTA +1};
        String expected = new String(
                new char[]{
                        TigrPeaksEncoder.FLAG_CHAR,
                        ' ',
                        ' ',
                        ' ',
                        '_',
                        TigrPeaksEncoder.TERMINATOR_CHAR
                });

        String actual = TigrPeaksEncoder.encode(positions);
        assertEquals(expected,actual);

    }
    @Test
    public void testEncode_uuencodedDeltaSurroundedByBareDeltas(){
        short positions[] = new short[]{
                    20,
                    20 +TigrPeaksEncoder.MAX_BARE_DELTA +1,
                    20 +TigrPeaksEncoder.MAX_BARE_DELTA +1+40
                    };
        String expected = new String(
                new char[]{
                        20 +TigrPeaksEncoder.ENCODING_ORIGIN,

                        TigrPeaksEncoder.FLAG_CHAR,
                        ' ',
                        ' ',
                        ' ',
                        '_',

                        40 +TigrPeaksEncoder.ENCODING_ORIGIN,
                        TigrPeaksEncoder.TERMINATOR_CHAR
                });

        String actual = TigrPeaksEncoder.encode(positions);
        assertEquals(expected,actual);
    }
    @Test
    public void testUuencode_negative(){
        char[] actual = TigrPeaksEncoder.uuencode(-1);
        char[] expected = new char[]{'_','_','_','_'};

        assertTrue(Arrays.equals(expected, actual));
    }
    @Test
    public void testUuencode_GreaterThanMaxDelta(){
        char[] actual = TigrPeaksEncoder.uuencode(TigrPeaksEncoder.MAX_BARE_DELTA +1);
        char[] expected = new char[]{' ',' ',' ','_'};

        assertTrue(Arrays.equals(expected, actual));
    }
    @Test
    public void testUuencode(){
        char[] actual = TigrPeaksEncoder.uuencode(200);
        char[] expected = new char[]{' ',' ','#','('};
        assertTrue(Arrays.equals(expected, actual));
    }
    @Test
    public void testUudecode(){
        int expected = 200;
        int actual = TigrPeaksEncoder.uudecode(
                new char[]{' ',' ','#','('}
                );

        assertEquals(expected, actual);
    }
    @Test
    public void testUudecode_negative(){
        int expected = Integer.decode("0xFFFFFF");
        int actual = TigrPeaksEncoder.uudecode(
                new char[]{'_','_','_','_'}
                );


        assertEquals(expected, actual);
    }
    @Test
    public void testUudecode_GreaterThanMaxDelta(){

        int expected =TigrPeaksEncoder.MAX_BARE_DELTA +1;
        int actual = TigrPeaksEncoder.uudecode(
                new char[]{' ',' ',' ','_'}
                );

        assertEquals(expected, actual);
    }

    @Test
    public void testDecode() throws Exception{

        short[] expected = new short[]{20};
        String encodedSequence = new String(new char[]{
                20 +TigrPeaksEncoder.ENCODING_ORIGIN,
                TigrPeaksEncoder.TERMINATOR_CHAR
        });
        assertPositionsDecodedCorrectly(expected, encodedSequence);
    }
    @Test
    public void testDecode_twoPositions() throws Exception{

        short[] expected = new short[]{20,30};
        String encodedSequence = new String(new char[]{
                20 +TigrPeaksEncoder.ENCODING_ORIGIN,
                10 +TigrPeaksEncoder.ENCODING_ORIGIN,
                TigrPeaksEncoder.TERMINATOR_CHAR
        });
        assertPositionsDecodedCorrectly(expected, encodedSequence);
    }
    @Test
    public void testDecode_JustTerminator() throws Exception{

        short[] expected = new short[]{};
        String encodedSequence = new String(new char[]{
                TigrPeaksEncoder.TERMINATOR_CHAR
        });
        assertPositionsDecodedCorrectly(expected, encodedSequence);
    }
    @Test
    public void testDecode_emtpy() throws Exception{
        assertPositionsDecodedThrowsIllegalEncodedValueException("");

    }
    @Test
    public void testDecode_null_shouldThrowNullPointerException() throws Exception{
        try{
            TigrPeaksEncoder.decode(null);
            fail("decoder should throw null pointer when encodedString param is null");
        }
        catch(NullPointerException e){
            assertEquals(e.getMessage(), "encodedString can not be null");
        }
    }
    @Test
    public void testDecode_noTerminator() throws Exception{

        String encodedSequence = new String(new char[]{
                20 +TigrPeaksEncoder.ENCODING_ORIGIN

        });
        assertPositionsDecodedThrowsIllegalEncodedValueException(encodedSequence);
    }

    @Test
    public void testDecode_GreaterThanMaxBareDelta() throws Exception{
        short expected[] = new short[]{TigrPeaksEncoder.MAX_BARE_DELTA +1};
        String encodedSequence = new String(
                new char[]{
                        TigrPeaksEncoder.FLAG_CHAR,
                        ' ',
                        ' ',
                        ' ',
                        '_',
                        TigrPeaksEncoder.TERMINATOR_CHAR
                });
        assertPositionsDecodedCorrectly(expected, encodedSequence);

    }
    @Test
    public void testDecode_uuEncodedSurroundedByBareDeltas() throws Exception{
        short expected[] = new short[]{
                20,
                20 +TigrPeaksEncoder.MAX_BARE_DELTA +1,
                20 +TigrPeaksEncoder.MAX_BARE_DELTA +1+40
                };
        String encodedSequence = new String(
            new char[]{
                    20 +TigrPeaksEncoder.ENCODING_ORIGIN,

                    TigrPeaksEncoder.FLAG_CHAR,
                    ' ',
                    ' ',
                    ' ',
                    '_',

                    40 +TigrPeaksEncoder.ENCODING_ORIGIN,
                    TigrPeaksEncoder.TERMINATOR_CHAR
            });

        assertPositionsDecodedCorrectly(expected, encodedSequence);

    }
    @Test
    public void testDecode_negativeDelta_negOne() throws Exception{
        short expected[] = new short[]{20,19,59};
        String encodedSequence = new String(
                new char[]{
                      20  +TigrPeaksEncoder.ENCODING_ORIGIN,
                      TigrPeaksEncoder.FLAG_CHAR,
                      '_',
                      '_',
                      '_',
                      '_',
                      40 +TigrPeaksEncoder.ENCODING_ORIGIN,
                      TigrPeaksEncoder.TERMINATOR_CHAR

                }
        );

        assertPositionsDecodedCorrectly(expected, encodedSequence);
    }
    @Test
    public void testDecode_negativeDelta_largeNegative() throws Exception{
        short expected[] = new short[]{200,10,59};

        StringBuilder encodedSequence = new StringBuilder();
        encodedSequence.append(TigrPeaksEncoder.FLAG_CHAR);
        encodedSequence.append(TigrPeaksEncoder.uuencode(200));
        encodedSequence.append(TigrPeaksEncoder.FLAG_CHAR);
        encodedSequence.append(TigrPeaksEncoder.uuencode(-190));
        encodedSequence.append((char)(49 + TigrPeaksEncoder.ENCODING_ORIGIN));
        encodedSequence.append(TigrPeaksEncoder.TERMINATOR_CHAR);

        assertPositionsDecodedCorrectly(expected, encodedSequence.toString());


    }

    @Test
    public void testDecodedEncoded() throws Exception{
        short expected[] = new short[]{
                20,
                20 +TigrPeaksEncoder.MAX_BARE_DELTA +1,
                20 +TigrPeaksEncoder.MAX_BARE_DELTA +1+40
                };

        assertTrue(Arrays.equals(expected,
                TigrPeaksEncoder.decode(TigrPeaksEncoder.encode(expected))));
    }




}

