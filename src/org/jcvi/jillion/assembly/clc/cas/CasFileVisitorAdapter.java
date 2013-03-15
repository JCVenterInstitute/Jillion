package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;

public class CasFileVisitorAdapter implements CasFileVisitor{

	private final CasFileVisitor delegate;
	
	
	public CasFileVisitorAdapter(CasFileVisitor delegate) {
		if(delegate ==null){
			throw new NullPointerException("delegate can not be null");
		}
		this.delegate = delegate;
	}

	protected final CasFileVisitor getDelegate(){
		return delegate;
	}
	@Override
	public void visitAssemblyProgramInfo(String name, String version,
			String parameters) {
		delegate.visitAssemblyProgramInfo(name, version, parameters);		
	}

	@Override
	public void visitMetaData(long numberOfReferenceSequences,
			long numberOfReads) {
		delegate.visitMetaData(numberOfReferenceSequences, numberOfReads);		
	}

	@Override
	public void visitNumberOfReadFiles(long numberOfReadFiles) {
		delegate.visitNumberOfReadFiles(numberOfReadFiles);		
	}

	@Override
	public void visitNumberOfReferenceFiles(long numberOfReferenceFiles) {
		delegate.visitNumberOfReferenceFiles(numberOfReferenceFiles);
	}

	@Override
	public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
		delegate.visitReferenceFileInfo(referenceFileInfo);		
	}

	@Override
	public void visitReadFileInfo(CasFileInfo readFileInfo) {
		delegate.visitReadFileInfo(readFileInfo);
		
	}

	@Override
	public void visitScoringScheme(CasScoringScheme scheme) {
		delegate.visitScoringScheme(scheme);
	}

	@Override
	public void visitReferenceDescription(CasReferenceDescription description) {
		delegate.visitReferenceDescription(description);		
	}

	@Override
	public void visitContigPair(CasContigPair contigPair) {
		delegate.visitContigPair(contigPair);		
	}

	@Override
	public void visitEnd() {
		delegate.visitEnd();
	}

	@Override
	public void halted() {
		delegate.halted();
	}

	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		return delegate.visitMatches(callback);
	}

}
