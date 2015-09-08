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
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestBasicNucleotideCodec {
    List<Nucleotide> evenBases = Nucleotides.parse("ACGTACGTWS-NACGT");
    List<Nucleotide> oddBases =  Nucleotides.parse("ACGTACGTWS-NACGTA");
    
    BasicNucleotideCodec sut = BasicNucleotideCodec.INSTANCE;
    
    @Test
    public void evenEncodesAndDecodes(){
        byte[] encoded =Nucleotides.encodeWithGapSentientals(sut, evenBases);
        assertEquals(evenBases, decode(encoded));
    }
    
    @Test
    public void oddEncodesAndDecodes(){
        byte[] encoded =Nucleotides.encodeWithGapSentientals(sut, oddBases);
        assertEquals(oddBases, decode(encoded));
    }
    
    @Test
    public void isGap(){
    	assertIsGapCorrect(Nucleotides.encodeWithGapSentientals(sut, evenBases));
        assertIsGapCorrect(Nucleotides.encodeWithGapSentientals(sut, oddBases));
    }

	private void assertIsGapCorrect(byte[] encoded) {
		assertTrue(sut.isGap(encoded, 10));
        assertFalse(sut.isGap(encoded, 9));
        assertFalse(sut.isGap(encoded, 11));
	}
    /**
     * this is a regression test for a bug where I was getting
     * the sign wrong for final odd byte values.  C is the base
     * that caused this error in production.
     */
    @Test
    public void oddNumberOfBasesEndsWithC(){
        assertFinalBaseInOddLengthSequenceCorrectlyDecoded("C");
    }
   
    private void assertFinalBaseInOddLengthSequenceCorrectlyDecoded(
            String finalBase) {
        String basesString = "ATTTGCTATCCATA"+finalBase;
        List<Nucleotide> expectedGlyphs = Nucleotides.parse(basesString);
        byte[] encoded =Nucleotides.encodeWithGapSentientals(sut, expectedGlyphs);
        assertEquals("did not decode final base of "+finalBase + " correctly",
                expectedGlyphs, decode(encoded));
    }
    
    private List<Nucleotide> decode(byte[] encodedBytes){
    	List<Nucleotide> list = new ArrayList<Nucleotide>();
    	Iterator<Nucleotide> iter = sut.iterator(encodedBytes);
    	while(iter.hasNext()){
    		list.add(iter.next());
    	}
    	return list;
    }
    
    
    /**
     * to go along with {@link #oddNumberOfBasesEndsWithC()}
     * try every possible base ending
     */
    @Test
    public void testOddNumberOfBasesEveryPossibleFinalBase(){
        for(Nucleotide nuc : Nucleotide.VALUES){
            assertFinalBaseInOddLengthSequenceCorrectlyDecoded(nuc.toString());
        }
    }
    
    @Test
    public void noBases(){
        byte[] encoded = Nucleotides.encodeWithGapSentientals(sut, Collections.<Nucleotide>emptyList());
        assertTrue(decode(encoded).isEmpty());
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getBeyondLengthShouldThrowException(){
    	 List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGT");
         byte[] actual =Nucleotides.encodeWithGapSentientals(sut, nucleotides);
         sut.decode(actual, 10);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeOffsetShouldThrowException(){
    	 List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGT");
         byte[] actual =Nucleotides.encodeWithGapSentientals(sut, nucleotides);
         sut.decode(actual, -1);
    }
    
    @Test
    public void evenIterator(){
		assertIterateCorrectly(evenBases);
    }
    @Test
    public void oddIterator(){
		assertIterateCorrectly(oddBases);
    }
    
    private void assertIterateCorrectly(List<Nucleotide> list){
    	assertIterateCorrectly(list, new Range.Builder(list.size()).build());
    }
	private void assertIterateCorrectly(List<Nucleotide> list, Range range) {
		Iterator<Nucleotide> expected = list.iterator();
		byte[] bytes =Nucleotides.encodeWithGapSentientals(sut, list);
		Iterator<Nucleotide> actual = sut.iterator(bytes, range);
		for(int i=0; i<range.getBegin(); i++){
			expected.next();
		}
		for(int i=0; i<range.getLength(); i++){
			assertTrue(expected.hasNext());
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
		try{
			actual.next();
			fail("should throw NoSuchElementException when done iterating");
		}catch(NoSuchElementException e){
			//expected
		}
	}
	
	@Test
	public void evenToString(){
		byte[] encodedBytes = Nucleotides.encodeWithGapSentientals(sut, evenBases);
		assertEquals("ACGTACGTWS-NACGT", sut.toString(encodedBytes).toString());
	}
	@Test
	public void oddToString(){
		byte[] encodedBytes = Nucleotides.encodeWithGapSentientals(sut, oddBases);
		assertEquals("ACGTACGTWS-NACGTA", sut.toString(encodedBytes).toString());
	}
	
	@Test
    public void evenRangedIterator(){
		assertIterateCorrectly(evenBases, Range.of(10,12));
    }
    @Test
    public void oddRangedIterator(){
		assertIterateCorrectly(oddBases,Range.of(10,16));
    }
    
}
