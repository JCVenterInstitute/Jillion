/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.align.blast;

import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.experimental.align.blast.Hsp.Comparators;

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
