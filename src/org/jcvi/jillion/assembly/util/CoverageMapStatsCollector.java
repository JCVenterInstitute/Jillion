package org.jcvi.jillion.assembly.util;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.jcvi.jillion.core.Rangeable;
/**
 * 
 * @author dkatzel
 *
 * @param <T>
 * @param <R>
 */
public final class CoverageMapStatsCollector<T extends Rangeable, R extends CoverageRegion<T>> implements Collector<R, CoverageStatsCombiner, CoverageMapStats>{

	
	private static final Set<java.util.stream.Collector.Characteristics> UNORDERED_AND_CONCURRENT = EnumSet.of(Collector.Characteristics.UNORDERED,
																						Collector.Characteristics.CONCURRENT
																	);
	
	
	
	
	@Override
	public Supplier<CoverageStatsCombiner> supplier() {
		return () -> new CoverageStatsCombiner();
	}

	@Override
	public BiConsumer<CoverageStatsCombiner, R> accumulator() {
		return CoverageStatsCombiner::add;
	}

	@Override
	public BinaryOperator<CoverageStatsCombiner> combiner() {
		return CoverageStatsCombiner::merge;
	}

	@Override
	public Function<CoverageStatsCombiner, CoverageMapStats> finisher() {
		return CoverageStatsCombiner::build;
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		return UNORDERED_AND_CONCURRENT;
	}

}
