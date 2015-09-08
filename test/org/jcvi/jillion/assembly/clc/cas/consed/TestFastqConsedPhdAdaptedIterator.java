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
package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.DateUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordBuilder;
import org.junit.Test;

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
		return new FastqRecordBuilder("read1",
				new NucleotideSequenceBuilder(bases).build(),
				new QualitySequenceBuilder(quals).build())
				.build();
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
