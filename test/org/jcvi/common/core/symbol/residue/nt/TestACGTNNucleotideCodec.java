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

package org.jcvi.common.core.symbol.residue.nt;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.symbol.residue.nt.ACGTNNucloetideCodec;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideCodec;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestACGTNNucleotideCodec {
    NucleotideCodec sut = ACGTNNucloetideCodec.INSTANCE;
    
    
    private void assertDecodeByIndexIsCorrect(List<Nucleotide> expected, byte[] actual){
        for(int i=0; i<expected.size(); i++){
            Nucleotide actualBase = sut.decode(actual, i);
            assertEquals("" +i,expected.get(i),actualBase);
        }        
     }
    
    @Test
    public void encode(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void lastByteHasOnly1Base(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGTC");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    
    @Test
    public void lastByteHasOnly2Bases(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGTCA");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    @Test
    public void lastByteHasOnly3Bases(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTACGTCAG");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);
    }
    
    @Test
    public void oneBase(){        
        byte[] actual =sut.encode(Nucleotide.Cytosine);
        List<Nucleotide> nucleotides = Arrays.asList(Nucleotide.Cytosine);
        assertEquals(nucleotides ,sut.decode(actual));
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
    
    
    //////////////////////
    @Test
    public void encodeWithOneN(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTNACGT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    @Test
    public void encodeWithTwoNs(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTNACNGT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    /**
     * Ns in each of the 4 offsets in a byte
     */
    @Test
    public void encodeWithFourNs(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTNCGTANGTACNTACGNACGT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void encodeWithTwoConsecutiveNs(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGTNNACNGT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
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
        
        byte[] actual =sut.encode(longBases);
        assertEquals(longBases,sut.decode(actual));
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
        
        byte[] actual =sut.encode(longBases);
        assertEquals(longBases,sut.decode(actual));
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
    
}
