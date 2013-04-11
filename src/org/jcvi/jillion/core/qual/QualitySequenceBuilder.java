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
package org.jcvi.jillion.core.qual;


import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.SequenceBuilder;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
/**
 * {@code QualitySequenceBuilder} is a 
 * is a way to
 * construct a {@link QualitySequence}
 * similar to how a {@link StringBuilder} can be used
 * to create a String.  The sequence of the builder
 * can be changed by method calls.  This class
 * is not thread safe.
 * @author dkatzel
 *
 */
public final class QualitySequenceBuilder implements SequenceBuilder<PhredQuality, QualitySequence>{
	/**
	 * Default capacity set to {@value} 
	 * since that should be big enough for
	 * most next-gen reads.  The builder will
	 * grow to handle larger reads.
	 */
	private static final int DEFAULT_CAPACITY = 200;
	
	private GrowableByteArray builder;
	/**
	 * Create a new empty builder with the default
	 * capacity.
	 */
	public QualitySequenceBuilder(){
		this(DEFAULT_CAPACITY);
	}
	/**
	 * Create a new empty builder with the given
	 * capacity.
	 * @throws NegativeArraySizeException if initialCapacity
	 * is <=0.
	 */
	public QualitySequenceBuilder(int initialCapacity){
		this.builder = new GrowableByteArray(initialCapacity);
	}
	/**
	 * Create a builder and set the initial
	 * sequence to the quality values represented
	 * by the given byte array.  (If an element in the array
	 * has a byte value of 40, then the corresponding
	 * {@link PhredQuality} in the sequence will also have a value
	 * of 40.)
	 */
	public QualitySequenceBuilder(byte[] initialqualities){
		this.builder = new GrowableByteArray(initialqualities);
	}
	/**
	 * Creates a new builder whose initial sequence
	 * is set to the given {@link QualitySequence}.
	 * @param qualitySequence the initial quality sequence
	 * @throws NullPointerException if qualitySequence is null.
	 */
	public QualitySequenceBuilder(Iterable<PhredQuality> qualitySequence){
		this();
		for(PhredQuality q: qualitySequence){
			append(q);
		}
	}
	/**
	 * Creates a new builder whose initial sequence
	 * is set to the given {@link QualitySequence}.
	 * @param qualitySequence the initial quality sequence
	 * @throws NullPointerException if qualitySequence is null.
	 */
	public QualitySequenceBuilder(QualitySequence qualitySequence){
		this.builder = new GrowableByteArray(encode(qualitySequence));
	}
	/**
	 * internal copy constructor used by {@link #copy()}.
	 * @param copy
	 */
	private QualitySequenceBuilder(QualitySequenceBuilder copy){
		this.builder = copy.builder.copy();
	}
	private byte encode(PhredQuality q){
		return q.getQualityScore();
	}

	private byte[] encode(QualitySequence sequence){
		byte[] b = new byte[(int)sequence.getLength()];
		int i=0;
		for(PhredQuality q : sequence){
			b[i]=encode(q);
			i++;
		}
		return b;
	}
	
	@Override
	public PhredQuality get(int offset) {
		assertInsertOffsetValid(offset);
		return PhredQuality.valueOf(builder.get(offset));
	}
	/**
	 * 
	 * {@inheritDoc}
	 * @param quality a single {@link PhredQuality}
	 * to be appended to the end of the current sequence
	 * (can not be null).
	 * 
	 */
	@Override
	public QualitySequenceBuilder append(
			PhredQuality quality) {
		builder.append(encode(quality));
		return this;
	}
	/**
	 * Appends the current contents of the given QualitySequenceBuilder
	 * to this sequence.  Any downstream changes to either of these builders
	 * will not be reflected in the other.
	 * @param other the other builder whose contents is to be appended 
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if other is null.
	 */
	public QualitySequenceBuilder append(QualitySequenceBuilder other) {
		builder.append(other.builder);
		return this;
	}
	public QualitySequenceBuilder append(
			int qualityScore) {
		return append(PhredQuality.valueOf(qualityScore));
	}
	/**
	 * Appends the quality values represented
	 * by the given byte array.  (If an element in the array
	 * has a byte value of 40, then the corresponding
	 * {@link PhredQuality} in the sequence will also have a value
	 * of 40).
	 * to this sequence.  Any downstream changes to either of these builders
	 * will not be reflected in the other.
	 * @param qualityScores a byte array representing quality scores
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if qualityScores is null.
	 */
	public QualitySequenceBuilder append(
			byte[] qualityScores) {
		builder.append(qualityScores);
		return this;
	}

	public QualitySequenceBuilder append(
			QualitySequence sequence) {
		builder.append(encode(sequence));
		return this;
	}

	

	@Override
	public long getLength() {
		return builder.getCurrentLength();
	}

	@Override
	public QualitySequenceBuilder replace(int offset,
			PhredQuality replacement) {
		this.builder.replace(offset,encode(replacement));
		return this;
	}

	@Override
	public QualitySequenceBuilder delete(Range range) {
		builder.remove(range);
		return this;
	}

	public QualitySequenceBuilder insert(int offset,
			QualitySequenceBuilder otherBuilder) {
		assertInsertOffsetValid(offset);
		builder.insert(offset, otherBuilder.builder);
		return this;
	}
	/**
	 * Inserts the {@link QualitySequence} 
	 * at the specified sequence offset.  The entire
	 * sequence will be added to the builder.
	 * @param offset the offset into the builder to insert
	 * the quality array.
	 * @param sequence the {@link QualitySequence} to insert
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if qualityScores is null.
	 * @throws IndexOutOfBoundsException if offset is less than 0 
	 * or greater than the current length of the sequence.
	 */
	public QualitySequenceBuilder insert(int offset,
			QualitySequence sequence) {
		builder.insert(offset, encode(sequence));
		return this;
	}

	@Override
	public QualitySequenceBuilder insert(int offset,
			PhredQuality qualityScore) {
		assertInsertOffsetValid(offset);
		builder.insert(offset, encode(qualityScore));
		return this;
	}
	/**
	 * Inserts the given quality values represented
	 * by the given byte array to the current sequence starting
	 * at the specified sequence offset.  The entire
	 * array will be added to the builder.  (If an element in the array
	 * has a byte value of 40, then the corresponding
	 * {@link PhredQuality} in the sequence will also have a value
	 * of 40).
	 * to this sequence. 
	 * @param offset the offset into the builder to insert
	 * the quality array.
	 * @param qualityScores a byte array representing quality scores
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if qualityScores is null.
	 * @throws IndexOutOfBoundsException if offset is less than 0 
	 * or greater than the current length of the sequence.
	 */
	public QualitySequenceBuilder insert(int offset,
			byte[] qualityScores) {
		assertInsertOffsetValid(offset);
		builder.insert(offset, qualityScores);
		return this;
	}
	private void assertInsertOffsetValid(int offset) {
		if(offset <0 || offset >getLength()){
			throw new IndexOutOfBoundsException(
					String.format("invalid offset %d only values between 0 and %d are allowed", offset, getLength()));
		}
	}
	/**
	 * Creates a new {@link QualitySequence} using
	 * the given quality values thus far.
	 */
	@Override
	public QualitySequence build() {
		byte[] array = builder.toArray();
		byte[] runLengthEncoded = RunLengthEncodedQualityCodec.INSTANCE.encode(array);
		if(runLengthEncoded.length < builder.getCurrentLength()){
			return new RunLengthEncodedQualitySequence(runLengthEncoded);
		}
		
		return new EncodedQualitySequence(DefaultQualitySymbolCodec.INSTANCE, array);
	}
	

	@Override
	public QualitySequenceBuilder trim(Range range) {
		if(range.isEmpty()){
			builder.remove(Range.ofLength(builder.getCurrentLength()));
			return this;
		}
		Range right = Range.of(range.getEnd()+1, builder.getCurrentLength()-1);
		Range left = Range.of(0,range.getBegin()-1);
		builder.remove(right);
		builder.remove(left);
		return this;
	}

	@Override
	public QualitySequenceBuilder copy() {
		return new QualitySequenceBuilder(this);
	}


	@Override
	public QualitySequenceBuilder reverse() {
		builder.reverse();
		return this;
	}
	/**
     * Inserts the given qualityscore value
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, PhredQuality) insert(0,PhredQuality.valueOf(qualityScore))}
     * @param qualityScore the quality value to
     * be inserted at the beginning of the sequence.
     * @return this.
     * @throws IllegalArgumentException if qualityScore < 0 or > {@link Byte#MAX_VALUE}.
     */
	public QualitySequenceBuilder prepend(int qualityScore){
		return insert(0,PhredQuality.valueOf(qualityScore));
	}
	
	/**
     * Inserts the given qualityscores value
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, PhredQuality) insert(0,PhredQuality.valueOf(qualityScore))}
     * @param qualityScores the quality value to
     * be inserted at the beginning of the sequence.
     * @return this.
     * @throws IllegalArgumentException if qualityScore < 0.
     */
	public QualitySequenceBuilder prepend(byte[] qualityScores){
		return insert(0,qualityScores);
	}
	/**
     * Inserts the given QualitySequenceBuilder's current
     * sequence
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, QualitySequenceBuilder) insert(0,otherBuilder)}
     * @param otherBuilder the other {@link QualitySequenceBuilder}
     * whose sequence should be prepended to this builder.
     * @return this.
     * @throws NullPointerException if otherBuilder is null.
     */
	public QualitySequenceBuilder prepend(QualitySequenceBuilder otherBuilder){
		return insert(0,otherBuilder);
	}
	/**
     * Inserts the given {@link QualitySequence} 
     * to the beginning of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, QualitySequence) insert(0,sequence)}
     * @param sequence the QualitySequence
     * whose sequence should be prepended to this builder.
     * @return this.
     * @throws NullPointerException if sequence is null.
     */
	public QualitySequenceBuilder prepend(QualitySequence sequence){
		return insert(0,sequence);
	}
	
	@Override
	public Iterator<PhredQuality> iterator() {
		return new IteratorImpl();
	}

	private class IteratorImpl implements Iterator<PhredQuality>{
		private int currentOffset=0;

		@Override
		public boolean hasNext() {
			return currentOffset<builder.getCurrentLength();
		}

		@Override
		public PhredQuality next() {
			PhredQuality next = PhredQuality.valueOf(builder.get(currentOffset));
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}
}
