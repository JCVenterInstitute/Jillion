package org.jcvi.common.core.assembly.clc.cas.consed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.DateUtil;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.core.util.iter.StreamingIteratorAdapter;
import org.junit.Test;

public class TestFastaConsedPhdAdaptedIterator extends AbstractTestPhdAdaptedIterator{

	private final File fastaFile = new File("example.fasta");
	private final Date phdDate = DateUtil.getCurrentDate();
	private final byte defaultQualityValue = 20;
	
	private FastaConsedPhdAdaptedIterator createSUT(StreamingIterator<NucleotideSequenceFastaRecord> iter){
		return new FastaConsedPhdAdaptedIterator(iter, fastaFile, phdDate,PhredQuality.valueOf(defaultQualityValue));
	}

	private PhdReadRecord createExpectedPhdReadRecord(NucleotideSequenceFastaRecord fastaRecord){
		byte[] quals = new byte[(int)fastaRecord.getSequence().getLength()];
		Arrays.fill(quals, defaultQualityValue);
		return createExpectedPhdReadRecord(fastaFile, fastaRecord.getId(), 
				fastaRecord.getSequence(), 
				new QualitySequenceBuilder(quals).build(), phdDate);
	}
	
	private NucleotideSequenceFastaRecord createFasta(String id, String basecalls){
		return new NucleotideSequenceFastaRecord(id, new NucleotideSequenceBuilder(basecalls).build());
	}
	@Test
	public void noReadsShouldMakeEmptyIterator(){
		StreamingIterator<NucleotideSequenceFastaRecord> iter = IteratorUtil.createEmptyStreamingIterator();
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);
	}
	
	@Test
	public void oneRead(){
		NucleotideSequenceFastaRecord fasta = createFasta("read1", "ACGT");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta);
		
		StreamingIterator<NucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(Arrays.asList(fasta).iterator());
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	@Test
	public void twoReads(){
		NucleotideSequenceFastaRecord fasta1 = createFasta("read1", "ACGT");
		NucleotideSequenceFastaRecord fasta2 = createFasta("read2", "AAAA");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta1);
		PhdReadRecord read2 = createExpectedPhdReadRecord(fasta2);
		StreamingIterator<NucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(
										Arrays.asList(fasta1, 
												fasta2)
												.iterator());
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertTrue(sut.hasNext());
		assertEquals(read2, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	
	@Test
	public void close() throws IOException{
		NucleotideSequenceFastaRecord fasta1 = createFasta("read1", "ACGT");
		NucleotideSequenceFastaRecord fasta2 = createFasta("read2", "AAAA");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta1);
		StreamingIterator<NucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(
										Arrays.asList(fasta1, 
												fasta2)
												.iterator());
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertTrue(sut.hasNext());
		sut.close();
		throwsExceptionWhenNoMoreElements(sut);

	}
	
	@Test
	public void removeShouldThrowException(){
		NucleotideSequenceFastaRecord fastq = createFasta("read1", "ACGT");		
		StreamingIterator<NucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(Arrays.asList(fastq).iterator());
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		try{
			sut.remove();
			fail("should throw unsupportedOperationException");
		}catch(UnsupportedOperationException expected){
			//expected
		}
		
	}
}
