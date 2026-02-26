package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

import org.jcvi.jillion.core.util.RangeMap;
/**
 * {@link java.util.stream.Collectors} that deal with {@link Range}
 * and {@link Rangeable} {@link java.util.stream.Stream}s.
 * 
 * @since 6.0
 */
public final class RangeCollectors {

	private RangeCollectors(){
		//can not instantiate
	}

	/**
	 * Subsume the Stream of {@link Rangeable}s.
	 *
	 * @return a List of Ranges; will never be null but may be empty.
	 * @see Ranges#subsume(Collection)
	 */
	public static <T extends Rangeable>  Collector<T, ?, List<Range>> subsume(){
		return Collector.of( ()-> new ArrayList<T>(),
                ArrayList::add,
				(a, b) -> {a.addAll(b); return a;},
                Ranges::subsume);

	}

	/**
	 * Merge the Stream of Ranges.
	 * 
	 * @return a new List of Ranges; will never be null
	 * but may be empty.
	 *
	 * @see Ranges#merge(Collection)
	 */
	public static <T extends Rangeable>  Collector<T, ?, List<Range>> mergeRanges(){
		return Collector.of( ()-> new ArrayList<T>(),
                ArrayList::add,
				(a, b) -> {a.addAll(b); return a;},
                Ranges::merge);
				
	}
	/**
	 * Return a single
     * Range that covers the entire span
     * of the given Ranges collected.
	 * @return
	 */
	public static <T extends Rangeable>  Collector<T, ?, Range> inclusiveRange(){
		return Collector.of( ()-> new ArrayList<T>(),
                ArrayList::add,
				(a, b) -> {a.addAll(b); return a;},
                l-> Ranges.createInclusiveRange(l, Rangeable::asRange));
				
	}
	/**
	 * Merge the Stream of Ranges.
	 *
	 * @param maxDistance the max distance between the Ranges to be merged.
	 *
	 * @return a new List of Ranges; will never be null
	 * but may be empty.
	 *
	 * @see Ranges#merge(Collection, int)
	 */
	public static Collector<Range, ?, List<Range>> mergeRanges(int maxDistance){
		return Collector.of( ()-> new ArrayList<Range>(),
                ArrayList::add,
				(a, b) -> {a.addAll(b); return a;},
				l-> Ranges.merge(l, maxDistance ));
				
	}
	/**
	 * Collect all elements in the stream and collect them into a {@link RangeMap}.
	 * This assumes that each Range in the stream is either unique or you are OK with replacing
	 * elements with the same Range.
	 */
	public static <T extends Rangeable> Collector<T, ?, RangeMap<T>> toRangeMap(){
		return Collector.of(RangeMap::new,
				(m, r)-> m.put(r.asRange(), r),
				(a, b) -> {a.putAll(b); return a;});
	}

	/**
	 * Collect all elements in the stream and collect them into a {@link RangeMap}.
	 * This assumes that each Range in the stream is either unique or you are OK with replacing
	 * elements with the same Range.
	 *
	 * @param toRangeFunction A Function to compute the {@link Range} of the given types;
	 *                        can not be null; or return a null Range.
	 * @since 6.1.3
	 *
	 * @throws NullPointerException if toRangeFunction is null.
	 */
	public static <T extends Rangeable> Collector<T, ?, RangeMap<T>> toRangeMap(Function<T, Range> toRangeFunction){
		Objects.requireNonNull(toRangeFunction);

		return Collector.of( RangeMap::new,
				(m, r)-> m.put(toRangeFunction.apply(r), r),
				(a, b) -> {a.putAll(b); return a;});
	}

	/**
	 * Collect all elements in the stream and collect them into a {@link RangeMap}.
	 * This assumes that each Range in the stream is either unique or you are OK with replacing
	 * elements with the same Range.
	 *
	 * @param toRangeFunction A Function to compute the {@link Range} of the given types;
	 *                        can not be null; or return a null Range.
	 *
	 *  @param valueFunction A Function to compute the Value of the given types;
	 *                         can not be null; or return a null Range.
	 * @since 6.1.3
	 *
	 * @throws NullPointerException if toRangeFunction  or valueFunction is null.
	 */
	public static <T extends Rangeable, V> Collector<T, ?, RangeMap<V>> toRangeMap(Function<T, Range> toRangeFunction, Function<T, V> valueFunction){
		Objects.requireNonNull(toRangeFunction);
		Objects.requireNonNull(valueFunction);

		return Collector.of( RangeMap::new,
				(m, r)-> m.put(toRangeFunction.apply(r), valueFunction.apply(r)),
				(a, b) -> {a.putAll(b); return a;});
	}
	
	
}
