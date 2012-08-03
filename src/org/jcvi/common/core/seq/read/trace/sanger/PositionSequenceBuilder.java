package org.jcvi.common.core.seq.read.trace.sanger;

import java.nio.ByteBuffer;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.SequenceBuilder;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.util.GrowableShortArray;

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
	 * is set to the given {@link QualitySequence}.
	 * @param qualitySequence the initial quality sequence
	 * @throws NullPointerException if qualitySequence is null.
	 */
	public PositionSequenceBuilder(PositionSequence positionSequence){
		this.builder = new GrowableShortArray(encode(positionSequence));
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

	private short[] encode(PositionSequence sequence){
		short[] b = new short[(int)sequence.getLength()];
		int i=0;
		for(Position q : sequence){
			b[i]=encode(q);
			i++;
		}
		return b;
	}
	@Override
	public PositionSequence build() {
		//need to convert short[] into 
		//a byte[] for codec, use ByteBuffers to do this.
		short[] shorts = builder.toArray();
		ByteBuffer buffer =ByteBuffer.allocate(shorts.length*2);
		buffer.asShortBuffer().put(shorts);
		return new EncodedPositionSequence(DefaultPositionCodec.INSTANCE, buffer.array());
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
	public PositionSequenceBuilder append(
			Position quality) {
		builder.append(encode(quality));
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
	 * @throws NullPointerException if qualityScores is null.
	 */
	public PositionSequenceBuilder append(
			short[] positionValues) {
		builder.append(positionValues);
		return this;
	}

	public PositionSequenceBuilder append(
			PositionSequence sequence) {
		builder.append(encode(sequence));
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
	public PositionSequenceBuilder insert(int offset,
			PositionSequence sequence) {
		builder.insert(offset, encode(sequence));
		return this;
	}

	@Override
	public PositionSequenceBuilder insert(int offset,
			Position qualityScore) {
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
	 * @param positionValues a byte array representing quality scores
	 * (can not be null).
	 * @return this
	 * @throws NullPointerException if qualityScores is null.
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
		Range right = Range.create(range.getEnd()+1, builder.getCurrentLength()-1);
		Range left = Range.create(0,range.getBegin()-1);
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
     * @param positionValue the quality value to
     * be inserted at the beginning of the sequence.
     * @return this.
     */
	public PositionSequenceBuilder prepend(int positionValue){
		return insert(0,Position.valueOf(positionValue));
	}
	
	
	public PositionSequenceBuilder prepend(short[] qualityScores){
		return insert(0,qualityScores);
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

}
