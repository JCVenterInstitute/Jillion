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

package org.jcvi.ncbi;

import java.util.Arrays;
import java.util.List;

import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultNcbiGappedFastaRecord {

    List<NucleotideEncodedGlyphs> expectedSequences = Arrays.<NucleotideEncodedGlyphs>asList(
            new DefaultNucleotideEncodedGlyphs("AAATGCATGGGTAAAAGTAGTAGAAGAGAAGGCTTTTAGCCCAGAAGTAATACCCATGTTTTCAGCATTAGGAAAAAGGGCTGTTG"),
            new DefaultNucleotideEncodedGlyphs("TGGATGACAGAAACCTTGTTGGTCCAAAATGCAAACCCAGATKGTAAGACCATTTTAAAAGCATTGGGTCTTAGAAATAGGGCAACACAGAACAAAAAT"),
            new DefaultNucleotideEncodedGlyphs("AAAAATAAAAGCATTAGTAGAAATTTGTACAGAACTGGAAAAGGAAGGAAAAATTTCAAAAATTGGGCCTGAAAACCCATACAATACTCCGGG")
        );
    String id = "Dobi";
    String comments = "[organism=Canis familiaris] [breed=Doberman pinscher]";
    int expectedGapSize = 234;
    DefaultNcbiGappedFastaRecord sut;
    @Before
    public void setup(){
        sut= createSut();
    }

    protected DefaultNcbiGappedFastaRecord createSut() {
        DefaultNcbiGappedFastaRecord.Builder builder = new DefaultNcbiGappedFastaRecord.Builder(id, comments);
        for(NucleotideEncodedGlyphs sequence : expectedSequences){
            builder.addSequence(sequence);
        }
        builder.addGap();
        builder.addGap(expectedGapSize);
        DefaultNcbiGappedFastaRecord record =builder.build();
        return record;
    }
    
    @Test
    public void builtCorrectly(){
        String expectedFastaRecord = 
            ">Dobi [organism=Canis familiaris] [breed=Doberman pinscher]\n"+
            "AAATGCATGGGTAAAAGTAGTAGAAGAGAAGGCTTTTAGCCCAGAAGTAATACCCATGTT\n"+
            "TTCAGCATTAGGAAAAAGGGCTGTTG\n"+
            ">?unk100\n"+
            "TGGATGACAGAAACCTTGTTGGTCCAAAATGCAAACCCAGATKGTAAGACCATTTTAAAA\n"+
            "GCATTGGGTCTTAGAAATAGGGCAACACAGAACAAAAAT\n"+
            ">?234\n"+
            "AAAAATAAAAGCATTAGTAGAAATTTGTACAGAACTGGAAAAGGAAGGAAAAATTTCAAA\n"+
            "AATTGGGCCTGAAAACCCATACAATACTCCGGG";
        
        assertThat(sut.getIdentifier(), is(equalTo(id)));
        assertThat(sut.getComments(), is(equalTo(comments)));
        assertThat(sut.getStringRecord().toString(), is(equalTo(expectedFastaRecord)));
        StringBuilder concatenatedSequenceBuilder = new StringBuilder("AAATGCATGGGTAAAAGTAGTAGAAGAGAAGGCTTTTAGCCCAGAAGTAATACCCATGTTTTCAGCATTAGGAAAAAGGGCTGTTG");
        for(int i=0; i<100; i++){
            concatenatedSequenceBuilder.append("-");
        }
        concatenatedSequenceBuilder.append("TGGATGACAGAAACCTTGTTGGTCCAAAATGCAAACCCAGATKGTAAGACCATTTTAAAAGCATTGGGTCTTAGAAATAGGGCAACACAGAACAAAAAT");
        for(int i=0; i<234; i++){
            concatenatedSequenceBuilder.append("-");
        }
        concatenatedSequenceBuilder.append("AAAAATAAAAGCATTAGTAGAAATTTGTACAGAACTGGAAAAGGAAGGAAAAATTTCAAAAATTGGGCCTGAAAACCCATACAATACTCCGGG");
        NucleotideEncodedGlyphs concatenatedSequence = new DefaultNucleotideEncodedGlyphs(concatenatedSequenceBuilder.toString());
        
        assertThat(sut.getValues(), is(equalTo(concatenatedSequence)));
    }
    @Test
    public void nullComments(){
        
        DefaultNcbiGappedFastaRecord.Builder builder = new DefaultNcbiGappedFastaRecord.Builder(id);
        for(NucleotideEncodedGlyphs sequence : expectedSequences){
            builder.addSequence(sequence);
        }
        builder.addGap();
        builder.addGap(expectedGapSize);
        DefaultNcbiGappedFastaRecord noComments =builder.build();
        
        String expectedFastaRecord = 
            ">Dobi\n"+
            "AAATGCATGGGTAAAAGTAGTAGAAGAGAAGGCTTTTAGCCCAGAAGTAATACCCATGTT\n"+
            "TTCAGCATTAGGAAAAAGGGCTGTTG\n"+
            ">?unk100\n"+
            "TGGATGACAGAAACCTTGTTGGTCCAAAATGCAAACCCAGATKGTAAGACCATTTTAAAA\n"+
            "GCATTGGGTCTTAGAAATAGGGCAACACAGAACAAAAAT\n"+
            ">?234\n"+
            "AAAAATAAAAGCATTAGTAGAAATTTGTACAGAACTGGAAAAGGAAGGAAAAATTTCAAA\n"+
            "AATTGGGCCTGAAAACCCATACAATACTCCGGG";
        
        assertThat(noComments.getIdentifier(), is(equalTo(id)));
        assertThat(noComments.getComments(), is(nullValue()));
        assertThat(noComments.getStringRecord().toString(), is(equalTo(expectedFastaRecord)));
        StringBuilder concatenatedSequenceBuilder = new StringBuilder("AAATGCATGGGTAAAAGTAGTAGAAGAGAAGGCTTTTAGCCCAGAAGTAATACCCATGTTTTCAGCATTAGGAAAAAGGGCTGTTG");
        for(int i=0; i<100; i++){
            concatenatedSequenceBuilder.append("-");
        }
        concatenatedSequenceBuilder.append("TGGATGACAGAAACCTTGTTGGTCCAAAATGCAAACCCAGATKGTAAGACCATTTTAAAAGCATTGGGTCTTAGAAATAGGGCAACACAGAACAAAAAT");
        for(int i=0; i<234; i++){
            concatenatedSequenceBuilder.append("-");
        }
        concatenatedSequenceBuilder.append("AAAAATAAAAGCATTAGTAGAAATTTGTACAGAACTGGAAAAGGAAGGAAAAATTTCAAAAATTGGGCCTGAAAACCCATACAATACTCCGGG");
        NucleotideEncodedGlyphs concatenatedSequence = new DefaultNucleotideEncodedGlyphs(concatenatedSequenceBuilder.toString());
        
        assertThat(noComments.getValues(), is(equalTo(concatenatedSequence)));
    }
    @Test
    public void sequencesBuiltWithStrings(){
        
        DefaultNcbiGappedFastaRecord.Builder builder = new DefaultNcbiGappedFastaRecord.Builder(id);
        for(NucleotideEncodedGlyphs sequence : expectedSequences){
            builder.addSequence(NucleotideGlyph.convertToString(sequence.decode()));
        }
        builder.addGap();
        builder.addGap(expectedGapSize);
        DefaultNcbiGappedFastaRecord noComments =builder.build();
        
        String expectedFastaRecord = 
            ">Dobi\n"+
            "AAATGCATGGGTAAAAGTAGTAGAAGAGAAGGCTTTTAGCCCAGAAGTAATACCCATGTT\n"+
            "TTCAGCATTAGGAAAAAGGGCTGTTG\n"+
            ">?unk100\n"+
            "TGGATGACAGAAACCTTGTTGGTCCAAAATGCAAACCCAGATKGTAAGACCATTTTAAAA\n"+
            "GCATTGGGTCTTAGAAATAGGGCAACACAGAACAAAAAT\n"+
            ">?234\n"+
            "AAAAATAAAAGCATTAGTAGAAATTTGTACAGAACTGGAAAAGGAAGGAAAAATTTCAAA\n"+
            "AATTGGGCCTGAAAACCCATACAATACTCCGGG";
        
        assertThat(noComments.getIdentifier(), is(equalTo(id)));
        assertThat(noComments.getComments(), is(nullValue()));
        assertThat(noComments.getStringRecord().toString(), is(equalTo(expectedFastaRecord)));
        StringBuilder concatenatedSequenceBuilder = new StringBuilder("AAATGCATGGGTAAAAGTAGTAGAAGAGAAGGCTTTTAGCCCAGAAGTAATACCCATGTTTTCAGCATTAGGAAAAAGGGCTGTTG");
        for(int i=0; i<100; i++){
            concatenatedSequenceBuilder.append("-");
        }
        concatenatedSequenceBuilder.append("TGGATGACAGAAACCTTGTTGGTCCAAAATGCAAACCCAGATKGTAAGACCATTTTAAAAGCATTGGGTCTTAGAAATAGGGCAACACAGAACAAAAAT");
        for(int i=0; i<234; i++){
            concatenatedSequenceBuilder.append("-");
        }
        concatenatedSequenceBuilder.append("AAAAATAAAAGCATTAGTAGAAATTTGTACAGAACTGGAAAAGGAAGGAAAAATTTCAAAAATTGGGCCTGAAAACCCATACAATACTCCGGG");
        NucleotideEncodedGlyphs concatenatedSequence = new DefaultNucleotideEncodedGlyphs(concatenatedSequenceBuilder.toString());
        
        assertThat(noComments.getValues(), is(equalTo(concatenatedSequence)));
    }
    @Test(expected = IllegalStateException.class)
    public void moreGapsThanSequencesShouldThrowIllegalStateException(){
        new DefaultNcbiGappedFastaRecord.Builder(id)
        .addSequence("ACGT")
        .addGap()
        .addGap(34)
        .build();
    }
    @Test(expected = NullPointerException.class)
    public void nullIdShouldThrowNullPointerException(){
        new DefaultNcbiGappedFastaRecord.Builder(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullNucleotideGlyphSequenceShouldThrowNullPointerException(){
        new DefaultNcbiGappedFastaRecord.Builder(id)
            .addSequence((NucleotideEncodedGlyphs)null);
    }
    @Test(expected = NullPointerException.class)
    public void nullStringSequenceShouldThrowNullPointerException(){
        new DefaultNcbiGappedFastaRecord.Builder(id)
            .addSequence((String)null);
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        TestUtil.assertEqualAndHashcodeSame(sut, createSut());
    }
    @Test
    public void notEqualtoNull(){
        assertThat(sut, is(not(equalTo(null))));
    }
    
    @Test
    public void notEqualtoOtherObject(){
        assertThat((Object)sut, is(not(equalTo((Object)"not a Gapped Fasta Record"))));
    }
    @Test
    public void differentIdsShouldNotBeEqual(){
        DefaultNcbiGappedFastaRecord rec1 = new DefaultNcbiGappedFastaRecord.Builder(id)
                                                .addSequence("ACGT")
                                                .build();
        DefaultNcbiGappedFastaRecord rec2 = new DefaultNcbiGappedFastaRecord.Builder("not"+id)
                                                .addSequence("ACGT")
                                                .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(rec1, rec2);
    }
    @Test
    public void differentSequencesShouldNotBeEqual(){
        DefaultNcbiGappedFastaRecord rec1 = new DefaultNcbiGappedFastaRecord.Builder(id)
                                                .addSequence("ACGT")
                                                .build();
        DefaultNcbiGappedFastaRecord rec2 = new DefaultNcbiGappedFastaRecord.Builder(id)
                                                .addSequence("NNNN")
                                                .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(rec1, rec2);
    }
    @Test
    public void differentGapTypesShouldNotBeEqual(){
        DefaultNcbiGappedFastaRecord rec1 = new DefaultNcbiGappedFastaRecord.Builder(id)
                                                .addSequence("ACGT")
                                                .addGap(10)
                                                .addSequence("NNNN")
                                                .build();
        DefaultNcbiGappedFastaRecord rec2 = new DefaultNcbiGappedFastaRecord.Builder(id)
                                                .addSequence("ACGT")                                                
                                                .addGap()
                                                .addSequence("NNNN")
                                                .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(rec1, rec2);
    }
    
    @Test
    public void differentGapLengthsShouldNotBeEqual(){
        DefaultNcbiGappedFastaRecord rec1 = new DefaultNcbiGappedFastaRecord.Builder(id)
                                                .addSequence("ACGT")
                                                .addGap(10)
                                                .addSequence("NNNN")
                                                .build();
        DefaultNcbiGappedFastaRecord rec2 = new DefaultNcbiGappedFastaRecord.Builder(id)
                                                .addSequence("ACGT")                                                
                                                .addGap(5)
                                                .addSequence("NNNN")
                                                .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(rec1, rec2);
    }
}
