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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NoAmbiguitiesEncodedNucleotideCodec;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideCodec;
import org.jcvi.jillion.core.residue.nt.Nucleotides;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestNoAmbiguitiesEncodedNucleotideCodec {

    NucleotideCodec sut = NoAmbiguitiesEncodedNucleotideCodec.INSTANCE;
    
    @Test
    public void canOnlyEncodedACGTAndGap(){
        assertTrue(NoAmbiguitiesEncodedNucleotideCodec.canEncode(Arrays.asList(
                Nucleotide.Adenine,
                Nucleotide.Cytosine,
                Nucleotide.Guanine,
                Nucleotide.Thymine,
                Nucleotide.Gap
                )));
    }
    
    @Test
    public void encode(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGT");
        byte[] actual =sut.encode(nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    private void assertDecodeByIndexIsCorrect(List<Nucleotide> expected, byte[] actual){
    	assertEquals(expected.size(), sut.decodedLengthOf(actual));
       for(int i=0; i<expected.size(); i++){
           Nucleotide actualBase = sut.decode(actual, i);
           assertEquals("" +i,expected.get(i),actualBase);
       }        
    }
    @Test
    public void lastByteHasOnly1Base(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGTC");
        byte[] actual =sut.encode(nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    
    @Test
    public void lastByteHasOnly2Bases(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGTCA");
        byte[] actual =sut.encode(nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    @Test
    public void lastByteHasOnly3Bases(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGTCAG");
        byte[] actual =sut.encode(nucleotides);
        
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
        sut.encode(Nucleotide.Strong);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void hasAmbiguityShouldThrowIllegalArgumentException(){
        sut.encode(Nucleotides.parse("ACGTWACGT"));
    }
    
    @Test
    public void encodeWithOneGap(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGT-ACGT");
        byte[] actual =sut.encode(nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    @Test
    public void encodeWithTwoGaps(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGT-AC-GT");
        byte[] actual =sut.encode(nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    /**
     * Gaps in each of the 4 offsets in a byte
     */
    @Test
    public void encodeWithFourGaps(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGT-CGTA-GTAC-TACG-ACGT");
        byte[] actual =sut.encode(nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void encodeWithTwoConsecutiveGaps(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGT--AC-GT");
        byte[] actual =sut.encode(nucleotides);
        
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void gapOffsetsEncodedAsShorts(){
        int size = 2* Byte.MAX_VALUE+1;
        List<Nucleotide> longBases = new ArrayList<Nucleotide>(size);
        for(int i=0; i< Byte.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("C"));
        }
        longBases.add(Nucleotide.Gap);
        for(int i=0; i< Byte.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("T"));
        }
        
        byte[] actual =sut.encode(longBases);
        assertDecodeByIndexIsCorrect(longBases, actual);    
    }
    @Test
    public void gapOffsetsEncodedAsInts(){
        int size = 2* Short.MAX_VALUE+1;
        List<Nucleotide> longBases = new ArrayList<Nucleotide>(size);
        for(int i=0; i< Short.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("C"));
        }
        longBases.add(Nucleotide.Gap);
        for(int i=0; i< Short.MAX_VALUE ;i++){
            longBases.add(Nucleotide.parse("T"));
        }
        
        byte[] actual =sut.encode(longBases);
        assertDecodeByIndexIsCorrect(longBases, actual);    
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getBeyondLengthShouldThrowException(){
    	 List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGT");
         byte[] actual =sut.encode(nucleotides);
         sut.decode(actual, 10);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void negativeOffsetShouldThrowException(){
    	 List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGT");
         byte[] actual =sut.encode(nucleotides);
         sut.decode(actual, -1);
    }
    
    @Test
    public void iterator(){
    	List<Nucleotide> list = Nucleotides.parse("ACG-ACGT");
		assertIterateCorrectly(list);
    }
    @Test
    public void iteratorLastByteHasOnly1Base(){
    	List<Nucleotide> list = Nucleotides.parse("ACG-ACGTC");
		assertIterateCorrectly(list);
    }
    @Test
    public void iteratorLastByteHasOnly2Bases(){
    	List<Nucleotide> list = Nucleotides.parse("ACGT-CGTCA");
		assertIterateCorrectly(list);
    }
    @Test
    public void iteratorLastByteHasOnly3Bases(){
    	List<Nucleotide> list = Nucleotides.parse("ACGT-CGTCAG");
		assertIterateCorrectly(list);
    }
    
    private void assertIterateCorrectly(List<Nucleotide> list){
    	assertIterateCorrectly(list, new Range.Builder(list.size()).build());
    }
	private void assertIterateCorrectly(List<Nucleotide> list, Range range) {
		Iterator<Nucleotide> expected = list.iterator();
		byte[] bytes =sut.encode(list);
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
    	List<Nucleotide> list = Nucleotides.parse("ACG-ACGT");
		assertIterateCorrectly(list, Range.of(3,6));
    }
    @Test
    public void rangedIteratorLastByteHasOnly1Base(){
    	List<Nucleotide> list = Nucleotides.parse("ACG-ACGTC");
		assertIterateCorrectly(list, Range.of(3,7));
    }
    @Test
    public void rangedIteratorLastByteHasOnly2Bases(){
    	List<Nucleotide> list = Nucleotides.parse("ACGT-CGTCA");
		assertIterateCorrectly(list, Range.of(3,7));
    }
    @Test
    public void rangedIteratorLastByteHasOnly3Bases(){
    	List<Nucleotide> list = Nucleotides.parse("ACGT-CGTCAG");
		assertIterateCorrectly(list, Range.of(3,8));
    }
}
