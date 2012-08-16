package org.jcvi.common.core.assembly.clc.cas.consed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.jcvi.common.core.seq.fastx.fasta.nt.DefaultNucleotideSequenceFastaRecord;
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
	
	private FastaConsedPhdAdaptedIterator createSUT(StreamingIterator<DefaultNucleotideSequenceFastaRecord> iter){
		return new FastaConsedPhdAdaptedIterator(iter, fastaFile, phdDate,PhredQuality.valueOf(defaultQualityValue));
	}

	private PhdReadRecord createExpectedPhdReadRecord(DefaultNucleotideSequenceFastaRecord fastaRecord){
		byte[] quals = new byte[(int)fastaRecord.getSequence().getLength()];
		Arrays.fill(quals, defaultQualityValue);
		return createExpectedPhdReadRecord(fastaFile, fastaRecord.getId(), 
				fastaRecord.getSequence(), 
				new QualitySequenceBuilder(quals).build(), phdDate);
	}
	
	private DefaultNucleotideSequenceFastaRecord createFasta(String id, String basecalls){
		return new DefaultNucleotideSequenceFastaRecord(id, new NucleotideSequenceBuilder(basecalls).build());
	}
	@Test
	public void noReadsShouldMakeEmptyIterator(){
		StreamingIterator<DefaultNucleotideSequenceFastaRecord> iter = IteratorUtil.createEmptyStreamingIterator();
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);
	}
	
	@Test
	public void oneRead(){
		DefaultNucleotideSequenceFastaRecord fasta = createFasta("read1", "ACGT");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta);
		
		StreamingIterator<DefaultNucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(Arrays.asList(fasta).iterator());
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	@Test
	public void twoReads(){
		DefaultNucleotideSequenceFastaRecord fasta1 = createFasta("read1", "ACGT");
		DefaultNucleotideSequenceFastaRecord fasta2 = createFasta("read2", "AAAA");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta1);
		PhdReadRecord read2 = createExpectedPhdReadRecord(fasta2);
		StreamingIterator<DefaultNucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(
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
		DefaultNucleotideSequenceFastaRecord fasta1 = createFasta("read1", "ACGT");
		DefaultNucleotideSequenceFastaRecord fasta2 = createFasta("read2", "AAAA");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta1);
		StreamingIterator<DefaultNucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(
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
		DefaultNucleotideSequenceFastaRecord fastq = createFasta("read1", "ACGT");		
		StreamingIterator<DefaultNucleotideSequenceFastaRecord> iter = StreamingIteratorAdapter.adapt(Arrays.asList(fastq).iterator());
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
