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
package org.jcvi.jillion.assembly.util;

import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class CoverageStatsCombiner {

	
	LongAccumulator minCoverage = new LongAccumulator(
						(current, min) -> current < min ? current : min
				, Integer.MAX_VALUE);
	LongAccumulator maxCoverage = new LongAccumulator(
							(current, min) -> current > min ? current : min
					, Integer.MIN_VALUE);
	
	LongAdder totalLength = new LongAdder();
	LongAdder totalCoverage = new LongAdder();
	
	
	public CoverageStatsCombiner add(CoverageRegion<?> region){
		long regionLength = region.getLength();
		totalLength.add(regionLength);
		
		int coverageDepth = region.getCoverageDepth();
		
		totalCoverage.add(coverageDepth * regionLength);
		minCoverage.accumulate(coverageDepth);
		maxCoverage.accumulate(coverageDepth);
		
		
		return this;
	}
	
	public CoverageStatsCombiner merge(CoverageStatsCombiner other){
		totalLength.add(other.totalLength.longValue());
		totalCoverage.add(other.totalCoverage.longValue());
		
		minCoverage.accumulate(other.minCoverage.intValue());
		maxCoverage.accumulate(other.maxCoverage.intValue());
		
		return this;
	}
	
	public CoverageMapStats build(){
		long totalLength = this.totalLength.longValue();
		if(totalLength ==0){
			return new CoverageMapStats(0, 0, 0D);
		}
		return new CoverageMapStats(minCoverage.intValue(), maxCoverage.intValue(), 
				totalCoverage.longValue()/this.totalLength.doubleValue());
	}
}
