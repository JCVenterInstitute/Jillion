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
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import static org.easymock.classextension.EasyMock.*;

import org.jcvi.Range;
import org.jcvi.assembly.annot.Strand;
import org.jcvi.assembly.annot.ref.CodingRegion;
import org.jcvi.assembly.annot.ref.DefaultRefGene;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestDefaultRefGene {

    Range transcriptionRange = createMock(Range.class);
    CodingRegion codingRegion = createMock(CodingRegion.class);
    
    
    String name = "refGeneName";
    String referenceName = "chromosome 1";
    Strand strand = Strand.FORWARD;
    
    ////////////optional fields
    String alternateName = "alternate Name";
    int id = 1234;
    int bin = 2;
    
    
    DefaultRefGene sut = new DefaultRefGene(id, bin,name,alternateName, referenceName,
            strand, transcriptionRange, codingRegion);
    
    @Test
    public void fullConstructor(){
        assertRequiredFields(sut);
        
        assertEquals(alternateName, sut.getAlternateName());
        assertEquals(id, sut.getId());
        assertEquals(bin, sut.getBin());
        assertEquals(referenceName, sut.getReferenceSequenceName());
    }

    private void assertRequiredFields(DefaultRefGene refGene) {
        assertEquals(name, refGene.getName());
        assertEquals(strand, refGene.getStrand());
        assertEquals(transcriptionRange, refGene.getTranscriptionRange());
        assertEquals(codingRegion, refGene.getCodingRegion());
    }
    
    @Test
    public void partialconstructor(){
        DefaultRefGene refGene = new DefaultRefGene(name,referenceName,
                strand, transcriptionRange, codingRegion);
        assertRequiredFields(refGene);
        
        assertNull(refGene.getAlternateName());
        assertEquals(0, refGene.getId());
        assertEquals(0, refGene.getBin());
        assertEquals(referenceName,refGene.getReferenceSequenceName());
    }
    @Test
    public void nullNameShouldThrowIllegalArgumentException(){
        try{
            new DefaultRefGene(null,referenceName,
                    strand, transcriptionRange, codingRegion);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("name can not be null", e.getMessage());
        }
    }
    
    @Test
    public void nullReferenceNameShouldThrowIllegalArgumentException(){
        try{
            new DefaultRefGene(name,null,
                    strand, transcriptionRange, codingRegion);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("reference name can not be null", e.getMessage());
        }
    }
    
    @Test
    public void nullStrandShouldThrowIllegalArgumentException(){
        try{
            new DefaultRefGene(name,referenceName,
                    null, transcriptionRange, codingRegion);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("strand can not be null", e.getMessage());
        }
    }
    
    @Test
    public void nullTranscriptionRegionShouldThrowIllegalArgumentException(){
        try{
            new DefaultRefGene(name,referenceName,
                    strand, null, codingRegion);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("transcriptionRange can not be null", e.getMessage());
        }
    }
    
    @Test
    public void nullCodingRegionShouldThrowIllegalArgumentException(){
        try{
            new DefaultRefGene(name,referenceName,
                    strand, transcriptionRange, null);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("codingRegion can not be null", e.getMessage());
        }
    }
    
    
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a DefaultRefGene"));
    }
    
    @Test
    public void equalsSameValues(){
        DefaultRefGene sameValues = new DefaultRefGene(id, bin,name,alternateName, referenceName,
                strand, transcriptionRange, codingRegion);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void equalsDifferentId(){
        DefaultRefGene differentId = new DefaultRefGene(id+1, bin,name,alternateName, referenceName,
                strand, transcriptionRange, codingRegion);
        TestUtil.assertEqualAndHashcodeSame(sut, differentId);
    }
    @Test
    public void equalsDifferentBin(){
        DefaultRefGene differentBin = new DefaultRefGene(id, bin+1,name,alternateName, referenceName,
                strand, transcriptionRange, codingRegion);
        TestUtil.assertEqualAndHashcodeSame(sut, differentBin);
    }
    @Test
    public void equalsDifferentAlternateName(){
        DefaultRefGene differentAltName = new DefaultRefGene(id, bin,name,null, referenceName,
                strand, transcriptionRange, codingRegion);
        TestUtil.assertEqualAndHashcodeSame(sut, differentAltName);
    }
    
   
    @Test
    public void differentNameShouldNotBeEquals(){
        DefaultRefGene differentName = new DefaultRefGene("different"+name,referenceName,
                strand, transcriptionRange, codingRegion);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentName);
    }
    @Test
    public void differentReferenceSequenceShouldNotBeEquals(){
        DefaultRefGene differentReferenceSequence = new DefaultRefGene(name, "different"+referenceName,
                strand, transcriptionRange, codingRegion);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentReferenceSequence);
    }
    @Test
    public void differentStrandShouldNotBeEquals(){
        DefaultRefGene differentStrand = new DefaultRefGene(name,referenceName,
                strand.oppositeStrand(), transcriptionRange, codingRegion);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentStrand);
    }
    
    @Test
    public void differentTranscriptionRegionShouldNotBeEquals(){
        DefaultRefGene differentTranscription = new DefaultRefGene(name,referenceName,
                strand, createMock(Range.class), codingRegion);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentTranscription);
    }
    
    @Test
    public void differentCodingRegionShouldNotBeEquals(){
        DefaultRefGene differentCoding = new DefaultRefGene(name,referenceName,
                strand, transcriptionRange,createMock(CodingRegion.class));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentCoding);
    }
    
    @Test
    public void testToString(){
        String expected = DefaultRefGene.class.getName() +
        " : name : " + sut.getName()+" : reference Name : "+sut.getReferenceSequenceName()
        + " strand : " + sut.getStrand() + " transcription range : " + sut.getTranscriptionRange() + 
        " coding region : " + sut.getCodingRegion();
        
        assertEquals(expected, sut.toString());
    }
    
}
