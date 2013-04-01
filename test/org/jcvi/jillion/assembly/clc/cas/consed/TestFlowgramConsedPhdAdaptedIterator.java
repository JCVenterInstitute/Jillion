package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
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
import org.jcvi.jillion.trace.sff.SffFlowgram;
import org.junit.Test;

public class TestFlowgramConsedPhdAdaptedIterator extends AbstractTestPhdAdaptedIterator{
	private final File sffFile = new File("example.sff");
	private final Date phdDate = DateUtil.getCurrentDate();
	
	private FlowgramConsedPhdAdaptedIterator createSUT(StreamingIterator<SffFlowgram> iter){
		return new FlowgramConsedPhdAdaptedIterator(iter, sffFile, phdDate);
	}
	@Test
	public void noReadsShouldMakeEmptyIterator(){
		StreamingIterator<SffFlowgram> iter = IteratorUtil.createEmptyStreamingIterator();
		FlowgramConsedPhdAdaptedIterator sut = createSUT(iter);
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);
	}

	private PhdReadRecord createExpectedPhdReadRecord(SffFlowgram flowgram){
		return createExpectedPhdReadRecord(sffFile, flowgram.getId(), 
				flowgram.getNucleotideSequence(), 
				flowgram.getQualitySequence(), phdDate);
	}
	private SffFlowgram createFlowgram(String id, String bases, byte[] quals){
		SffFlowgram mockFlow = createMock(SffFlowgram.class);
		expect(mockFlow.getId()).andStubReturn(id);
		expect(mockFlow.getNucleotideSequence()).andStubReturn(new NucleotideSequenceBuilder(bases).build());
		expect(mockFlow.getQualitySequence()).andStubReturn(new QualitySequenceBuilder(quals).build());
		replay(mockFlow);
		return mockFlow;
	}
	@Test
	public void oneRead(){
		SffFlowgram fastq = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});
		PhdReadRecord read1 = createExpectedPhdReadRecord(fastq);
		
		StreamingIterator<SffFlowgram> iter = IteratorUtil.createStreamingIterator(Arrays.asList(fastq).iterator());
		FlowgramConsedPhdAdaptedIterator sut = createSUT(iter);
		assertTrue(sut.hasNext());
		assertEquals(read1, sut.next());
		assertFalse(sut.hasNext());
		throwsExceptionWhenNoMoreElements(sut);

	}
	@Test
	public void twoReads(){
		SffFlowgram flowgram = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});
		SffFlowgram flowgram2 = createFlowgram("read2", "AAAA", new byte[]{12,15,16,17});
		PhdReadRecord read1 = createExpectedPhdReadRecord(flowgram);
		PhdReadRecord read2 = createExpectedPhdReadRecord(flowgram2);
		StreamingIterator<SffFlowgram> iter = IteratorUtil.createStreamingIterator(
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
		SffFlowgram flowgram = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});
		SffFlowgram flowgram2 = createFlowgram("read2", "AAAA", new byte[]{12,15,16,17});
		PhdReadRecord read1 = createExpectedPhdReadRecord(flowgram);
		StreamingIterator<SffFlowgram> iter = IteratorUtil.createStreamingIterator(
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
		SffFlowgram flowgram = createFlowgram("read1", "ACGT", new byte[]{20,30,40,50});		
		StreamingIterator<SffFlowgram> iter = IteratorUtil.createStreamingIterator(Arrays.asList(flowgram).iterator());
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
