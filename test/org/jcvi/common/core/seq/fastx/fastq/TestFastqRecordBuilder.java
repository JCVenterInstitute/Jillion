package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
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
