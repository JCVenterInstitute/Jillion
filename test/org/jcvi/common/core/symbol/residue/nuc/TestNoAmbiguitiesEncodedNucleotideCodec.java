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

package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestNoAmbiguitiesEncodedNucleotideCodec {

    NoAmbiguitiesEncodedNucleotideCodec sut = NoAmbiguitiesEncodedNucleotideCodec.INSTANCE;
    
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
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    private void assertDecodeByIndexIsCorrect(List<Nucleotide> expected, byte[] actual){
       for(int i=0; i<expected.size(); i++){
           Nucleotide actualBase = sut.decode(actual, i);
           assertEquals("" +i,expected.get(i),actualBase);
       }        
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
    
    @Test
    public void encodeWithOneGap(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGT-ACGT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    @Test
    public void encodeWithTwoGaps(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGT-AC-GT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
    
    @Test
    public void encodeWithTwoConsecutiveGaps(){
        List<Nucleotide> nucleotides = Nucleotides.parse("ACGT--AC-GT");
        byte[] actual =sut.encode(nucleotides);
        assertEquals(nucleotides,sut.decode(actual));
        assertDecodeByIndexIsCorrect(nucleotides, actual);        
    }
}
