package org.jcvi.jillion.assembly.clc.cas;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor.CasVisitorCallback;
import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;
import org.junit.Test;
public class TestCasFileVisitorAdapter extends EasyMockSupport{

	private final CasFileVisitorAdapter sut;
	private final CasFileVisitor delegate;
	
	public TestCasFileVisitorAdapter(){
		delegate = createMock(CasFileVisitor.class);
		sut = new CasFileVisitorAdapter(delegate);
	}
	
	@Test
	public void getDelegate(){
		assertSame(delegate, sut.getDelegate());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullDelegateThrowsNPE(){
		new CasFileVisitorAdapter(null);
	}
	
	@Test
	public void delegateHalted(){
		delegate.halted();
		replayAll();
		sut.halted();
		verifyAll();
	}
	
	@Test
	public void delegateEnd(){
		delegate.visitEnd();
		replayAll();
		sut.visitEnd();
		verifyAll();
	}
	
	@Test
	public void delegateAssemblyProgramInfo(){
		String name = "clc_ref_assemble_long";
		String version = "1.2.3.4";
		String params = "-a 1 -b 2 -message blah";
		delegate.visitAssemblyProgramInfo(name, version, params);
		replayAll();
		sut.visitAssemblyProgramInfo(name, version, params);
		verifyAll();
	}
	
	@Test
	public void adaptAssemblyProgramInfo(){
		String name = "clc_ref_assemble_long";
		String version = "1.2.3.4";
		String params = "-a 1 -b 2 -message blah";
		delegate.visitAssemblyProgramInfo("adapted-"+name, version, params);
		replayAll();
		CasFileVisitorAdapter adapter = new CasFileVisitorAdapter(delegate){

			@Override
			public void visitAssemblyProgramInfo(String name, String version,
					String parameters) {
				super.visitAssemblyProgramInfo("adapted-"+name, version, parameters);
			}
			
		};
		adapter.visitAssemblyProgramInfo(name, version, params);
		verifyAll();
	}
	
	
	@Test
	public void delegateMetaData(){
		long numRefs = 5;
		long numReads = 9999;
		delegate.visitMetaData(numRefs, numReads);
		replayAll();
		sut.visitMetaData(numRefs, numReads);
		verifyAll();
	}
	@Test
	public void adaptMetaData(){
		long numRefs = 5;
		long numReads = 9999;
		delegate.visitMetaData(numRefs+100, numReads);
		replayAll();
		
		CasFileVisitorAdapter adapter = new CasFileVisitorAdapter(delegate){

			@Override
			public void visitMetaData(long numberOfReferenceSequences,
					long numberOfReads) {
				super.visitMetaData(numberOfReferenceSequences+100, numberOfReads);
			}
			
		};
		adapter.visitMetaData(numRefs, numReads);
		verifyAll();
	}
	
	@Test
	public void delegateNumberOfReadFiles(){
		long numReads = 9999;
		delegate.visitNumberOfReadFiles(numReads);
		replayAll();
		sut.visitNumberOfReadFiles(numReads);
		verifyAll();
	}
	
	@Test
	public void delegateNumberOfReferenceFiles(){
		long numReads = 9999;
		delegate.visitNumberOfReferenceFiles(numReads);
		replayAll();
		sut.visitNumberOfReferenceFiles(numReads);
		verifyAll();
	}
	
	@Test
	public void delegateContigPair(){
		CasContigPair contigPair = createMock(CasContigPair.class);
		delegate.visitContigPair(contigPair);
		replayAll();
		sut.visitContigPair(contigPair);
		verifyAll();
	}
	@Test
	public void delegateMatches(){
		CasVisitorCallback callback = createMock(CasVisitorCallback.class);
		CasMatchVisitor returnVisitor = createMock(CasMatchVisitor.class);
		expect(delegate.visitMatches(callback)).andReturn(returnVisitor);
		replayAll();
		assertSame(returnVisitor,sut.visitMatches(callback));
		verifyAll();
	}
	
	@Test
	public void delegateReadFileInfo(){
		CasFileInfo info = createMock(CasFileInfo.class);
		delegate.visitReadFileInfo(info);
		replayAll();
		sut.visitReadFileInfo(info);
		verifyAll();
	}
	
	@Test
	public void delegateRefFileInfo(){
		CasFileInfo info = createMock(CasFileInfo.class);
		delegate.visitReferenceFileInfo(info);
		replayAll();
		sut.visitReferenceFileInfo(info);
		verifyAll();
	}
	
	@Test
	public void delegateRefDescription(){
		CasReferenceDescription descr = createMock(CasReferenceDescription.class);
		delegate.visitReferenceDescription(descr);
		replayAll();
		sut.visitReferenceDescription(descr);
		verifyAll();
	}
	
	@Test
	public void delegateScoringScheme(){
		CasScoringScheme scoringScheme = createMock(CasScoringScheme.class);
		delegate.visitScoringScheme(scoringScheme);
		replayAll();
		sut.visitScoringScheme(scoringScheme);
		verifyAll();
	}
}
