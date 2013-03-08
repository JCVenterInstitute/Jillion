package org.jcvi.jillion.assembly.ace;

import java.util.Date;
/**
 * {@code AbstractAceFileVisitor} is an {@link AceFileVisitor}
 * that implements all the methods as stubs.  By default
 * all non-void methods return {@code null}.  Users should
 * create subclasses that override the methods they wish to handle.
 * @author dkatzel
 *
 */
public abstract class AbstractAceFileVisitor implements AceFileVisitor{

	@Override
	public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
		//no-op		
	}

	@Override
	public AceContigVisitor visitContig(AceFileVisitorCallback callback,
			String contigId, int numberOfBases, int numberOfReads,
			int numberOfBaseSegments, boolean reverseComplemented) {
		//always skip
		return null;
	}

	@Override
	public void visitReadTag(String id, String type, String creator,
			long gappedStart, long gappedEnd, Date creationDate,
			boolean isTransient) {
		//no-op
		
	}

	@Override
	public AceConsensusTagVisitor visitConsensusTag(String id, String type,
			String creator, long gappedStart, long gappedEnd,
			Date creationDate, boolean isTransient) {
		//always skip
		return null;
	}

	@Override
	public void visitWholeAssemblyTag(String type, String creator,
			Date creationDate, String data) {
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

	
}
