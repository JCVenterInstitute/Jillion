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
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.symbol.GlyphCodec;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideGlyphCodec;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestReferenceEncodedNucleotideSequence {
    GlyphCodec codec = DefaultNucleotideGlyphCodec.getInstance();
    String referenceAsString = "ACGTACGTACGTACGTACGTACGTACGT";

    
    NucleotideSequence encodedReference = new DefaultNucleotideSequence(referenceAsString);
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
        DefaultReferenceEncodedNucleotideSequence sut = new DefaultReferenceEncodedNucleotideSequence(encodedReference,sequenceAsString, offset);
        assertEquals(sequenceAsString.length(), sut.getLength());
        assertEquals(sequenceAsString, Nucleotides.convertToString(sut.decode()));
        for(int i=0; i< sequenceAsString.length(); i++){
            assertEquals(Nucleotide.parse(sequenceAsString.charAt(i)),
                    sut.get(i));
        }
    }
    @Test
    public void exactlyTheSame(){
        int offset=5;
        String sequenceAsString = "CGTACGTACGT";
        assertDecodedCorrectly(offset, sequenceAsString);
    }
    @Test
    public void negativeStartOffset(){
        int offset=-5;
        String sequenceAsString = "NYWHTACGT";
        assertDecodedCorrectly(offset, sequenceAsString);
    }
    @Test
    public void sequenceGoesBeyondReference(){
        int offset=referenceAsString.length()-4;
        String sequenceAsString = "ACGTNYWH";
        assertDecodedCorrectly(offset, sequenceAsString);
    }
    @Test
    public void sequenceHasNegativeOffsetAndGoesBeyondReference(){
        int offset=-4;
        String sequenceAsString = "VHDB"+referenceAsString+"NYWH";
        assertDecodedCorrectly(offset, sequenceAsString);
    }
    @Test
    public void fullSequence(){
        NucleotideSequence encodedConsensus = new DefaultNucleotideSequence(
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
                "GACTCTTCAAGTAAGGACATGTTTTCATGTTCGCTACGTAGAGTAACTGTCTGAATGATA");
        
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
        assertEquals(expectedGapIndexes,actual.getGapIndexes());
        assertEquals(expectedGapIndexes.size(), actual.getNumberOfGaps());
       
        assertEquals(sequence.length(), actual.getLength());
        assertEquals(sequence,Nucleotides.convertToString(actual.decode()));
    }
    
    
}
