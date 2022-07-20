package org.jcvi.jillion.fasta;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.jcvi.jillion.internal.fasta.MaxNumberOfRecordsFastaVisitor;
public class MaxFastaVisitorTest {

	FastaRecordVisitor mockRecordVisitor = createMock(FastaRecordVisitor.class);
	
	@Test
	public void nullVisitorShouldThrowNPE() {
		assertThrows(NullPointerException.class, ()-> new MaxNumberOfRecordsFastaVisitor(10, null));
	}
	
	@Test
	public void negativeNumberOfMaxVisitorShouldThrowIllegalArgException() {
		assertThrows(IllegalArgumentException.class, ()-> new MaxNumberOfRecordsFastaVisitor(-5, createMock(FastaVisitor.class)));
	}
	
	@Test
	public void ZeroNumberOfMaxVisitorShouldThrowIllegalArgException() {
		assertThrows(IllegalArgumentException.class, ()-> new MaxNumberOfRecordsFastaVisitor(0, createMock(FastaVisitor.class)));
	}
	
	@Test
	public void maxMoreThanNumberOfVisitsShouldVisitAll() {
		FastaVisitor visitor = createMock(FastaVisitor.class);
		
		int numOfRecords=5;
		int max = 10;
		for(int i=0; i< numOfRecords; i++) {
			expectVisit(visitor, Integer.toString(i));
		}
		visitor.visitEnd();
		replay(visitor);
		
		MaxNumberOfRecordsFastaVisitor sut = new MaxNumberOfRecordsFastaVisitor(max, visitor);
		int id=0;
		
		FastaRecordVisitor rv;
		do {
			FastaVisitorCallback callback = createMock(FastaVisitorCallback.class);
			replay(callback);
			rv = sut.visitDefline(callback, Integer.toString(id), null);
			
			verify(callback);
			id++;
		}while(rv !=null && id< numOfRecords);
		sut.visitEnd();
		
		verify(visitor);
		
	}
	
	@Test
	public void whenReachMaxShouldCallHalt() {
		FastaVisitor visitor = createMock(FastaVisitor.class);
		
		
		expectVisit(visitor, "0");
		expectVisit(visitor, "1");
		expectVisit(visitor, "2");
		expectVisit(visitor, "3");
//		expectVisit(visitor, "4");

		expectFinalVisit(visitor, "4");
	
		replay(visitor);
		
		MaxNumberOfRecordsFastaVisitor sut = new MaxNumberOfRecordsFastaVisitor(5, visitor);
		int id=0;
		
		
		FastaRecordVisitor rv;
		do {
			FastaVisitorCallback callback = createMock(FastaVisitorCallback.class);
			replay(callback);

			rv = sut.visitDefline(callback, Integer.toString(id), null);
			
			verify(callback);
			id++;
		}while(rv !=null && id< 4);
		
		
			FastaVisitorCallback callback = createMock(FastaVisitorCallback.class);
			
			callback.haltParsing();
			replay(callback);
			rv = sut.visitDefline(callback, Integer.toString(id), null);
			assertNotNull(rv);
			verify(callback);
			sut.halted();
			
		
		
		verify(visitor);
		
	}
	private void expectFinalVisit(FastaVisitor visitor, String id) {
		expect(visitor.visitDefline(isA(FastaVisitorCallback.class),eq(id), isNull() ))
		.andAnswer(()->{
			FastaVisitorCallback callback = getCurrentArgument(0);
			callback.haltParsing();
			return mockRecordVisitor;
		});
		visitor.halted();
	}

	private void expectVisit(FastaVisitor visitor, String id) {
		expect(visitor.visitDefline(isA(FastaVisitorCallback.class),eq(id), isNull() )).andReturn(mockRecordVisitor);
	}
	
	
}
