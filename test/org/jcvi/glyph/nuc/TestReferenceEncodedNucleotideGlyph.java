/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.Arrays;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestReferenceEncodedNucleotideGlyph {
    GlyphCodec codec = DefaultNucleotideGlyphCodec.getInstance();
    
    NucleotideGlyphFactory factory = NucleotideGlyphFactory.getInstance();
    String referenceAsString = "ACGTACGTACGTACGTACGTACGTACGT";
    
   
    List<NucleotideGlyph> referenceGlyphs =factory.getGlyphsFor(referenceAsString);
    
    EncodedGlyphs encodedReference = new DefaultEncodedGlyphs(codec,referenceGlyphs);
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
        DefaultReferencedEncodedNucleotideGlyph sut = new DefaultReferencedEncodedNucleotideGlyph(encodedReference,sequenceAsString, offset, Range.buildRange(0,sequenceAsString.length()-1));
        List<NucleotideGlyph> decodedGlyphs =sut.decode();
        assertEquals(factory.getGlyphsFor(sequenceAsString), decodedGlyphs);
        assertEquals(sequenceAsString.length(), sut.getLength());
    }
    @Test
    public void exactlyTheSame(){
        int offset=5;
        String sequenceAsString = "CGTACGTACGT";
        assertDecodedCorrectly(offset, sequenceAsString);
    }
    
    @Test
    public void fullSequence(){
        EncodedGlyphs encodedConsensus = new DefaultEncodedGlyphs(codec,
                factory.getGlyphsFor(
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
                "GACTCTTCAAGTAAGGACATGTTTTCATGTTCGCTACGTAGAGTAACTGTCTGAATGATA"));
        
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
        DefaultReferencedEncodedNucleotideGlyph actual = new DefaultReferencedEncodedNucleotideGlyph(
                encodedConsensus, sequence, offset, Range.buildRange(7,571));
        List<Integer> expectedGapIndexes = Arrays.asList(174, 178,180,181,182,186);
        assertEquals(expectedGapIndexes,actual.getGapIndexes());
        assertEquals(factory.getGlyphsFor(sequence),actual.decode());
        assertEquals(sequence.length(), actual.getLength());
    }
    
    
}
