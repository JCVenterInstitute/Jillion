/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.TestUtil;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestQualityFastaRecord {

    private String id = "identifier";
    private String comment = "comment";
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);

    byte[] bytes = new byte[]{10,20,70,50,60,0,55,1,2,3,4,5,6,7,8,9,10,10,20,30,12,11,2,5};
   
    private EncodedGlyphs<PhredQuality> encodedBytes = new DefaultEncodedGlyphs<PhredQuality>(RUN_LENGTH_CODEC,PhredQuality.valueOf(bytes));
    DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>> sut = new DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>(id,comment,encodedBytes);
    
    @Test
    public void constructor(){
        assertEquals(id, sut.getIdentifier());
        assertEquals(comment, sut.getComments());
        assertEquals(0L, sut.getChecksum());
        final String expectedRecord = buildExpectedRecord();
        assertEquals(expectedRecord, sut.getStringRecord().toString());
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
    public void equalsSameId(){
        DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>> sameIdAndComment = new DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>(
                id,comment,createMock(EncodedGlyphs.class));
        TestUtil.assertEqualAndHashcodeSame(sut, sameIdAndComment);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsNotAQualityFasta(){
        assertFalse(sut.equals(createMock(DefaultEncodedNucleotideFastaRecord.class)));
    }
    @Test
    public void equalsDifferentComment(){
        DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>> differentComment = new DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>(
                id,null,createMock(EncodedGlyphs.class));
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    @Test
    public void notEqualsDifferentId(){
        DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>> differentId = new DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>(
                "different"+id,comment,createMock(EncodedGlyphs.class));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
}
