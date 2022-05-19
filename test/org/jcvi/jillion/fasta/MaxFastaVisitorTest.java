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
		
		int numOfRecords=10;
		int max = 5;
		int numToVisitBeforeHalt = max > numOfRecords? numOfRecords: max;
		for(int i=0; i< numToVisitBeforeHalt; i++) {
			expectVisit(visitor, Integer.toString(i));
		}
		if(max < numOfRecords) {
			expectFinalVisit(visitor, Integer.toString(max));
		}else {
			visitor.visitEnd();
		}
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
		}while(rv !=null && id< numToVisitBeforeHalt);
		
		if(max < numOfRecords) {
			FastaVisitorCallback callback = createMock(FastaVisitorCallback.class);
			
			
			callback.haltParsing();
			replay(callback);
			assertNull(sut.visitDefline(callback, Integer.toString(max), null));
			verify(callback);
			sut.halted();
			
		}else {
		
			sut.visitEnd();
			
		}
		
		verify(visitor);
		
	}
	
	private void expectVisit(FastaVisitor visitor, String id) {
		expect(visitor.visitDefline(isA(FastaVisitorCallback.class),eq(id), isNull() )).andReturn(mockRecordVisitor);
	}
	
	private void expectFinalVisit(FastaVisitor visitor, String id) {
		expect(visitor.visitDefline(isA(FastaVisitorCallback.class),eq(id), isNull() ))
		.andAnswer(()->{
			FastaVisitorCallback callback = getCurrentArgument(0);
			callback.haltParsing();
			return null;
		});
		visitor.halted();
	}
}
