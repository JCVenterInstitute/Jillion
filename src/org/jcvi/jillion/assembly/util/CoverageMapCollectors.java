package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.core.Rangeable;

public final class CoverageMapCollectors {

	private CoverageMapCollectors(){
		//can not instantiate
	}
	/**
	 * Compute a new {@link CoverageMapStats} 
	 * by collecting the values from an all the regions
	 * in a {@link CoverageMap} 
	 * 
	 * <pre>
	 * {@code CoverageMapStats stats = coverageMap.stream()
					.parallel()
					.collect(CoverageMapCollectors.computeStats());
				}
	 * </pre>
	 * @return
	 */
	public static <T extends Rangeable, R extends CoverageRegion<T>> CoverageMapStatsCollector<T, R> computeStats(){
		return new CoverageMapStatsCollector<>();
	}
}
