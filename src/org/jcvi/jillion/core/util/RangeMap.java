package org.jcvi.jillion.core.util;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.RangeCollectors;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.util.streams.ThrowingTriConsumer;

/**
 * Wrapper around a {@code Map<Range, T>} with methods to get values in the map that intersect ranges inside the map.
 * @author dkatzel
 *
 * @param <T> the value type.
 * 
 * @since 6.0
 */
public class RangeMap<T> {

	private final NavigableMap<Range, T> map;
	
	public static RangeMap<Boolean> setOf(Collection<? extends Rangeable> rangeables) {
		RangeMap<Boolean> map = new RangeMap<>();
		for(Rangeable r : rangeables) {
			map.put(r.asRange(), Boolean.TRUE);
		}
		return map;
	}
	public RangeMap() {
		map = new TreeMap<>(Range.Comparators.ARRIVAL);
	}
	public RangeMap(int initialCapacity) {
		map = new TreeMap<>(Range.Comparators.ARRIVAL);
	}
	
	public T put(Range range, T obj) {
		return map.put(range, obj);
	}
	
	public void putAll(RangeMap<T> other) {
		map.putAll(other.map);

	}

	/**
	 * Iterate over each of the Ranges in this map.
	 * @param consumer the consumer; can not be null.
	 *
	 * @since 6.0.2
	 */
	public void forEach(BiConsumer<Range, T> consumer){
		Objects.requireNonNull(consumer);
		map.forEach(consumer);
	}
	public T get(Range range) {
		return map.get(range);
	}
	public T remove(Range range) {
		return map.remove(range);
	}
	/**
	 * Are there any elements in this Map.
	 * @return {@code false} if there are no Ranges; otherwise {@code true}.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}
	/**
	 * Get the number of elements in this Map.
	 * @return
	 */
	public int size() {
		return map.size();
	}
	public <E extends Throwable> void getAllThatIntersect(Range range, ThrowingTriConsumer<Range, T, Callback, E> consumer) throws E{
		getAllThatIntersect(IntersectionOptions.intersect(range), consumer);
	}
	public <E extends Throwable> void getAllThatIntersect(List<Range> ranges, ThrowingTriConsumer<Range, T, Callback, E> consumer) throws E{
		getAllThatIntersect(IntersectionOptions.intersect(ranges), consumer);
	}
	public <E extends Throwable> void getAllThatIntersect(IntersectionOptions intersectionOptions, ThrowingTriConsumer<Range, T, Callback, E> consumer) throws E{
		Objects.requireNonNull(consumer);
		Objects.requireNonNull(intersectionOptions);
		CallbackImpl callback = new CallbackImpl();
		for(Entry<Range, T> entry : map.entrySet()) {
			if(intersectionOptions.intersects(entry.getKey(), callback)) {
				consumer.accept(entry.getKey(), entry.getValue(), callback);
				
			}
			if(callback.halt) {
				break;
			}
		}
	}
	public List<Range> computeMergedRanges() {
		return Ranges.merge(map.keySet());
	}
	@FunctionalInterface
	public interface IntersectionOptions{
		boolean intersects(Range range, Callback callback);
		
		public static IntersectionOptions intersect(Range range) {
			return new DefaultIntersectionOptions(Objects.requireNonNull(range));
		}
		public static IntersectionOptions intersect(Collection<Range> ranges) {
			return new DefaultMultiIntersectionOptions(Objects.requireNonNull(ranges));
		}
		public static IntersectionOptions intersectEdgesAtMostOnceEach(Range range, int wiggleroomAtEdges) {
			return new OnlyIntersectEdgesAtMostOnceEach(Range.of(range.getBegin(), range.getBegin()+wiggleroomAtEdges), 
														Range.of(range.getEnd()-wiggleroomAtEdges, range.getEnd()));
		}
		public static IntersectionOptions abuts(Range range) {
			return new OnlyIntersectEdgesAtMostOnceEach(Range.of(range.getBegin()-1, range.getBegin()), 
														Range.of(range.getEnd(), range.getEnd()+1));
		}
		public static IntersectionOptions abuts(Collection<Range> ranges) {
			List<Range> abuttingRanges = ranges.stream()
					.flatMap(range -> Stream.of(Range.of(range.getBegin()-1, range.getBegin()), 
							Range.of(range.getEnd(), range.getEnd()+1)))
					.collect(RangeCollectors.mergeRanges());
			return new DefaultMultiIntersectionOptions(abuttingRanges);
		}
		
		public static IntersectionOptions superRangeOf(Range range) {
			return (r, callback)-> range.isSubRangeOf(r);
			
		}
	}
	
	private static class DefaultIntersectionOptions implements IntersectionOptions{
		private final Range rangeOfInterest;

		public DefaultIntersectionOptions(Range rangeOfInterest) {
			this.rangeOfInterest = rangeOfInterest;
		}

		@Override
		public boolean intersects(Range range, Callback callback) {
			return rangeOfInterest.intersects(range);
		}
	}
	
	
	
	private static class DefaultMultiIntersectionOptions implements IntersectionOptions{
		private final List<Range> rangesOfInterest;

		public DefaultMultiIntersectionOptions(Collection<Range> rangesOfInterest) {
			this.rangesOfInterest = new ArrayList<>(rangesOfInterest);
			this.rangesOfInterest.forEach(Objects::requireNonNull);
		}

		@Override
		public boolean intersects(Range range, Callback callback) {
			return Ranges.intersects(rangesOfInterest, range);
		}
	}
	
	
	private static class OnlyIntersectEdgesAtMostOnceEach implements IntersectionOptions{
		private final Range leftRange, rightRange;
		private boolean foundLeft, foundRight;
		
		public OnlyIntersectEdgesAtMostOnceEach(Range leftRange, Range rightRange) {
			this.leftRange = leftRange;
			this.rightRange = rightRange;
		}

		@Override
		public boolean intersects(Range range, Callback callback) {
			boolean intersects= leftRange.intersects(range);
			if(intersects) {
				foundLeft=intersects;
				if(foundLeft && foundRight) {
					callback.halt();
					
				}
				return true;
			}
			intersects= rightRange.intersects(range);
			if(intersects) {
				foundRight=intersects;
				if(foundLeft && foundRight) {
					callback.halt();
					
				}
				return true;
			}
			return false;
		}
		
		
	}
	private static class CallbackImpl implements Callback{
		private volatile boolean halt =false;

		@Override
		public void halt() {
			halt=true;
			
		}
		
	}
	public interface Callback{
		void halt();
	}
	
	
}
