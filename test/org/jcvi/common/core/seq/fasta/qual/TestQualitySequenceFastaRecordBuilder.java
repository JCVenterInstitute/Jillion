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
package org.jcvi.common.core.seq.fasta.qual;

import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordBuilder;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaRecord;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaRecordBuilder;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestQualitySequenceFastaRecordBuilder {

    private String id = "identifier";
    private String comment = "comment";
 
    byte[] bytes = new byte[]{10,20,70,50,60,2,55,1,2,3,4,5,6,7,8,9,10,10,20,30,12,11,2,5};
   
    private QualitySequence qualities = new QualitySequenceBuilder(bytes).build();
    QualitySequenceFastaRecord sut = new QualitySequenceFastaRecordBuilder(id,qualities)
    										.comment(comment)
    										.build();
    
    
    @Test(expected = NullPointerException.class)
    public void nullIdShouldThrowNPE(){
    	new QualitySequenceFastaRecordBuilder(null,qualities);
    }
    @Test(expected = NullPointerException.class)
    public void nullSequenceShouldThrowNPE(){
    	new QualitySequenceFastaRecordBuilder(id,(QualitySequence)null);
    }
    @Test
    public void gettersWithComment(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        assertEquals(qualities, sut.getSequence());        
    }
    @Test
    public void gettersNoComment(){
    	QualitySequenceFastaRecord noComment = new QualitySequenceFastaRecordBuilder(id,qualities).build();
        
        assertEquals(id, noComment.getId());
        assertNull(noComment.getComment());
        assertEquals(qualities, noComment.getSequence());        
    }
    @Test
    public void gettersNullComment(){
    	QualitySequenceFastaRecord noComment = new QualitySequenceFastaRecordBuilder(id,qualities)
    											.comment(null)
    											.build();
        
        assertEquals(id, noComment.getId());
        assertNull(noComment.getComment());
        assertEquals(qualities, noComment.getSequence());        
    }
   
   
	private StringBuilder convertQualitiesToFormattedString() {
		StringBuilder builder = new StringBuilder(bytes.length*3);
		for(int i=1; i<bytes.length; i++){
            
            builder.append(String.format("%02d", bytes[i-1]));
            if(i%17==0){
                appendCarriageReturn(builder);
            }
            else{
                builder.append(" ");
            }
        }
        builder.append(String.format("%02d", bytes[bytes.length-1]));
        return builder;
	}
    private void appendCarriageReturn(StringBuilder builder) {
        builder.append('\n');
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
    public void notEqualsNotAQualityFasta(){
        assertFalse(sut.equals("something completely different"));
    }
    @Test
    public void differrentFastaRecordShouldNotBeEqual(){
    	NucleotideSequenceFastaRecord seq = new NucleotideSequenceFastaRecordBuilder(id, "ACGTACGT").build();
        assertFalse(sut.equals(seq));
    }
    @Test
    public void equalsDifferentComment(){
    	QualitySequenceFastaRecord differentComment = new QualitySequenceFastaRecordBuilder(id,qualities)
    													.comment("different"+comment)
    													.build();
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    @Test
    public void equalsNoComment(){
    	QualitySequenceFastaRecord differentComment = new QualitySequenceFastaRecordBuilder(id,qualities)
    													.build();
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    
    @Test
    public void notEqualsDifferentId(){
    	QualitySequenceFastaRecord differentId = new QualitySequenceFastaRecordBuilder("different"+id,qualities)
									    			.comment(comment)
									    			.build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
    @Test
    public void notEqualsDifferentSequence(){
    	QualitySequenceFastaRecord differentId = new QualitySequenceFastaRecordBuilder(id,new QualitySequenceBuilder(new byte[]{1,2,3,4,5}).build())
		    											.comment(comment)
		    											.build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
    @Test
    public void parseEntireQualitySequenceBody(){
    	String formattedString = convertQualitiesToFormattedString().toString();
    	QualitySequenceFastaRecord sameRecord = new QualitySequenceFastaRecordBuilder(id,formattedString)
    												.comment(comment)
    												.build();
    	TestUtil.assertEqualAndHashcodeSame(sut, sameRecord);
    }
}
