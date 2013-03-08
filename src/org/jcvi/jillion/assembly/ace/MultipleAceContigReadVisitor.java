package org.jcvi.jillion.assembly.ace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MultipleAceContigReadVisitor implements AceContigReadVisitor{

	private final List<AceContigReadVisitor> delegates;
	
	public MultipleAceContigReadVisitor(AceContigReadVisitor... delegates){
		this(Arrays.asList(delegates));
	}
	public MultipleAceContigReadVisitor(
			List<? extends AceContigReadVisitor> delegates) {
		this.delegates = new ArrayList<AceContigReadVisitor>(delegates.size());
		for(AceContigReadVisitor visitor : delegates){
			if(visitor !=null){
				this.delegates.add(visitor);
			}
		}
	}

	@Override
	public void visitQualityLine(int qualLeft, int qualRight, int alignLeft,
			int alignRight) {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitQualityLine(qualLeft, qualRight, alignLeft, alignRight);
		}
		
	}

	@Override
	public void visitTraceDescriptionLine(String traceName, String phdName,
			Date date) {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitTraceDescriptionLine(traceName, phdName, date);
		}
		
	}

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitBasesLine(mixedCaseBasecalls);
		}
		
	}

	@Override
	public void visitEnd() {
		for(AceContigReadVisitor visitor : delegates){
			visitor.visitEnd();
		}
		
	}

	@Override
	public void halted() {
		for(AceContigReadVisitor visitor : delegates){
			visitor.halted();
		}
		
	}

}
