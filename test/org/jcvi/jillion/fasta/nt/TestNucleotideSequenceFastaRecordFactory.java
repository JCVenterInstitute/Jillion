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
package org.jcvi.jillion.fasta.nt;


import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
import org.junit.Test;
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
	public void length(){
		assertEquals(sut.getSequence().getLength(), sut.getLength());
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
