/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.qual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordBuilder;
import org.junit.Test;
public class TestQualitySequenceFastaRecordBuilder {

    private String id = "identifier";
    private String comment = "comment";
 
    byte[] bytes = new byte[]{10,20,70,50,60,2,55,1,2,3,4,5,6,7,8,9,10,10,20,30,12,11,2,5};
   
    private QualitySequence qualities = new QualitySequenceBuilder(bytes).build();
    QualityFastaRecord sut = new QualityFastaRecordBuilder(id,qualities)
    										.comment(comment)
    										.build();
    
    
    @Test(expected = NullPointerException.class)
    public void nullIdShouldThrowNPE(){
    	new QualityFastaRecordBuilder(null,qualities);
    }
    @Test(expected = NullPointerException.class)
    public void nullSequenceShouldThrowNPE(){
    	new QualityFastaRecordBuilder(id,(QualitySequence)null);
    }
    @Test
    public void gettersWithComment(){
        assertEquals(id, sut.getId());
        assertEquals(comment, sut.getComment());
        assertEquals(qualities, sut.getSequence());        
    }
    @Test
    public void gettersNoComment(){
    	QualityFastaRecord noComment = new QualityFastaRecordBuilder(id,qualities).build();
        
        assertEquals(id, noComment.getId());
        assertNull(noComment.getComment());
        assertEquals(qualities, noComment.getSequence());        
    }
    
    @Test
	public void length(){
		assertEquals(sut.getSequence().getLength(), sut.getLength());
	}
    @Test
    public void gettersNullComment(){
    	QualityFastaRecord noComment = new QualityFastaRecordBuilder(id,qualities)
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
    	NucleotideFastaRecord seq = new NucleotideFastaRecordBuilder(id, "ACGTACGT").build();
        assertFalse(sut.equals(seq));
    }
    @Test
    public void equalsDifferentComment(){
    	QualityFastaRecord differentComment = new QualityFastaRecordBuilder(id,qualities)
    													.comment("different"+comment)
    													.build();
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    @Test
    public void equalsNoComment(){
    	QualityFastaRecord differentComment = new QualityFastaRecordBuilder(id,qualities)
    													.build();
        TestUtil.assertEqualAndHashcodeSame(sut, differentComment);
    }
    
    @Test
    public void notEqualsDifferentId(){
    	QualityFastaRecord differentId = new QualityFastaRecordBuilder("different"+id,qualities)
									    			.comment(comment)
									    			.build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
    @Test
    public void notEqualsDifferentSequence(){
    	QualityFastaRecord differentId = new QualityFastaRecordBuilder(id,new QualitySequenceBuilder(new byte[]{1,2,3,4,5}).build())
		    											.comment(comment)
		    											.build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    
    @Test
    public void parseEntireQualitySequenceBody(){
    	String formattedString = convertQualitiesToFormattedString().toString();
    	QualityFastaRecord sameRecord = new QualityFastaRecordBuilder(id,formattedString)
    												.comment(comment)
    												.build();
    	TestUtil.assertEqualAndHashcodeSame(sut, sameRecord);
    }
}
