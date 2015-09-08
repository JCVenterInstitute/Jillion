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
		new FastqRecordBuilder(null, 
				createMock(NucleotideSequence.class), createMock(QualitySequence.class));
	}
	@Test(expected = NullPointerException.class)
	public void sequenceIsNullShouldThrowNPE(){
		new FastqRecordBuilder("id", 
				null, createMock(QualitySequence.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void qualitiesAreNullShouldThrowNPE(){
		new FastqRecordBuilder("id", 
				createMock(NucleotideSequence.class), null);
	}
	
	@Test
	public void noComment(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGT").build();
		QualitySequence qual = new QualitySequenceBuilder(new byte[]{20,20,20,20}).build();
		FastqRecord sut = new FastqRecordBuilder("id",seq, qual)
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
		FastqRecord sut = new FastqRecordBuilder("id",seq, qual)
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
		new FastqRecordBuilder("id",seq, qual);
	}
}
