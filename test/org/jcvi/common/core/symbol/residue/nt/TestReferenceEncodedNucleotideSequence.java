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
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.symbol.GlyphCodec;
import org.jcvi.common.core.symbol.residue.nt.DefaultNucleotideCodec;
import org.jcvi.common.core.symbol.residue.nt.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestReferenceEncodedNucleotideSequence {
    GlyphCodec<Nucleotide> codec = DefaultNucleotideCodec.INSTANCE;
    String referenceAsString = "ACGTACGTACGTACGTACGTACGTACGT";

    
    NucleotideSequence encodedReference = new NucleotideSequenceBuilder(referenceAsString).build();
    @Test
    public void oneGapNoDifferences(){
        int offset=5;
        String sequenceAsString = "CGTACGT-CGT";
        assertDecodedCorrectly(offset, sequenceAsString);
       
    }
    @Test
    public void noGapsOneDifference(){
        int offset=5;
        String sequenceAsString = "CGTACGTWCGT";
        assertDecodedCorrectly(offset, sequenceAsString);
    }
    private void assertDecodedCorrectly(int offset, String sequenceAsString) {
        ReferenceEncodedNucleotideSequence sut = new DefaultReferenceEncodedNucleotideSequence(encodedReference,sequenceAsString, offset);
        assertEquals(sequenceAsString.length(), sut.getLength());
        assertEquals(sequenceAsString, Nucleotides.asString(sut.asList()));
        for(int i=0; i< sequenceAsString.length(); i++){
            assertEquals(Nucleotide.parse(sequenceAsString.charAt(i)),
                    sut.get(i));
        }
        //check for differences
        Map<Integer,Nucleotide> differences = new HashMap<Integer, Nucleotide>();
        for(int i=0; i< sequenceAsString.length(); i++){
        	Nucleotide ref = encodedReference.get(i+offset);
        	Nucleotide read = sut.get(i);
        	if(ref!=read){
        		differences.put(Integer.valueOf(i), read);
        	}
        }
        Map<Integer,Nucleotide> actualDifferences = sut.getDifferenceMap();
        assertEquals(differences, actualDifferences);
        
        //assertIterator
        Iterator<Nucleotide> actualIter = sut.iterator();
        int i=0;
        while(actualIter.hasNext()){
        	assertEquals(Nucleotide.parse(sequenceAsString.charAt(i)), actualIter.next());
        	i++;
        }
        assertEquals(i,sequenceAsString.length());
    }
    @Test
    public void exactlyTheSame(){
        int offset=5;
        String sequenceAsString = "CGTACGTACGT";
        assertDecodedCorrectly(offset, sequenceAsString);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeStartOffsetShouldThrowException(){
        int offset=-5;
        String sequenceAsString = "NYWHTACGT";
        new DefaultReferenceEncodedNucleotideSequence(encodedReference,sequenceAsString, offset);
    }
    @Test(expected = IllegalArgumentException.class)
    public void sequenceGoesBeyondReferenceShouldThrowException(){
        int offset=referenceAsString.length()-4;
        String sequenceAsString = "ACGTNYWH";
        new DefaultReferenceEncodedNucleotideSequence(encodedReference,sequenceAsString, offset);
    }
    @Test
    public void fullSequence(){
        NucleotideSequence encodedConsensus = new NucleotideSequenceBuilder(
                "GACATGGAAGTTTTATATTCATTGTCAAAAACTCTTAAAGATGCTAGGGACAAAATTGTT" +
                "GAAGGTACACTATATTCTAATGTTAGCGATCTCATTCAACAATTCAATCAAATGATAGTA" +
                "ACTATGAATGGAAATGACTTTCAAACTGGAGGAATTGGTAATTTGCCTATCAGAAACTGG" +
                "ACTTTCGATTTTGGTCTATTAGGTACAACACTTTTAAATTTAGATGCTAATTACGTTGAG" +
                "AATGCTAGAACTACAATTGAATATTTTATTGACTTTATTGATAATGTATGTATGGATGAA" +
                "ATGGCAAGAGAGTCTCAAAGAAATGGAGTAGCTCCACAATCTGAAGCGTTAAGGAAGTTA" +
                "GCAGGTATTAAATTCAAGAGAATAAATTTTGATAATTCATCTGAATATATAGAAAATTGG"+
                "AACTTGCAAAATAGGAGGCAGCGTACTGGATTTGTTTTCCATAAACCTAATATATTTCCA"+
                "TACTCAGCTTCATTCACTTTAAATAGATCTCAACCAATGCATGATAATCTGATGGGAACT"+
                "ATGTGGCTTAATGCTGGATCAGAAATTCAGGTAGCCGGATTTGATTATTCATGCGCTATA"+
                "AATGCACCAGCAAACATACAGCAATTTGAACATATTGTCCAGCTTAGGCGTGCGCTAACT"+
                "ACAGCTACTATAACTTTATTACCTGATGCAG-AAAGATTCAG-TTTTCCAAGAGTTATTA" + 
                "ATTCAGCTGATGGCGCGACTACATGGTTCTTTAATCCAGTCATTTTAAGACCAAATAATG" + 
                "TTGAAGTAGAATTTTTGTTGAATGGACAAATTATTAATACATATCAAGCTAGATTTGGCA" + 
                "CTATTATTGCAAGAAATTTTGATACTATTCGGTTGTTATTCCAGTTGATGCGTCCACCAA" + 
                "ATATGACGCCAGCTGTTAATGCACTGTTTCCGCAAGCACAACCTTTTCAACATCATGCAA" + 
                "CAGTTGGACTTACATTACGTATTGAATCTGCAGTTTGTGAATCAGTGCTTGCGGATGCTA" + 
                "ATGAGACTTTATTGGCGAATGTGACCGCAGTACGTCAAGAGTATGCTATACCAGTTGGTC" + 
                "CAGTATTTCCACCAGGCATGAATTGGACTGAATTAATTACTAATTACTCACCATCTAGAG" + 
                "AAGATAATTTACAACGTGTTTTTACAGTAGCTTCTATTAGAAGCATGTTGATTAAGTGAG" + 
                "GACCAGACTAACTATCTGGTATCCAATCTTAGTTGGCATGTAGCTATATCAAGTCATTCA" + 
                "GACTCTTCAAGTAAGGACATGTTTTCATGTTCGCTACGTAGAGTAACTGTCTGAATGATA")
        		.build();
        
        String sequence = 
                "ACAACTGTACTGTGATTATAATTTGGTATTAATGAAGTATGACGCTACATTGCAATTAGA" +
                "CATGTCCGAACTAGCAGATTTGTTACTTAATGAGTGGTTATGTAATCCTATGGACATCAC" +
                "TTTGTATTATTATCAACAAACTGATGAAGCAAATAAATGGATTTCAATGGGATC-ATC-T" +
                "---TGT-ACCATAAAAGTATGTCCATTAAATACGCAGACATTAGGAATTGGGTGTCTAAC" +
                "TACTGATACAAATACTTTCGAAGAAGTTGCAACAGCTGAAAAATTAGTAATTACTGACGT" +
                "TGTAGATGGAGTCAATCATAAATTGAAAGTGACGACAGATACTTGTACAATTAGAAATTG" +
                "TAAGAAATTAGGACCAAGGGAAAACGTAGCAGTTATACAGGTTGGTGGCTCAGATGTACT" +
                "TGATATAACAGCTGATCCAACGACAGCACCACAAACAGAAAGAATGATGCGAGTGAATTG" +
                "GAAGAAATGGTGGCAAGTGTTTTATACAATAGTTGACTATGTGAATCAAATTGTGCAAGC" +
                "GATGTCCAAAAGATCGAGATCATTAAATTCT"
                ;
        
        int offset = 414;
        DefaultReferenceEncodedNucleotideSequence actual = new DefaultReferenceEncodedNucleotideSequence(
                encodedConsensus, sequence, offset);
        List<Integer> expectedGapIndexes = Arrays.asList(174, 178,180,181,182,186);
        assertEquals(expectedGapIndexes,actual.getGapOffsets());
        assertEquals(expectedGapIndexes.size(), actual.getNumberOfGaps());
       
        assertEquals(sequence.length(), actual.getLength());
        assertEquals(sequence,Nucleotides.asString(actual.asList()));
    }
    
    
}
