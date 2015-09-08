/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;


import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestNucleotideSequenceFastaRecordFactory {

    private final String id = "1234";
    private final String comment = "comment";
    private final String bases = "ACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGT-N";
    private final  NucleotideSequence sequence =new NucleotideSequenceBuilder(bases).build();

    private final NucleotideFastaRecord sut;
    
    public TestNucleotideSequenceFastaRecordFactory(){

        sut = new NucleotideFastaRecordBuilder(id,  sequence)
        			.comment(comment)
        			.build();
    }
    
    @Test
    public void withComment(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        assertEquals(sequence, sut.getSequence());
    }
    
    @Test
    public void withoutComment(){
        NucleotideFastaRecord fasta = new NucleotideFastaRecordBuilder(id, sequence).build();
        
        assertEquals(id, fasta.getId());
        assertNull(fasta.getComment());
        assertEquals(sequence, fasta.getSequence());
    }
   
    @Test(expected = NullPointerException.class)
    public void nullIdThrowsNullPointerException(){
     new NucleotideFastaRecordBuilder(null, sequence);        
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        NucleotideFastaRecord sameValues = new NucleotideFastaRecordBuilder(id, sequence)
        											.comment(comment)
        											.build();
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void equalsDifferentComment(){
        NucleotideFastaRecord sameValues =new NucleotideFastaRecordBuilder(id, sequence)
        											.comment("diff"+comment)
        											.build();
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void equalsNoComment(){
        NucleotideFastaRecord sameValues = new NucleotideFastaRecordBuilder(id, sequence).build();
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);        
    }
    @Test
    public void notEqualsDifferentBases(){
        NucleotideFastaRecord differentBasesAndChecksum = new NucleotideFastaRecordBuilder(id, bases.substring(2))
        														.comment(comment)
        														.build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentBasesAndChecksum);        
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsNotANucleotideFasta(){
        assertFalse(sut.equals(createMock(QualityFastaRecord.class)));
    }
    

}
