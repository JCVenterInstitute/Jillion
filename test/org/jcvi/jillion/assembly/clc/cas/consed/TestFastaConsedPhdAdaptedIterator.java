package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.DateUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecordBuilder;
import org.junit.Test;

public class TestFastaConsedPhdAdaptedIterator extends AbstractTestPhdAdaptedIterator{

	private final File fastaFile = new File("example.fasta");
	private final Date phdDate = DateUtil.getCurrentDate();
	private final byte defaultQualityValue = 20;
	
	private FastaConsedPhdAdaptedIterator createSUT(StreamingIterator<NucleotideFastaRecord> iter){
		return new FastaConsedPhdAdaptedIterator(iter, fastaFile, phdDate,PhredQuality.valueOf(defaultQualityValue));
	}

	private PhdReadRecord createExpectedPhdReadRecord(NucleotideFastaRecord fastaRecord){
		byte[] quals = new byte[(int)fastaRecord.getSequence().getLength()];
		Arrays.fill(quals, defaultQualityValue);
		return createExpectedPhdReadRecord(fastaFile, fastaRecord.getId(), 
				fastaRecord.getSequence(), 
				new QualitySequenceBuilder(quals).build(), phdDate);
	}
	
	private NucleotideFastaRecord createFasta(String id, String basecalls){
		return new NucleotideSequenceFastaRecordBuilder(id, basecalls)
					.build();
	}
	@Test
	public void noReadsShouldMakeEmptyIterator(){
		StreamingIterator<NucleotideFastaRecord> iter = IteratorUtil.createEmptyStreamingIterator();
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);
	}
	
	@Test
	public void oneRead(){
		NucleotideFastaRecord fasta = createFasta("read1", "ACGT");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta);
		
		StreamingIterator<NucleotideFastaRecord> iter = IteratorUtil.createStreamingIterator(Arrays.asList(fasta).iterator());
		FastaConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	@Test
	public void twoReads(){
		NucleotideFastaRecord fasta1 = createFasta("read1", "ACGT");
		NucleotideFastaRecord fasta2 = createFasta("read2", "AAAA");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta1);
		PhdReadRecord read2 = createExpectedPhdReadRecord(fasta2);
		StreamingIterator<NucleotideFastaRecord> iter = IteratorUtil.createStreamingIterator(
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
		NucleotideFastaRecord fasta1 = createFasta("read1", "ACGT");
		NucleotideFastaRecord fasta2 = createFasta("read2", "AAAA");
		PhdReadRecord read1 = createExpectedPhdReadRecord(fasta1);
		StreamingIterator<NucleotideFastaRecord> iter = IteratorUtil.createStreamingIterator(
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
		NucleotideFastaRecord fastq = createFasta("read1", "ACGT");		
		StreamingIterator<NucleotideFastaRecord> iter = IteratorUtil.createStreamingIterator(Arrays.asList(fastq).iterator());
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
