package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;
/**
 * {@code AbstractCasFileVisitor} is a 
 * {@link CasFileVisitor} implementation that
 * implements each method with a default empty stub.
 * This allows subclasses to only override the methods
 * they care about without cluttering up the subclass with 
 * many empty methods.
 * @author dkatzel
 *
 */
public abstract class AbstractCasFileVisitor implements CasFileVisitor{

	@Override
	public void visitAssemblyProgramInfo(String name, String version,
			String parameters) {
		//no-op		
	}

	@Override
	public void visitMetaData(long numberOfReferenceSequences,
			long numberOfReads) {
		//no-op	
	}

	@Override
	public void visitNumberOfReadFiles(long numberOfReadFiles) {
		//no-op	
	}

	@Override
	public void visitNumberOfReferenceFiles(long numberOfReferenceFiles) {
		//no-op	
	}

	@Override
	public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
		//no-op	
	}

	@Override
	public void visitReadFileInfo(CasFileInfo readFileInfo) {
		//no-op	
	}

	@Override
	public void visitScoringScheme(CasScoringScheme scheme) {
		//no-op	
	}

	@Override
	public void visitReferenceDescription(CasReferenceDescription description) {
		//no-op	
	}

	@Override
	public void visitContigPair(CasContigPair contigPair) {
		//no-op	
	}

	@Override
	public void visitEnd() {
		//no-op	
	}

	@Override
	public void halted() {
		//no-op	
	}

	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		// always skip
		return null;
	}

}
