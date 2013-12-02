/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
/**
 * {@code PrimitiveArrayIterators}
 * provides factory methods to create
 * {@link Iterator} of boxed primitives
 * that iterate over primitive arrays.
 * the Boxing is performed one element at a 
 * time and only when {@link Iterator#next()}
 * is called.
 * <p/>
 * None of the returned iterators
 * are thread-safe.
 * @author dkatzel
 *
 */
public final class PrimitiveArrayIterators {
	
	private PrimitiveArrayIterators(){
		//can not instantiate
	}
	/**
	 * Create a new Iterator that will
	 * iterate over all the elements
	 * of the given array.  The iterator
	 * does NOT copy the array so any changes
	 * to the array while iterating
	 * will be reflected in the array.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 */
	public static Iterator<Integer> create(int[] array){
		return new IntIterator(array,0, array.length-1);
	}
	/**
	 * Create a new Iterator that will
	 * iterate over {@code length} elements
	 * of the given array starting at the offset 0.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param length the number of elements to iterate
	 * over; must be <= array.length and >=0.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if length < 0. or if the length
	 * is longer than the given array.
	 */
	public static Iterator<Integer> create(int[] array, int length){
		int arrayLength = array.length;
		validateParameters(length, arrayLength);
		if(length==0){
			return IteratorUtil.createEmptyIterator();
		}
		return new IntIterator(array,0, length-1);
	}
	public static void validateParameters(int length, int arrayLength) {
		if(length > arrayLength){
			throw new IllegalArgumentException("given length must be <= array.length");
		}
		if(length<0){
			throw new IllegalArgumentException("length must be >=0");
		}
	}
	/**
	 * Create a new Iterator that will start
	 * at the specified start offset (inclusive)
	 * and end at the specified end offset (also inclusive).
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param start the start offset (inclusive)
	 * to begin iterating, must be a valid offset into 
	 * the array.
	 * @param end the end offset (inclusive)
	 * to stop iterating, must be a valid offset into 
	 * the array.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if start is greater 
	 * than the end.
	 * @throws ArrayIndexOutOfBoundsException if either start
	 * or end are out of bounds of the array.
	 */
	public static Iterator<Integer> create(int[] array, int start, int end){
		validateParameters(start, end, array.length);
		return new IntIterator(array,start, end);
	}
	public static void validateParameters(int start, int end, int arrayLength) {
		if(start < 0 || start >= arrayLength){
			throw new ArrayIndexOutOfBoundsException("start " + start + " array length = " + arrayLength);
		}
		if(end < 0 || end >= arrayLength){
			throw new ArrayIndexOutOfBoundsException("end " + end +" array length = " + arrayLength);
		}
		if(start >end){
			throw new IllegalArgumentException("start > end" + start + "  " + end );
		}
	}
	
	/**
	 * Create a new Iterator that will
	 * iterate over all the elements
	 * of the given array.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 */
	public static Iterator<Byte> create(byte[] array){
		return new ByteIterator(array,0, array.length-1);
	}
	/**
	 * Create a new Iterator that will
	 * iterate over {@code length} elements
	 * of the given array starting at the offset 0.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param length the number of elements to iterate
	 * over; must be <= array.length and >=0.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if length < 0. or if the length
	 * is longer than the given array.
	 */
	public static Iterator<Byte> create(byte[] array, int length){
		int arrayLength = array.length;
		validateParameters(length, arrayLength);
		if(length==0){
			return IteratorUtil.createEmptyIterator();
		}
		return new ByteIterator(array,0, length-1);
	}
	/**
	 * Create a new Iterator that will start
	 * at the specified start offset (inclusive)
	 * and end at the specified end offset (also inclusive).
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param start the start offset (inclusive)
	 * to begin iterating, must be a valid offset into 
	 * the array.
	 * @param end the end offset (inclusive)
	 * to stop iterating, must be a valid offset into 
	 * the array.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if start is greater 
	 * than the end.
	 * @throws ArrayIndexOutOfBoundsException if either start
	 * or end are out of bounds of the array.
	 */
	public static Iterator<Byte> create(byte[] array, int start, int end){
		validateParameters(start, end, array.length);
		return new ByteIterator(array,start, end);
	}
	
	/**
	 * Create a new Iterator that will
	 * iterate over all the elements
	 * of the given array.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 */
	public static Iterator<Short> create(short[] array){
		return new ShortIterator(array,0, array.length-1);
	}
	/**
	 * Create a new Iterator that will
	 * iterate over {@code length} elements
	 * of the given array starting at the offset 0.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param length the number of elements to iterate
	 * over; must be <= array.length and >=0.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if length < 0. or if the length
	 * is longer than the given array.
	 */
	public static Iterator<Short> create(short[] array, int length){
		int arrayLength = array.length;
		validateParameters(length,arrayLength);
		if(length==0){
			return IteratorUtil.createEmptyIterator();
		}
		return new ShortIterator(array,0, length-1);
	}
	/**
	 * Create a new Iterator that will start
	 * at the specified start offset (inclusive)
	 * and end at the specified end offset (also inclusive).
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param start the start offset (inclusive)
	 * to begin iterating, must be a valid offset into 
	 * the array.
	 * @param end the end offset (inclusive)
	 * to stop iterating, must be a valid offset into 
	 * the array.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if start is greater 
	 * than the end.
	 * @throws ArrayIndexOutOfBoundsException if either start
	 * or end are out of bounds of the array.
	 */
	public static Iterator<Short> create(short[] array, int start, int end){
		validateParameters(start, end, array.length);
		return new ShortIterator(array,start, end);
	}
	
	/**
	 * Create a new Iterator that will
	 * iterate over all the elements
	 * of the given array.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 */
	public static Iterator<Long> create(long[] array){
		return new LongIterator(array,0, array.length-1);
	}
	/**
	 * Create a new Iterator that will
	 * iterate over {@code length} elements
	 * of the given array starting at the offset 0.
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param length the number of elements to iterate
	 * over; must be <= array.length and >=1.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if length < 1. or if the length
	 * is longer than the given array.
	 */
	public static Iterator<Long> create(long[] array, int length){
		int arrayLength = array.length;
		validateParameters(length, arrayLength);
		if(length==0){
			return IteratorUtil.createEmptyIterator();
		}
		return new LongIterator(array,0, length-1);
	}
	/**
	 * Create a new Iterator that will start
	 * at the specified start offset (inclusive)
	 * and end at the specified end offset (also inclusive).
	 * @param array the array of primitives
	 * to iterate over; can not be null.
	 * @param start the start offset (inclusive)
	 * to begin iterating, must be a valid offset into 
	 * the array.
	 * @param end the end offset (inclusive)
	 * to stop iterating, must be a valid offset into 
	 * the array.
	 * @return a new Iterator will never be null.
	 * @throws NullPointerException if array is null.
	 * @throws IllegalArgumentException if start is greater 
	 * than the end.
	 * @throws ArrayIndexOutOfBoundsException if either start
	 * or end are out of bounds of the array.
	 */
	public static Iterator<Long> create(long[] array, int start, int end){
		validateParameters(start, end, array.length);
		return new LongIterator(array,start, end);
	}
	
	private static class IntIterator implements Iterator<Integer>{

		private final int[] array;
		private int currentOffset;
		private final int endOffset;
		
		public IntIterator(int[] array, int start, int end){
			this.array = array;
			this.endOffset = end;
			this.currentOffset = start;
		}
		
		@Override
		public boolean hasNext() {
			return currentOffset<=endOffset;
		}

		@Override
		public Integer next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			Integer next =array[currentOffset];
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();			
		}
		
	}

	private static class ByteIterator implements Iterator<Byte>{

		private final byte[] array;
		private int currentOffset;
		private final int endOffset;
		
		public ByteIterator(byte[] array, int start, int end){
			this.array = array;
			this.endOffset = end;
			this.currentOffset = start;
		}
		
		@Override
		public boolean hasNext() {
			return currentOffset<=endOffset;
		}

		@Override
		public Byte next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			Byte next =array[currentOffset];
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();			
		}
		
	}
	
	private static class ShortIterator implements Iterator<Short>{

		private final short[] array;
		private int currentOffset;
		private final int endOffset;
		
		public ShortIterator(short[] array, int start, int end){
			this.array = array;
			this.endOffset = end;
			this.currentOffset = start;
		}
		
		@Override
		public boolean hasNext() {
			return currentOffset<=endOffset;
		}

		@Override
		public Short next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			Short next =array[currentOffset];
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();			
		}
		
	}
	
	private static class LongIterator implements Iterator<Long>{

		private final long[] array;
		private int currentOffset;
		private final int endOffset;
		
		public LongIterator(long[] array, int start, int end){
			this.array = array;
			this.endOffset = end;
			this.currentOffset = start;
		}
		
		@Override
		public boolean hasNext() {
			return currentOffset<=endOffset;
		}

		@Override
		public Long next() {
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			Long next =array[currentOffset];
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();			
		}
		
	}
}
