package org.jcvi.jillion_experimental.align.blast;

import java.util.SortedSet;
import java.util.TreeSet;

class MyBlastVisitor implements BlastVisitor{

	SortedSet<Hsp<?,?>> hsps = new TreeSet<Hsp<?,?>>(Hsp.Comparators.BIT_SCORE_BEST_TO_WORST);
	
	int numberOfHits=0;
	

	@Override
	public void visitInfo(String programName, String programVersion,
			String blastDb, String queryId) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void visitHit(BlastHit hit) {
		numberOfHits++;
		
		for(Hsp<?,?> hsp : hit.getHsps()){
			hsps.add(hsp);
		}
		
	}



	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		
	}
	
}