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
package org.jcvi.jillion.trace.fastq;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestFastqRecordBuilder {
	
	@Test(expected = NullPointerException.class)
	public void idIsNullShouldThrowNPE(){
		FastqRecordBuilder.create(null, 
				createMock(NucleotideSequence.class), createMock(QualitySequence.class));
	}
	@Test(expected = NullPointerException.class)
	public void sequenceIsNullShouldThrowNPE(){
		FastqRecordBuilder.create("id", 
				null, createMock(QualitySequence.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void qualitiesAreNullShouldThrowNPE(){
		FastqRecordBuilder.create("id", 
				createMock(NucleotideSequence.class), null);
	}
	
	@Test
	public void length(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGT").build();
		QualitySequence qual = new QualitySequenceBuilder(new byte[]{20,20,20,20}).build();
		FastqRecord sut = FastqRecordBuilder.create("id",seq, qual)
							.build();
		
		assertEquals(4, sut.getLength());
	}
	
	@Test
	public void noComment(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGT").build();
		QualitySequence qual = new QualitySequenceBuilder(new byte[]{20,20,20,20}).build();
		FastqRecord sut = FastqRecordBuilder.create("id",seq, qual)
							.build();
		
		assertEquals("id", sut.getId());
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(qual, sut.getQualitySequence());
		assertNull(sut.getComment());
		assertTrue(sut instanceof UncommentedFastqRecord);
	}
	@Test
	public void withComment(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGT").build();
		QualitySequence qual = new QualitySequenceBuilder(new byte[]{20,20,20,20}).build();
		
		String comment = "This is a multi-word comment.";
		FastqRecord sut = FastqRecordBuilder.create("id",seq, qual)
							.comment(comment)
							.build();
		
		assertEquals("id", sut.getId());
		assertEquals(seq, sut.getNucleotideSequence());
		assertEquals(qual, sut.getQualitySequence());
		assertEquals(comment,sut.getComment());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void differentSeqAndQualLengthShouldThrowException(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGT").build();
		QualitySequence qual = new QualitySequenceBuilder(new byte[]{20,20})
									.build();
		FastqRecordBuilder.create("id",seq, qual);
	}
}
