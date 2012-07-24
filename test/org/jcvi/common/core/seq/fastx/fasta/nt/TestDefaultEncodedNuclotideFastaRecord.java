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
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaRecord;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultEncodedNuclotideFastaRecord {

    private String id = "1234";
    private String comment = "comment";
    String bases = "ACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGT-N";
    List<Nucleotide> glyphs = Nucleotides.parse(bases);
    NucleotideSequence encodedGlyphs = new NucleotideSequenceBuilder(glyphs).build();

    NucleotideSequenceFastaRecord sut = new NucleotideSequenceFastaRecord(id, comment, bases);
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        assertEquals(encodedGlyphs, sut.getSequence());
        assertEquals(buildExpectedToString(comment), sut.toString());
    }
    
    @Test
    public void constructorWithoutComment(){
        NucleotideSequenceFastaRecord fasta = new NucleotideSequenceFastaRecord(id, bases);
        
        assertEquals(id, fasta.getId());
        assertNull(fasta.getComment());
        assertEquals(encodedGlyphs, fasta.getSequence());
        assertEquals(buildExpectedToString(null), fasta.toString());
    }
   
    @Test
    public void nullIdThrowsIllegalArgumentException(){
        try{
            new NucleotideSequenceFastaRecord(null, bases);
            fail("null id should throw IllegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("identifier can not be null", e.getMessage());
        }
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        NucleotideSequenceFastaRecord sameValues = new NucleotideSequenceFastaRecord(id, 
                comment, bases);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void equalsDifferentComment(){
        NucleotideSequenceFastaRecord sameValues = new NucleotideSequenceFastaRecord(id, 
                null, bases);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void notEqualsDifferentBases(){
        NucleotideSequenceFastaRecord differentBasesAndChecksum = new NucleotideSequenceFastaRecord(id, 
                comment, bases.substring(2));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentBasesAndChecksum);        
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsNotANucleotideFasta(){
        assertFalse(sut.equals(createMock(DefaultQualityFastaRecord.class)));
    }
    
    
    private String buildExpectedToString(String comment){
        StringBuilder builder = new StringBuilder();
        builder.append(">")
            .append(id);
        if(comment !=null){
            builder.append(' ').append(comment);
        }
        builder.append(FastaUtil.LINE_SEPARATOR);
        builder.append(formatBasecalls());
        builder.append(FastaUtil.LINE_SEPARATOR);
        return builder.toString();
    }

    private String formatBasecalls() {
        return bases.replaceAll("(.{60})", "$1"+FastaUtil.LINE_SEPARATOR);
    }
    
    @Test
    public void whenFastaSequenceEndsAtEndOfLineShouldNotMakeAdditionalBlankLine(){
        char[] bases = new char[60];
        Arrays.fill(bases, 'A');
        String sixtyBases= new String(bases);
        NucleotideSequenceFastaRecord record = new NucleotideSequenceFastaRecord(id, 
                null, sixtyBases);
        String expectedStringRecord = ">"+id+"\n"+sixtyBases+"\n";
        assertEquals(expectedStringRecord, record.toString());
        
    }
}
