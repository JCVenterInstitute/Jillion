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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.pos;

import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.SequenceBuilder;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.util.GrowableShortArray;

public final class PositionSequenceBuilder implements SequenceBuilder<Position, PositionSequence>{
	/**
	 * Default capacity set to {@value} 
	 * since that should be big enough for
	 * most sanger peaks reads.  The builder will
	 * grow to handle larger reads.
	 */
	private static final int DEFAULT_PEAK_CAPACITY = 800;
	
	private GrowableShortArray builder;
	
	
	
	/**
	 * Create a new empty builder with the default
	 * capacity.
	 */
	public PositionSequenceBuilder(){
		this(DEFAULT_PEAK_CAPACITY);
	}
	/**
	 * Create a new empty builder with the given
	 * capacity.
	 * @throws NegativeArraySizeException if initialCapacity
	 * is <0.
	 */
	public PositionSequenceBuilder(int initialCapacity){
		this.builder = new GrowableShortArray(initialCapacity);
	}
	/**
	 * Create a builder and set the initial
	 * sequence to the position values represented
	 * by the given short array.
	 */
	public PositionSequenceBuilder(short[] initialPositions){
		this.builder = new GrowableShortArray(initialPositions);
	}
	
	/**
	 * Creates a new builder whose initial sequence
	 * is set to the given {@link PositionSequence}.
	 * @param positionSequence the initial position sequence
	 * @throws NullPointerException if positionSequence is null.
	 */
	public PositionSequenceBuilder(PositionSequence positionSequence){
		this.builder = new GrowableShortArray(positionSequence.toArray());
	}
	/**
	 * internal copy constructor used by {@link #copy()}.
	 * @param copy
	 */
	private PositionSequenceBuilder(PositionSequenceBuilder copy){
		this.builder = copy.builder.copy();
	}
	private short encode(Position q){
		return IOUtil.toSignedShort(q.getValue());
	}

	@Override
	public PositionSequence build() {
		return new DefaultPositionSequence(builder.toArray());
	}
	/**
	 * 
	 * {@inheritDoc}
	 * @param position a single {@link PhredQuality}
	 * to be appended to the end of the current sequence
	 * (can not be null).
	 * 
	 */
	@Override
	public PositionSequenceBuilder append(
			Position position) {
		builder.append(encode(position));
		return this;
	}
	/**
	 * Appends the current contents of the given PositionSequenceBuilder
	 * to this sequence.  Any downstream changes to either of these builders
	 * will not be reflected in the other.
	 * @param other the other builder whose contents is to be appended 
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if other is null.
	 */
	public PositionSequenceBuilder append(PositionSequenceBuilder other) {
		builder.append(other.builder);
		return this;
	}
	public PositionSequenceBuilder append(
			int positionValue) {
		return append(Position.valueOf(positionValue));
	}
	/**
	 * Appends the position values represented
	 * by the given short array.
	 * to this sequence.  Any downstream changes to either of these builders
	 * will not be reflected in the other.
	 * @param positionValues a short array representing position values
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if positionScores is null.
	 */
	public PositionSequenceBuilder append(
			short[] positionValues) {
		builder.append(positionValues);
		return this;
	}

	public PositionSequenceBuilder append(
			PositionSequence sequence) {
		builder.append(sequence.toArray());
		return this;
	}

	

	@Override
	public long getLength() {
		return builder.getCurrentLength();
	}

	@Override
	public PositionSequenceBuilder replace(int offset,
			Position replacement) {
		this.builder.replace(offset,encode(replacement));
		return this;
	}

	@Override
	public PositionSequenceBuilder delete(Range range) {
		builder.remove(range);
		return this;
	}

	public PositionSequenceBuilder insert(int offset,
			PositionSequenceBuilder otherBuilder) {
		assertInsertOffsetValid(offset);
		builder.insert(offset, otherBuilder.builder);
		return this;
	}
	/**
	 * Inserts the {@link PositionSequence} 
	 * at the specified sequence offset.  The entire
	 * sequence will be added to the builder.
	 * @param offset the offset into the builder to insert
	 * the position array.
	 * @param sequence the {@link PositionSequence} to insert
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if positionScores is null.
	 * @throws IndexOutOfBoundsException if offset is less than 0 
	 * or greater than the current length of the sequence.
	 */
	public PositionSequenceBuilder insert(int offset,
			PositionSequence sequence) {
		builder.insert(offset, sequence.toArray());
		return this;
	}

	@Override
	public Position get(int offset) {
		assertInsertOffsetValid(offset);
		return Position.valueOf(builder.get(offset));
	}
	@Override
	public PositionSequenceBuilder insert(int offset,
			Position positionScore) {
		assertInsertOffsetValid(offset);
		builder.insert(offset, encode(positionScore));
		return this;
	}
	/**
	 * Inserts the given position values represented
	 * by the given byte array to the current sequence starting
	 * at the specified sequence offset.  The entire
	 * array will be added to the builder.  (If an element in the array
	 * has a byte value of 40, then the corresponding
	 * {@link PhredQuality} in the sequence will also have a value
	 * of 40).
	 * to this sequence. 
	 * @param offset the offset into the builder to insert
	 * the position array.
	 * @param positionValues a byte array representing position scores
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if positionScores is null.
	 * @throws IndexOutOfBoundsException if offset is less than 0 
	 * or greater than the current length of the sequence.
	 */
	public PositionSequenceBuilder insert(int offset,
			short[] positionValues) {
		assertInsertOffsetValid(offset);
		builder.insert(offset, positionValues);
		return this;
	}
	private void assertInsertOffsetValid(int offset) {
		if(offset <0 || offset >getLength()){
			throw new IndexOutOfBoundsException(
					String.format("invalid offset %d only values between 0 and %d are allowed", offset, getLength()));
		}
	}
	
	

	@Override
	public PositionSequenceBuilder trim(Range range) {
		if(range.isEmpty()){
			builder.remove(Range.ofLength(getLength()));
			return this;
		}
		Range right = Range.of(range.getEnd()+1, builder.getCurrentLength()-1);
		Range left = Range.of(0,range.getBegin()-1);
		builder.remove(right);
		builder.remove(left);
		return this;
	}

	@Override
	public PositionSequenceBuilder copy() {
		return new PositionSequenceBuilder(this);
	}


	@Override
	public PositionSequenceBuilder reverse() {
		builder.reverse();
		return this;
	}
	/**
     * Inserts the given position value
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, Position) insert(0,Position.valueOf(positionValue))}
     * @param positionValue the position value to
     * be inserted at the beginning of the sequence.
     * @return this.
     */
	public PositionSequenceBuilder prepend(int positionValue){
		return insert(0,Position.valueOf(positionValue));
	}
	
	
	public PositionSequenceBuilder prepend(short[] positionValues){
		return insert(0,positionValues);
	}
	/**
     * Inserts the given PositionSequenceBuilder's current
     * sequence
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, PositionSequenceBuilder) insert(0,otherBuilder)}
     * @param otherBuilder the other {@link PositionSequenceBuilder}
     * whose sequence should be prepended to this builder.
     * @return this.
     * @throws NullPointerException if otherBuilder is null.
     */
	public PositionSequenceBuilder prepend(PositionSequenceBuilder otherBuilder){
		return insert(0,otherBuilder);
	}
	/**
     * Inserts the given {@link PositionSequence} 
     * to the beginning of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, PositionSequence) insert(0,sequence)}
     * @param sequence the PositionSequence
     * whose sequence should be prepended to this builder.
     * @return this.
     * @throws NullPointerException if sequence is null.
     */
	public PositionSequenceBuilder prepend(PositionSequence sequence){
		return insert(0,sequence);
	}
	
	@Override
	public Iterator<Position> iterator() {
		return new IteratorImpl();
	}

	private class IteratorImpl implements Iterator<Position>{
		private int currentOffset=0;

		@Override
		public boolean hasNext() {
			return currentOffset<builder.getCurrentLength();
		}

		@Override
		public Position next() {
			Position next = Position.valueOf(IOUtil.toUnsignedShort(builder.get(currentOffset)));
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}

}
