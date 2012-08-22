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
package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordFactory;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestQualitySequenceFastaRecordFactory {

    private String id = "identifier";
    private String comment = "comment";
 
    byte[] bytes = new byte[]{10,20,70,50,60,2,55,1,2,3,4,5,6,7,8,9,10,10,20,30,12,11,2,5};
   
    private QualitySequence qualities = new QualitySequenceBuilder(bytes).build();
    QualitySequenceFastaRecord sut = QualitySequenceFastaRecordFactory.create(id,qualities,comment);
    
    
    @Test(expected = NullPointerException.class)
    public void nullIdShouldThrowNPE(){
    	QualitySequenceFastaRecordFactory.create(null,qualities,comment);
    }
    @Test(expected = NullPointerException.class)
    public void nullSequenceShouldThrowNPE(){
    	QualitySequenceFastaRecordFactory.create(id,(QualitySequence)null,comment);
    }
    @Test
    public void gettersWithComment(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        assertEquals(qualities, sut.getSequence());        
    }
    @Test
    public void gettersNoComment(){
    	QualitySequenceFastaRecord noComment = QualitySequenceFastaRecordFactory.create(id,qualities);
        
        assertEquals(id, noComment.getId());
        assertNull(noComment.getComment());
        assertEquals(qualities, noComment.getSequence());        
    }
    @Test
    public void gettersNullComment(){
    	QualitySequenceFastaRecord noComment = QualitySequenceFastaRecordFactory.create(id,qualities,null);
        
        assertEquals(id, noComment.getId());
        assertNull(noComment.getComment());
        assertEquals(qualities, noComment.getSequence());        
    }
    @Test
    public void getFormattedString(){
    	final String expectedRecord = buildExpectedRecord();
        assertEquals(expectedRecord, sut.toFormattedString());
    }
    private String buildExpectedRecord(){
        StringBuilder builder= new StringBuilder();
        builder.append(">")
                    .append(id)
                    .append(" ")
                    .append(comment);
        appendCarriageReturn(builder);
        builder.append(convertQualitiesToFormattedString());
        appendCarriageReturn(builder);
        return builder.toString();
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
    	NucleotideSequenceFastaRecord seq = NucleotideSequenceFastaRecordFactory.create(id, new NucleotideSequenceBuilder("ACGTACGT").build());
        assertFalse(sut.equals(seq));
    }
    @Test
    public void equalsDifferentComment(){
    	QualitySequenceFastaRecord differentComment = QualitySequenceFastaRecordFactory.create(
                id,qualities,"different"+comment);
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    @Test
    public void equalsNoComment(){
    	QualitySequenceFastaRecord differentComment = QualitySequenceFastaRecordFactory.create(
                id,qualities);
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    
    @Test
    public void notEqualsDifferentId(){
    	QualitySequenceFastaRecord differentId = QualitySequenceFastaRecordFactory.create(
                "different"+id,qualities,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
    @Test
    public void notEqualsDifferentSequence(){
    	QualitySequenceFastaRecord differentId = QualitySequenceFastaRecordFactory.create(
                id,new QualitySequenceBuilder(new byte[]{1,2,3,4,5}).build(),comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
    @Test
    public void parseEntireQualitySequenceBody(){
    	String formattedString = convertQualitiesToFormattedString().toString();
    	QualitySequenceFastaRecord sameRecord = QualitySequenceFastaRecordFactory.create(id,formattedString,comment);
    	TestUtil.assertEqualAndHashcodeSame(sut, sameRecord);
    }
}
