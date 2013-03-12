package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.align.CasScoringScheme;

public abstract class AbstractCasFileVisitor2 implements CasFileVisitor2{

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
