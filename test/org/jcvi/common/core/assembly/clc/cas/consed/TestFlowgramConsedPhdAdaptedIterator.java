package org.jcvi.common.core.assembly.clc.cas.consed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.DateUtil;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.junit.Test;
import static org.easymock.EasyMock.*;

public class TestFlowgramConsedPhdAdaptedIterator extends AbstractTestPhdAdaptedIterator{
	private final File sffFile = new File("example.sff");
	private final Date phdDate = DateUtil.getCurrentDate();
	
	private FlowgramConsedPhdAdaptedIterator createSUT(StreamingIterator<Flowgram> iter){
		return new FlowgramConsedPhdAdaptedIterator(iter, sffFile, phdDate);
	}
	@Test
	public void noReadsShouldMakeEmptyIterator(){
		StreamingIterator<Flowgram> iter = IteratorUtil.createEmptyStreamingIterator();
		FlowgramConsedPhdAdaptedIterator sut = createSUT(iter);
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);
	}

	private PhdReadRecord createExpectedPhdReadRecord(Flowgram flowgram){
		return createExpectedPhdReadRecord(sffFile, flowgram.getId(), 
				flowgram.getNucleotideSequence(), 
				flowgram.getQualitySequence(), phdDate);
	}
	private Flowgram createFlowgram(String id, String bases, byte[] quals){
		Flowgram mockFlow = createMock(Flowgram.class);
		expect(mockFlow.getId()).andStubReturn(id);
		expect(mockFlow.getNucleotideSequence()).andStubReturn(new NucleotideSequenceBuilder(bases).build());
		expect(mockFlow.getQualitySequence()).andStubReturn(new QualitySequenceBuilder(quals).build());
		replay(mockFlow);
		return mockFlow;
	}
	@Test
	public void oneRead(){
		Flowgram fastq = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});
		PhdReadRecord read1 = createExpectedPhdReadRecord(fastq);
		
		StreamingIterator<Flowgram> iter = IteratorUtil.createStreamingIterator(Arrays.asList(fastq).iterator());
		FlowgramConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	@Test
	public void twoReads(){
		Flowgram flowgram = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});
		Flowgram flowgram2 = createFlowgram("read2", "AAAA", new byte[]{12,15,16,17});
		PhdReadRecord read1 = createExpectedPhdReadRecord(flowgram);
		PhdReadRecord read2 = createExpectedPhdReadRecord(flowgram2);
		StreamingIterator<Flowgram> iter = IteratorUtil.createStreamingIterator(
										Arrays.asList(flowgram, 
												flowgram2)
												.iterator());
		FlowgramConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertTrue(sut.hasNext());
		assertEquals(read2, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	
	@Test
	public void close() throws IOException{
		Flowgram flowgram = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});
		Flowgram flowgram2 = createFlowgram("read2", "AAAA", new byte[]{12,15,16,17});
		PhdReadRecord read1 = createExpectedPhdReadRecord(flowgram);
		StreamingIterator<Flowgram> iter = IteratorUtil.createStreamingIterator(
										Arrays.asList(flowgram, 
												flowgram2)
												.iterator());
		FlowgramConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertTrue(sut.hasNext());
		sut.close();
		throwsExceptionWhenNoMoreElements(sut);

	}
	
	@Test
	public void removeShouldThrowException(){
		Flowgram flowgram = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});		
		StreamingIterator<Flowgram> iter = IteratorUtil.createStreamingIterator(Arrays.asList(flowgram).iterator());
		FlowgramConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		try{
			sut.remove();
			fail("should throw unsupportedOperationException");
		}catch(UnsupportedOperationException expected){
			//expected
		}
		
	}
}
