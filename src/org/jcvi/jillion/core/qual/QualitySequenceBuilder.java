/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
public final class QualitySequenceBuilder implements SequenceBuilder<PhredQuality, QualitySequence, QualitySequenceBuilder>{
	/**
	 * Default capacity set to {@value} 
	 * since that should be big enough for
	 * most next-gen reads.  The builder will
	 * grow to handle larger reads.
	 */
	private static final int DEFAULT_CAPACITY = 200;
	
	private GrowableByteArray builder;
	
	private boolean turnOffDataCompression=false;
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
	 * 
	 * @param initialCapacity the intial size of the builder's backing array.
	 * 
	 * @throws NegativeArraySizeException if initialCapacity
	 * is negative.
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
	
	
	@Override
	public QualitySequenceBuilder clear() {
		this.builder.clear();
		return this;
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
		this.builder = new GrowableByteArray(qualitySequence.toArray());
	}
	
	/**
         * Creates a new builder whose initial sequence
         * is set to the given {@link QualitySequence}.
         * @param qualitySequence the initial quality sequence
         * @param range the subRange of the sequence to use; can not be null.
         * @throws NullPointerException if either parameter is null.
         * 
         * @since 5.2
         */
        public QualitySequenceBuilder(QualitySequence qualitySequence, Range range){
            this.builder = new GrowableByteArray(qualitySequence.toArray(range));
               
        }
	/**
	 * internal copy constructor used by {@link #copy()}.
	 * @param copy
	 */
	private QualitySequenceBuilder(QualitySequenceBuilder copy){
		this.builder = copy.builder.copy();
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
		builder.append(quality.getQualityScore());
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
		builder.append(sequence.toArray());
		return this;
	}

	

	@Override
	public long getLength() {
		return builder.getCurrentLength();
	}

	@Override
	public QualitySequenceBuilder replace(int offset,
			PhredQuality replacement) {
		this.builder.replace(offset,replacement.getQualityScore());
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
		builder.insert(offset,sequence.toArray());
		return this;
	}

	@Override
	public QualitySequenceBuilder insert(int offset,
			PhredQuality qualityScore) {
		assertInsertOffsetValid(offset);
		builder.insert(offset, qualityScore.getQualityScore());
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
     * Turn off more extreme data compression which
     * will improve cpu performance at the cost
     * of the built {@link QualitySequence} taking up more memory.
     * By default, if this method is not called, then 
     * the data compression is turned ON which is the equivalent
     * of calling this method with the parameter set to {@code false}.
     * @param turnOffDataCompression {@code true} to turn off data compression;
     * {@code false} to keep data compression on.  Defaults to {@code false}. 
     * @return this.
     */
    public QualitySequenceBuilder turnOffDataCompression(boolean turnOffDataCompression){
    	this.turnOffDataCompression = turnOffDataCompression;
    	return this;
    }
    
	/**
	 * Creates a new {@link QualitySequence} using
	 * the given quality values thus far.
	 */
	@Override
	public QualitySequence build() {
		byte[] array = builder.toArray();
		if(!turnOffDataCompression){
			byte[] runLengthEncoded = RunLengthEncodedQualityCodec.INSTANCE.encode(array);
			if(runLengthEncoded.length < builder.getCurrentLength()){
				return new RunLengthEncodedQualitySequence(runLengthEncoded);
			}
		}
		
		return new EncodedQualitySequence(DefaultQualitySymbolCodec.INSTANCE, array);
	}
	

	@Override
	public QualitySequenceBuilder trim(Range range) {
		Range fullRange = Range.ofLength(builder.getCurrentLength());		
		if(range.isEmpty()){
			builder.remove(fullRange);
			return this;
		}
		Range insersection = fullRange.intersection(range);
		Range right = Range.of(insersection.getEnd()+1, builder.getCurrentLength()-1);
		Range left = Range.of(0,insersection.getBegin()-1);
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
     * Inserts the given quality score value
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, PhredQuality) insert(0,PhredQuality.valueOf(qualityScore))}
     * @param qualityScore the quality value to
     * be inserted at the beginning of the sequence.
     * @return this.
     * @throws IllegalArgumentException if qualityScore &lt; 0 or &gt; {@link Byte#MAX_VALUE}.
     */
	public QualitySequenceBuilder prepend(int qualityScore){
		return insert(0,PhredQuality.valueOf(qualityScore));
	}
	
	/**
     * Inserts the given quality scores value
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, PhredQuality) insert(0,PhredQuality.valueOf(qualityScore))}
     * @param qualityScores the quality value to
     * be inserted at the beginning of the sequence.
     * @return this.
     * @throws NullPointerException if qualityScore is null.
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
	
	/**
	 * 
	 * @param range
	 * @return
	 * 
	 * @since 5.2
	 */
        public Iterator<PhredQuality> iterator(Range range) {
                return new IteratorImpl(range);
        }

	private class IteratorImpl implements Iterator<PhredQuality>{
		private int currentOffset;
		private int endOffset;
		
		public IteratorImpl(){
		    currentOffset=0;
		    endOffset = builder.getCurrentLength()-1;
		}
		
		public IteratorImpl(Range range) {
		    currentOffset = Math.max(0, (int)range.getBegin());
		    endOffset = Math.min(builder.getCurrentLength() -1,(int) range.getEnd());
                }

        @Override
		public boolean hasNext() {
			return currentOffset<=endOffset;
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
    /**
     * Get the current quality values as a byte array.
     * 
     * @return the current qualities as a byte array;
     * will never be null but could be empty.
     * 
     * @since 5.2
     */
    public byte[] toArray() {
        return builder.toArray();
    }
	@Override
	public QualitySequenceBuilder getSelf() {
		return this;
	}
}
