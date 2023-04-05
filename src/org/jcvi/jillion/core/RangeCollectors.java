package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import org.jcvi.jillion.core.util.RangeMap;
/**
 * {@link java.util.stream.Collectors} that deal with {@link Range} {@link java.util.stream.Stream}s.
 * 
 * @since 6.0
 */
public final class RangeCollectors {

	private RangeCollectors(){
		//can not instantiate
	}
	
	public static Collector<Range, ?, List<Range>> mergeRanges(){
		return Collector.of( ()-> new ArrayList<Range>(),
				(l, r)-> l.add(r),
				(a, b) -> {a.addAll(b); return a;},
				l-> Ranges.merge(l));
				
	}
	/**
	 * Return a single
     * Range that covers the entire span
     * of the given Ranges collected.
	 * @return
	 */
	public static Collector<Range, ?, Range> inclusiveRange(){
		return Collector.of( ()-> new ArrayList<Range>(),
				(l, r)-> l.add(r),
				(a, b) -> {a.addAll(b); return a;},
				l-> Ranges.createInclusiveRange(l));
				
	}
	
	public static Collector<Range, ?, List<Range>> mergeRanges(int maxDistance){
		return Collector.of( ()-> new ArrayList<Range>(),
				(l, r)-> l.add(r),
				(a, b) -> {a.addAll(b); return a;},
				l-> Ranges.merge(l, maxDistance ));
				
	}
	/**
	 * Collect all elements in the stream and collect them into a {@link RangeMap}.
	 * This assumes that each Range in the stream is either unique or you are OK with replacing
	 * elements with the same Range.
	 */
	public static <T extends Rangeable> Collector<T, ?, RangeMap<T>> toRangeMap(){
		return Collector.of( ()-> new RangeMap<T>(),
				(m, r)-> m.put(r.asRange(), r),
				(a, b) -> {a.putAll(b); return a;});
	}
	
	
}
