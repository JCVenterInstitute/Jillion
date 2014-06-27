package org.jcvi.jillion.assembly.util;

/**
 * {@code CoverageMapStats} is a class
 * that contains commonly used statistics such as
 * min, max, and average coverage values for a single
 * {@link CoverageMap}.
 * 
 *  @implNote This implementation is not thread safe. However, it is safe to use
 * {@link CoverageMapStatsCollector#CoverageMapStatsCollector()} on a parallel stream, because the parallel
 * implementation of {@link java.util.stream.Stream#collect Stream.collect()}
 * provides the necessary partitioning, isolation, and merging of results for
 * safe and efficient parallel execution.
 *
 * <p>This implementation does not check for overflow of the sum.
 * @since 5
 * @author dkatzel
 *
 */
public final class CoverageMapStats{

	private final int minCoverage, maxCoverage;
	private final double avgCoverage;
	
	public CoverageMapStats(int minCoverage, int maxCoverage, double avgCoverage) {
		this.minCoverage = minCoverage;
		this.maxCoverage = maxCoverage;
		this.avgCoverage = avgCoverage;
	}

	public int getMinCoverage() {
		return minCoverage;
	}

	public int getMaxCoverage() {
		return maxCoverage;
	}

	public double getAvgCoverage() {
		return avgCoverage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(avgCoverage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + maxCoverage;
		result = prime * result + minCoverage;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CoverageMapStats)) {
			return false;
		}
		CoverageMapStats other = (CoverageMapStats) obj;
		if (Double.doubleToLongBits(avgCoverage) != Double
				.doubleToLongBits(other.avgCoverage)) {
			return false;
		}
		if (maxCoverage != other.maxCoverage) {
			return false;
		}
		if (minCoverage != other.minCoverage) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CoverageMapStats [minCoverage=" + minCoverage
				+ ", maxCoverage=" + maxCoverage + ", avgCoverage="
				+ avgCoverage + "]";
	}
	
	
	
	//public  Collector<CoverageRegion<T>, CoverageStatsCombiner, CoverageMapStats>
}
