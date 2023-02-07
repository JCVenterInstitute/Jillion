package org.jcvi.jillion.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.jcvi.jillion.core.Range;
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

	private final Map<Range, T> map;
	
	public static RangeMap<Boolean> setOf(Collection<? extends Rangeable> rangeables) {
		RangeMap<Boolean> map = new RangeMap<>(rangeables.size());
		for(Rangeable r : rangeables) {
			map.put(r.asRange(), Boolean.TRUE);
		}
		return map;
	}
	public RangeMap() {
		map = new HashMap<Range, T>();
	}
	public RangeMap(int initialCapacity) {
		map = new HashMap<Range, T>(initialCapacity);
	}
	
	public T put(Range range, T obj) {
		return map.put(range, obj);
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
	
	public interface IntersectionOptions{
		boolean intersects(Range range, Callback callback);
		
		public static IntersectionOptions intersect(Range range) {
			return new DefaultIntersectionOptions(Objects.requireNonNull(range));
		}
		public static IntersectionOptions intersectEdgesAtMostOnceEach(Range range, int wiggleroomAtEdges) {
			return new OnlyIntersectEdgesAtMostOnceEach(Range.of(range.getBegin(), range.getBegin()+wiggleroomAtEdges), 
														Range.of(range.getEnd()-wiggleroomAtEdges, range.getEnd()));
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
