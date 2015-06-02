package org.jcvi.jillion.assembly.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import org.jcvi.jillion.core.Rangeable;
/**
 * Utility class that contains helpful {@link Collector}s
 * for {@link CoverageMap} related computations.
 * 
 * @author dkatzel
 *
 * @since 5.0
 */
public final class CoverageMapCollectors {

	private CoverageMapCollectors(){
		//can not instantiate
	}
	/**
	 * Returns a {@code Collector} that accumulates the input elements 
	 * of a {@link CoverageMap} into a new {@link CoverageMapStats}.
	 * 
	 * @apiNote
	 * 
	 * <pre>
	 * {@code CoverageMapStats stats = coverageMap.regions()
					.parallel()
					.collect(CoverageMapCollectors.computeStats());
				}
	 * </pre>
	 * @return a new CoverageMapStats Collector
	 */
	public static <T extends Rangeable, R extends CoverageRegion<T>> Collector<R, CoverageStatsCombiner, CoverageMapStats> computeStats(){
		//return new CoverageMapStatsCollector<>();
		
		return Collector.of(CoverageStatsCombiner::new, 
				CoverageStatsCombiner::add, 
				CoverageStatsCombiner::merge, 
				CoverageStatsCombiner::build,
				Characteristics.UNORDERED, Characteristics.CONCURRENT
				);
	}
	
	
	 /**
     * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code CoverageMap}.
     *
     * @param <T> the type of the input elements which must implement the {@link Rangeable} interface.
     * @return a {@code Collector} which collects all the input elements into a
     * {@code CoverageMap}.
     */
	public static <T extends Rangeable> Collector<T, List<T>, CoverageMap<T>> toCoverageMap(){
		return Collector.of(ArrayList::new,
				List::add,
				(a,b) ->{ a.addAll(b); return a;},
                (List<T> list) ->new CoverageMapBuilder<T>(list).build(),
                Characteristics.UNORDERED
                );
	}
	/**
     * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code CoverageMap} that has limited each coverage region to the specified maxCoverage.
     * Any elements that provide additional coverage than the specified max will not be included in the CoverageMap.
     *
     * @param maxCoverage the maxCoverage any CoverageRegion will be allowed to have; must be >=0.
     * 
     * @param <T> the type of the input elements which must implement the {@link Rangeable} interface.
     * 
     * @return a {@code Collector} which collects all the input elements into a
     * {@code CoverageMap}.
     * 
     * @see CoverageMapBuilder#maxAllowedCoverage(int)
     * 
     * @throws IllegalArgumentException if maxCoverage < 0.
     * 
     * @implNote filtering parallel streams may return CoverageMaps which contain different elements or even
     * have different levels of coverage at certain coverage regions.  This is because the max coverage filter
     * is partially based on encounter order
     */
	public static <T extends Rangeable> Collector<T, List<T>, CoverageMap<T>> toCoverageMap(int maxCoverage){
		if(maxCoverage < 1){
			throw new IllegalArgumentException("max coverage must be >=0");
		}
		//NOTE: no characteristics set since
		//we need to preserve encounter order so filtering
		//results in the same CoverageMaps being produced.
		return Collector.of(ArrayList::new,
				List::add,
				(a,b) ->{ a.addAll(b); return a;},
                (List<T> list) ->new CoverageMapBuilder<T>(list).maxAllowedCoverage(maxCoverage).build()
                );
	}
	/**
	 * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code CoverageMap} that will try to keep preferred maximum coverage depth any {@link CoverageRegion}
	 * in the resulting CoverageMap will have while still maintaining the required minimum coverage.
	 * 
	 * @param preferredMaxCoverage the maximum coverage any {@link CoverageRegion}
	 * will strive to have; must be >=0.
	 * @param requiredMinCoverage the minimum coverage any 
	 * {@link CoverageRegion} must have even at the expense of exceeding the 
	 * preferredMaxCoverage; must be >=0.
	 * 
     * @param <T> the type of the input elements which must implement the {@link Rangeable} interface.
     * 
     * @return a {@code Collector} which collects all the input elements into a
     * {@code CoverageMap}.
     * 
     * @see CoverageMapBuilder#maxAllowedCoverage(int, int)
     * 
     * @throws IllegalArgumentException if either parameter is < 0
	 */
	public static <T extends Rangeable> Collector<T, List<T>, CoverageMap<T>> toCoverageMap(int preferredMaxCoverage, int requiredMinCoverage){
		if(preferredMaxCoverage < 1){
			throw new IllegalArgumentException("max coverage must be >=0");
		}
		if(requiredMinCoverage < 1){
			throw new IllegalArgumentException("min coverage must be >=0");
		}
		//NOTE: no characteristics set since
		//we need to preserve encounter order so filtering
		//results in the same CoverageMaps being produced.
		return Collector.of(ArrayList::new,
				List::add,
				(a,b) ->{ a.addAll(b); return a;},
                (List<T> list) ->new CoverageMapBuilder<T>(list).maxAllowedCoverage(preferredMaxCoverage, requiredMinCoverage).build()
                );
	}
	
}
