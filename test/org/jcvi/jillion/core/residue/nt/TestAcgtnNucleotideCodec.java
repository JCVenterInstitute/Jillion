/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestAcgtnNucleotideCodec {
    NucleotideCodec sut = AcgtnNucloetideCodec.INSTANCE;
    
    
    private void assertDecodeByIndexIsCorrect(List<Nucleotide> expected, byte[] actual){
    	assertEquals(expected.size(), sut.decodedLengthOf(actual));
        for(int i=0; i<expected.size(); i++){
            Nucleotide actualBase = sut.decode(actual, i);
            assertEquals("" +i,expected.get(i),actualBase);
        }        
     }

    private List<Nucleotide> asList(NucleotideSequenceBuilder builder){
    	List<Nucleotide> list = new ArrayList<Nucleotide>((int)builder.getLength());
    	for(Nucleotide n : builder){
    		list.add(n);
    	}
    	return list;
    }
    
    @Test
    public void getUngappedLength(){
    	 List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTNCGTANGTACNTACGNACGT"));
         byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
         assertEquals(nucleotides.size(), sut.getUngappedLength(actual));
    }
    
    @Test
    public void alwaysSaysNumGapsIs0(){
    	byte[] fakeData = new byte[10];
    	assertEquals(0, sut.getNumberOfGaps(fakeData));
    	assertTrue(sut.getGapOffsets(fakeData).isEmpty());
    	assertFalse(sut.isGap(fakeData, 123));
    	assertEquals(0, sut.getNumberOfGapsUntil(fakeData, 123));
    	assertEquals(123, sut.getGappedOffsetFor(fakeData, 123));
    	assertEquals(123, sut.getUngappedOffsetFor(fakeData, 123));
    }
    
    @Test
    public void encode(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTACGT"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void lastByteHasOnly1Base(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTACGTC"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    
    @Test
    public void lastByteHasOnly2Bases(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTACGTCA"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    @Test
    public void lastByteHasOnly3Bases(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTACGTCAG"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    
    @Test
    public void oneBase(){        
        byte[] actual =sut.encode(Nucleotide.Cytosine);
        List<Nucleotide> nucleotides = Arrays.asList(Nucleotide.Cytosine);
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void oneBaseAmbiguityShouldThrowIllegalArgumentException(){
    	Nucleotides.encodeWithNSentientals(sut, Collections.singletonList(Nucleotide.Strong));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void hasAmbiguityShouldThrowIllegalArgumentException(){
    	Nucleotides.encodeWithNSentientals(sut, asList(new NucleotideSequenceBuilder("ACGTWACGT")));
    }
    
    
    //////////////////////
    @Test
    public void encodeWithOneN(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTNACGT"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    @Test
    public void encodeWithTwoNs(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTNACNGT"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    /**
     * Ns in each of the 4 offsets in a byte
     */
    @Test
    public void encodeWithFourNs(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTNCGTANGTACNTACGNACGT"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void encodeWithTwoConsecutiveNs(){
        List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTNNACNGT"));
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void NOffsetsEncodedAsShorts(){
        int size = 2* Byte.MAX_VALUE+1;
        List<Nucleotide> longBases = new ArrayList<Nucleotide>(size);
        for(int i=0; i< Byte.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("C"));
        }
        longBases.add(Nucleotide.Unknown);
        for(int i=0; i< Byte.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("T"));
        }
        
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, longBases);
        assertDecodeByIndexIsCorrect(longBases, actual);    
    }
    @Test
    public void NOffsetsEncodedAsInts(){
        int size = 2* Short.MAX_VALUE+1;
        List<Nucleotide> longBases = new ArrayList<Nucleotide>(size);
        for(int i=0; i< Short.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("C"));
        }
        longBases.add(Nucleotide.Unknown);
        for(int i=0; i< Short.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("T"));
        }
        
        byte[] actual =Nucleotides.encodeWithNSentientals(sut, longBases);
        assertDecodeByIndexIsCorrect(longBases, actual);    
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getBeyondLengthShouldThrowException(){
    	 List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTACGT"));
         byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
         sut.decode(actual, 10);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeOffsetShouldThrowException(){
    	 List<Nucleotide> nucleotides = asList(new NucleotideSequenceBuilder("ACGTACGT"));
         byte[] actual =Nucleotides.encodeWithNSentientals(sut, nucleotides);
         sut.decode(actual, -1);
    }
    
    
    @Test
    public void iterator(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGNACGT"));
		assertIterateCorrectly(list);
    }
    @Test
    public void iteratorLastByteHasOnly1Base(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGNACGTC"));
		assertIterateCorrectly(list);
    }
    @Test
    public void iteratorLastByteHasOnly2Bases(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGTNCGTCA"));
		assertIterateCorrectly(list);
    }
    @Test
    public void iteratorLastByteHasOnly3Bases(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGTNCGTCAG"));
		assertIterateCorrectly(list);
    }
    

    private void assertIterateCorrectly(List<Nucleotide> list){
    	assertIterateCorrectly(list, new Range.Builder(list.size()).build());
    }
	private void assertIterateCorrectly(List<Nucleotide> list, Range range) {
		Iterator<Nucleotide> expected = list.iterator();
		byte[] bytes =Nucleotides.encodeWithNSentientals(sut, list);
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
    public void rangedIterator(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGNACGT"));
		assertIterateCorrectly(list, Range.of(2,6));
    }
    @Test
    public void rangedIteratorLastByteHasOnly1Base(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGNACGTC"));
		assertIterateCorrectly(list, Range.of(4,7));
    }
    @Test
    public void rangedIteratorLastByteHasOnly2Bases(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGTNCGTCA"));
		assertIterateCorrectly(list, Range.of(4,7));
    }
    @Test
    public void rangedIteratorLastByteHasOnly3Bases(){
    	List<Nucleotide> list = asList(new NucleotideSequenceBuilder("ACGTNCGTCAG"));
		assertIterateCorrectly(list, Range.of(4,7));
    }
    
}
