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


import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestNucleotideSequenceFastaRecordFactory {

    private final String id = "1234";
    private final String comment = "comment";
    private final String bases = "ACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGT-N";
    private final  NucleotideSequence encodedGlyphs =new NucleotideSequenceBuilder(bases).build();

    private final NucleotideSequenceFastaRecord sut;
    
    public TestNucleotideSequenceFastaRecordFactory(){

        sut = NucleotideSequenceFastaRecordFactory.create(id,  encodedGlyphs,comment);
    }
    
    @Test
    public void withComment(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        assertEquals(encodedGlyphs, sut.getSequence());
    }
    
    @Test
    public void withoutComment(){
        NucleotideSequenceFastaRecord fasta = NucleotideSequenceFastaRecordFactory.create(id, encodedGlyphs);
        
        assertEquals(id, fasta.getId());
        assertNull(fasta.getComment());
        assertEquals(encodedGlyphs, fasta.getSequence());
    }
   
    @Test
    public void nullIdThrowsNullPointerException(){
        try{
        	NucleotideSequenceFastaRecordFactory.create(null, encodedGlyphs);
            fail("null id should throw IllegalArgumentException");
        }catch(NullPointerException e){
            assertEquals("identifier can not be null", e.getMessage());
        }
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        NucleotideSequenceFastaRecord sameValues = NucleotideSequenceFastaRecordFactory.create(id, 
                encodedGlyphs,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void equalsDifferentComment(){
        NucleotideSequenceFastaRecord sameValues =NucleotideSequenceFastaRecordFactory.create(id, 
                encodedGlyphs,"diff"+comment);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void equalsNoComment(){
        NucleotideSequenceFastaRecord sameValues =NucleotideSequenceFastaRecordFactory.create(id, 
                encodedGlyphs);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void notEqualsDifferentBases(){
        NucleotideSequenceFastaRecord differentBasesAndChecksum = NucleotideSequenceFastaRecordFactory.create(id, 
                new NucleotideSequenceBuilder(bases.substring(2)).build(),comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentBasesAndChecksum);        
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsNotANucleotideFasta(){
        assertFalse(sut.equals(createMock(QualitySequenceFastaRecord.class)));
    }
    

}
