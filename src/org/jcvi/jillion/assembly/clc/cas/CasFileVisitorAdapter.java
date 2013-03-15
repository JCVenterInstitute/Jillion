package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;
/**
 * {@code CasFileVisitorAdapter} is a {@link CasFileVisitor}
 * that wraps another {@link CasFileVisitor} and delegates
 * all visitXXX methods to the wrapped instance.
 * 
 * Subclasses may override any visit methods to modify
 * the visit messages before the wrapped instance receives them.
 * 
 * @author dkatzel
 *
 */
public class CasFileVisitorAdapter implements CasFileVisitor{

	private final CasFileVisitor delegate;
	
	/**
	 * Create a new instance of CasFileVisitorAdapter
	 * which will wrap the given {@link CasFileVisitor}.
	 * @param delegate the {@link CasFileVisitor} to wrap;
	 * may not be null.
	 * @throws NullPointerException if delegate is null.
	 */
	public CasFileVisitorAdapter(CasFileVisitor delegate) {
		if(delegate ==null){
			throw new NullPointerException("delegate can not be null");
		}
		this.delegate = delegate;
	}
	/**
	 * Get the {@link CasFileVisitor} instance
	 * that will is being wrapped.
	 * @return the delegate that was provided in the constructor;
	 * will never be null.
	 */
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
