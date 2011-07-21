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
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.pos;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.createMock;

import java.util.List;

import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.pos.DefaultPositionFastaRecord;
import org.jcvi.common.core.symbol.DefaultShortGlyphCodec;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.GlyphCodec;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortGlyph;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;

public class TestPositionFastaRecord {

    private String id = "identifier";
    private String comment = "comment";
    
    private short[] positions = new short[]{1, 10,21,31,42,50,62,84,90,101,110,121,130,140,152};
    private static final GlyphCodec<ShortGlyph> CODEC = DefaultShortGlyphCodec.getInstance();
    
    Sequence<ShortGlyph> encodedPositions = 
        new EncodedSequence<ShortGlyph>(CODEC,
                ShortGlyphFactory.getInstance().getGlyphsFor(positions));
    
    DefaultPositionFastaRecord<Sequence<ShortGlyph>> sut = new DefaultPositionFastaRecord<Sequence<ShortGlyph>>(id,comment, encodedPositions);
    
    private String buildExpectedRecord(DefaultPositionFastaRecord<Sequence<ShortGlyph>> fasta){
        StringBuilder builder= new StringBuilder();
        builder.append(">")
                    .append(fasta.getId());
        if(fasta.getComment() !=null){
            builder.append(" ")
                    .append(fasta.getComment());
        }
        appendCarriageReturn(builder);
        List<ShortGlyph> pos = fasta.getValue().decode();
        for(int i=1; i<pos.size(); i++){
            
            builder.append(String.format("%04d", pos.get(i-1).getNumber()));
            if(i%12==0){
                appendCarriageReturn(builder);
            }
            else{
                builder.append(" ");
            }
        }
        builder.append(String.format("%04d", pos.get(pos.size() -1).getNumber()));
        appendCarriageReturn(builder);
        return builder.toString();
    }
    
    private void appendCarriageReturn(StringBuilder builder) {
        builder.append('\n');
    }
    
    @Test
    public void constructor(){
        assertEquals(comment, sut.getComment());
        assertConstructedFieldsCorrect(sut);
    }

    private void assertConstructedFieldsCorrect(DefaultPositionFastaRecord<Sequence<ShortGlyph>> fasta) {
        assertEquals(id, fasta.getId());        
        assertEquals(0L, fasta.getChecksum());
        final String expectedRecord = buildExpectedRecord(fasta);
        assertEquals(expectedRecord, fasta.toFormattedString().toString());
        assertEquals(encodedPositions, fasta.getValue());
    }
    @Test
    public void constructorWithoutComment(){
        DefaultPositionFastaRecord<Sequence<ShortGlyph>> noComment = new DefaultPositionFastaRecord<Sequence<ShortGlyph>>(id,encodedPositions);
        
        assertNull(noComment.getComment());
        assertConstructedFieldsCorrect(noComment);
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameId(){
        DefaultPositionFastaRecord<Sequence<ShortGlyph>> sameIdAndComment = new DefaultPositionFastaRecord<Sequence<ShortGlyph>>(
                id,comment,createMock(Sequence.class));
        TestUtil.assertEqualAndHashcodeSame(sut, sameIdAndComment);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsNotAQualityFasta(){
        assertFalse(sut.equals(createMock(DefaultNucleotideSequenceFastaRecord.class)));
    }
    @Test
    public void equalsDifferentComment(){
        DefaultPositionFastaRecord<Sequence<ShortGlyph>> differentComment = new DefaultPositionFastaRecord<Sequence<ShortGlyph>>(
                id,null,createMock(Sequence.class));
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    @Test
    public void notEqualsDifferentId(){
        DefaultPositionFastaRecord<Sequence<ShortGlyph>> differentId = new DefaultPositionFastaRecord<Sequence<ShortGlyph>>(
                "different"+id,comment,createMock(Sequence.class));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
}
