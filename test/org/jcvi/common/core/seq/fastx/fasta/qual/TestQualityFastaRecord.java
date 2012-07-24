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
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaRecord;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestQualityFastaRecord {

    private String id = "identifier";
    private String comment = "comment";
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);

    byte[] bytes = new byte[]{10,20,70,50,60,2,55,1,2,3,4,5,6,7,8,9,10,10,20,30,12,11,2,5};
   
    private QualitySequence encodedBytes = new EncodedQualitySequence(RUN_LENGTH_CODEC,PhredQuality.valueOf(bytes));
    DefaultQualityFastaRecord sut = new DefaultQualityFastaRecord(id,comment,encodedBytes);
    
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        final String expectedRecord = buildExpectedRecord();
        assertEquals(expectedRecord, sut.toFormattedString());
    }
    public String buildExpectedRecord(){
        StringBuilder builder= new StringBuilder();
        builder.append(">")
                    .append(id)
                    .append(" ")
                    .append(comment);
        appendCarriageReturn(builder);
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
        appendCarriageReturn(builder);
        return builder.toString();
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
    	NucleotideSequenceFastaRecord seq = new NucleotideSequenceFastaRecord(id, "ACGTACGT");
        assertFalse(sut.equals(seq));
    }
    @Test
    public void equalsDifferentComment(){
        DefaultQualityFastaRecord differentComment = new DefaultQualityFastaRecord(
                id,null,encodedBytes);
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    @Test
    public void notEqualsDifferentId(){
        DefaultQualityFastaRecord differentId = new DefaultQualityFastaRecord(
                "different"+id,comment,encodedBytes);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
    @Test
    public void notEqualsDifferentSequence(){
        DefaultQualityFastaRecord differentId = new DefaultQualityFastaRecord(
                "different"+id,comment,new EncodedQualitySequence(RUN_LENGTH_CODEC,PhredQuality.valueOf(new byte[]{1,2,3,4,5})));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
}
