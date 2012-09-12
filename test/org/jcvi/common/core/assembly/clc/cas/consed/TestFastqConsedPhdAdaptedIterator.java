package org.jcvi.common.core.assembly.clc.cas.consed;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordFactory;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.DateUtil;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestFastqConsedPhdAdaptedIterator extends AbstractTestPhdAdaptedIterator{

	private final File fastqFile = new File("example.fastq");
	private final Date phdDate = DateUtil.getCurrentDate();
	
	private FastqConsedPhdAdaptedIterator createSUT(StreamingIterator<FastqRecord> iter){
		return new FastqConsedPhdAdaptedIterator(iter, fastqFile, phdDate);
	}
	@Test
	public void noReadsShouldMakeEmptyIterator(){
		StreamingIterator<FastqRecord> iter = IteratorUtil.createEmptyStreamingIterator();
		FastqConsedPhdAdaptedIterator sut = createSUT(iter);
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);
	}

	private PhdReadRecord createExpectedPhdReadRecord(FastqRecord fastqRecord){
		return createExpectedPhdReadRecord(fastqFile, fastqRecord.getId(), 
				fastqRecord.getNucleotideSequence(), 
				fastqRecord.getQualitySequence(), phdDate);
	}
	private FastqRecord createFastq(String id, String bases, byte[] quals){
		return FastqRecordFactory.create("read1",
				new NucleotideSequenceBuilder(bases).build(),
				new QualitySequenceBuilder(quals).build());
	}
	@Test
	public void oneRead(){
		FastqRecord fastq = createFastq("read1", "ACGT", new byte[]{20,30,40,50});
		PhdReadRecord read1 = createExpectedPhdReadRecord(fastq);
		
		StreamingIterator<FastqRecord> iter = IteratorUtil.createStreamingIterator(Arrays.asList(fastq).iterator());
		FastqConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	@Test
	public void twoReads(){
		FastqRecord fastq1 = createFastq("read1", "ACGT", new byte[]{20,30,40,50});
		FastqRecord fastq2 = createFastq("read2", "AAAA", new byte[]{12,15,16,17});
		PhdReadRecord read1 = createExpectedPhdReadRecord(fastq1);
		PhdReadRecord read2 = createExpectedPhdReadRecord(fastq2);
		StreamingIterator<FastqRecord> iter = IteratorUtil.createStreamingIterator(
										Arrays.asList(fastq1, 
												fastq2)
												.iterator());
		FastqConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertTrue(sut.hasNext());
		assertEquals(read2, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	
	@Test
	public void close() throws IOException{
		FastqRecord fastq1 = createFastq("read1", "ACGT", new byte[]{20,30,40,50});
		FastqRecord fastq2 = createFastq("read2", "AAAA", new byte[]{12,15,16,17});
		PhdReadRecord read1 = createExpectedPhdReadRecord(fastq1);
		StreamingIterator<FastqRecord> iter = IteratorUtil.createStreamingIterator(
										Arrays.asList(fastq1, 
												fastq2)
												.iterator());
		FastqConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertTrue(sut.hasNext());
		sut.close();
		throwsExceptionWhenNoMoreElements(sut);

	}
	
	@Test
	public void removeShouldThrowException(){
		FastqRecord fastq = createFastq("read1", "ACGT", new byte[]{20,30,40,50});		
		StreamingIterator<FastqRecord> iter = IteratorUtil.createStreamingIterator(Arrays.asList(fastq).iterator());
		FastqConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		try{
			sut.remove();
			fail("should throw unsupportedOperationException");
		}catch(UnsupportedOperationException expected){
			//expected
		}
		
	}
}
