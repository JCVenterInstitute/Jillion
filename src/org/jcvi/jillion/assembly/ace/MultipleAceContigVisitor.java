package org.jcvi.jillion.assembly.ace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;

public class MultipleAceContigVisitor implements AceContigVisitor{

	private final List<AceContigVisitor> delegates;
	
	public MultipleAceContigVisitor(AceContigVisitor...delegates){
		this(Arrays.asList(delegates));
	}
	public MultipleAceContigVisitor(List<? extends AceContigVisitor> delegates) {
		this.delegates = new ArrayList<AceContigVisitor>(delegates.size());
		for(AceContigVisitor visitor : delegates){
			if(visitor !=null){
				this.delegates.add(visitor);
			}
		}
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitBasesLine(mixedCaseBasecalls);
		}
		
	}

	@Override
	public void visitConsensusQualities(
			QualitySequence ungappedConsensusQualities) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitConsensusQualities(ungappedConsensusQualities);
		}
		
	}

	@Override
	public void visitAlignedReadInfo(String readId, Direction dir,
			int gappedStartPosition) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitAlignedReadInfo(readId, dir, gappedStartPosition);
		}
		
	}

	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		for(AceContigVisitor visitor : delegates){
			visitor.visitBaseSegment(gappedConsensusRange, readId);
		}
		
	}

	@Override
	public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
		List<AceContigReadVisitor> readVisitors = new ArrayList<AceContigReadVisitor>(delegates.size());
		for(AceContigVisitor visitor : delegates){
			AceContigReadVisitor readVisitor = visitor.visitBeginRead(readId, gappedLength);
			if(readVisitor !=null){
				readVisitors.add(readVisitor);
			}
		}
		if(readVisitors.isEmpty()){
			//all delegates say skip
			return null;
		}
		return new MultipleAceContigReadVisitor(readVisitors);
	}

	@Override
	public void visitEnd() {
		for(AceContigVisitor visitor : delegates){
			visitor.visitEnd();
		}
		
	}

	@Override
	public void halted() {
		for(AceContigVisitor visitor : delegates){
			visitor.halted();
		}
		
	}

}
