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
package org.jcvi.jillion.internal.core.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.util.Arrays;
import java.util.BitSet;

/**
 * {@code LargeBitSet} is a modified version of
 * {@link BitSet} changes the {@link BitSet} API to use long index parameter
 * values instead of ints. This allows LargeBitSet to take more than {@link Integer#MAX_VALUE}
 * number of bits.  Since {@link BitSet} uses a long array to internally
 * store the bits, it is actually possible to store 2<sup>38</sup> -1
 * bits in the set, but the {@link BitSet} API only uses int parameters,
 * it wasn't possible to fully utilize all the bits.
 * <p/>
 * 
 * 
 * This class implements a vector of bits that grows as needed. Each
 * component of the bit set has a <code>boolean</code> value. The
 * bits of a <code>BitSet</code> are indexed by nonnegative integers.
 * Individual indexed bits can be examined, set, or cleared. One
 * <code>BitSet</code> may be used to modify the contents of another
 * <code>BitSet</code> through logical AND, logical inclusive OR, and
 * logical exclusive OR operations.
 * <p>
 * By default, all bits in the set initially have the value
 * <code>false</code>.
 * <p>
 * Every bit set has a current size, which is the number of bits
 * of space currently in use by the bit set. Note that the size is
 * related to the implementation of a bit set, so it may change with
 * implementation. The length of a bit set relates to logical length
 * of a bit set and is defined independently of implementation.
 * <p>
 * Unless otherwise noted, passing a null parameter to any of the
 * methods in a <code>BitSet</code> will result in a
 * <code>NullPointerException</code>.
 *
 * <p>A <code>BitSet</code> is not safe for multithreaded use without
 * external synchronization.
 *
 * @author  Arthur van Hoff
 * @author  Michael McCloskey
 * @author  Martin Buchholz
 * @version %I%, %G%
 * @since   JDK1.0
 */
public class LargeBitSet implements Cloneable, java.io.Serializable {
    /*
     * BitSets are packed into arrays of "words."  Currently a word is
     * a long, which consists of 64 bits, requiring 6 address bits.
     * The choice of word size is determined purely by performance concerns.
     */
    private final static int ADDRESS_BITS_PER_WORD = 6;
    private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
    private final static int BIT_INDEX_MASK = BITS_PER_WORD - 1;

    /* Used to shift left or right for a partial word mask */
    private static final long WORD_MASK = 0xffffffffffffffffL;

    /**
     * @serialField bits long[]
     *
     * The bits in this BitSet.  The ith bit is stored in bits[i/64] at
     * bit position i % 64 (where bit position 0 refers to the least
     * significant bit and 63 refers to the most significant bit).
     */
    private static final ObjectStreamField[] serialPersistentFields = {
	new ObjectStreamField("bits", long[].class),
    };

    /**
     * The internal field corresponding to the serialField "bits".
     */
    private long[] words;

    /**
     * The number of words in the logical size of this BitSet.
     */
    private transient int wordsInUse = 0;

    /**
     * Whether the size of "words" is user-specified.  If so, we assume
     * the user knows what he's doing and try harder to preserve it.
     */
    private transient boolean sizeIsSticky = false;

    /* use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 7997698588986878753L;

    /**
     * Given a bit index, return word index containing it.
     */
    private static int wordIndex(long bitIndex) {
        long ret= bitIndex >> ADDRESS_BITS_PER_WORD;
	    if(ret > Integer.MAX_VALUE){
	    	throw new IndexOutOfBoundsException(bitIndex + " bit index is larger than Int.MAX word index");
	    }
	    return (int)ret;
    }

    /**
     * Every public method must preserve these invariants.
     */
    private void checkInvariants() {
	assert(wordsInUse == 0 || words[wordsInUse - 1] != 0);
	assert(wordsInUse >= 0 && wordsInUse <= words.length);
	assert(wordsInUse == words.length || words[wordsInUse] == 0);
    }

    /**
     * Set the field wordsInUse with the logical size in words of the bit
     * set.  WARNING:This method assumes that the number of words actually
     * in use is less than or equal to the current value of wordsInUse!
     */
    private void recalculateWordsInUse() {
        // Traverse the bitset until a used word is found
        int i;
        for (i = wordsInUse-1; i >= 0; i--)
	    if (words[i] != 0)
		break;

        wordsInUse = i+1; // The new logical size
    }

    /**
     * Creates a new bit set. All bits are initially <code>false</code>.
     */
    public LargeBitSet() {
	initWords(BITS_PER_WORD);
	sizeIsSticky = false;
    }

    /**
     * Creates a bit set whose initial size is large enough to explicitly
     * represent bits with indices in the range <code>0</code> through
     * <code>nbits-1</code>. All bits are initially <code>false</code>.
     *
     * @param     nbits   the initial size of the bit set.
     * @exception NegativeArraySizeException if the specified initial size
     *               is negative.
     */
    public LargeBitSet(long nbits) {
	// nbits can't be negative; size 0 is OK
	if (nbits < 0)
	    throw new NegativeArraySizeException("nbits < 0: " + nbits);

	initWords(nbits);
	sizeIsSticky = true;
    }

    private void initWords(long nbits) {
	words = new long[wordIndex(nbits-1) + 1];
    }

    /**
     * Ensures that the BitSet can hold enough words.
     * @param wordsRequired the minimum acceptable number of words.
     */
    private void ensureCapacity(int wordsRequired) {
	if (words.length < wordsRequired) {
	    // Allocate larger of doubled size or required size
	    int request = Math.max(2 * words.length, wordsRequired);
            words = Arrays.copyOf(words, request);
            sizeIsSticky = false;
        }
    }

    /**
     * Ensures that the BitSet can accommodate a given wordIndex,
     * temporarily violating the invariants.  The caller must
     * restore the invariants before returning to the user,
     * possibly using recalculateWordsInUse().
     * @param	wordIndex the index to be accommodated.
     */
    private void expandTo(int wordIndex) {
	int wordsRequired = wordIndex+1;
	if (wordsInUse < wordsRequired) {
	    ensureCapacity(wordsRequired);
	    wordsInUse = wordsRequired;
	}
    }

    /**
     * Checks that fromIndex ... toIndex is a valid range of bit indices.
     */
    private static void checkRange(long fromIndex, long toIndex) {
	if (fromIndex < 0)
	    throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        if (toIndex < 0)
	    throw new IndexOutOfBoundsException("toIndex < 0: " + toIndex);
        if (fromIndex > toIndex)
	    throw new IndexOutOfBoundsException("fromIndex: " + fromIndex +
                                                " > toIndex: " + toIndex);
    }



    /**
     * Sets the bit at the specified index to <code>true</code>.
     *
     * @param     bitIndex   a bit index.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void set(long bitIndex) {
	if (bitIndex < 0)
	    throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);

        int wordIndex = wordIndex(bitIndex);
	expandTo(wordIndex);

	words[wordIndex] |= (1L << bitIndex); // Restores invariants

	checkInvariants();
    }

    /**
     * Sets the bit at the specified index to the specified value.
     *
     * @param     bitIndex   a bit index.
     * @param     value a boolean value to set.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     1.4
     */
    public void set(long bitIndex, boolean value) {
        if (value)
            set(bitIndex);
        else
            clear(bitIndex);
    }

    /**
     * Sets the bits from the specified <tt>fromIndex</tt> (inclusive) to the
     * specified <tt>toIndex</tt> (exclusive) to <code>true</code>.
     *
     * @param     fromIndex   index of the first bit to be set.
     * @param     toIndex index after the last bit to be set.
     * @exception IndexOutOfBoundsException if <tt>fromIndex</tt> is negative,
     *            or <tt>toIndex</tt> is negative, or <tt>fromIndex</tt> is
     *            larger than <tt>toIndex</tt>.
     * @since     1.4
     */
    public void set(long fromIndex, long toIndex) {
	checkRange(fromIndex, toIndex);

	if (fromIndex == toIndex)
	    return;

        // Increase capacity if necessary
        int startWordIndex = wordIndex(fromIndex);
        int endWordIndex   = wordIndex(toIndex - 1);
	expandTo(endWordIndex);

	long firstWordMask = WORD_MASK << fromIndex;
	long lastWordMask  = WORD_MASK >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            // Case 1: One word
	    words[startWordIndex] |= (firstWordMask & lastWordMask);
        } else {
	    // Case 2: Multiple words
	    // Handle first word
	    words[startWordIndex] |= firstWordMask;

	    // Handle intermediate words, if any
	    for (int i = startWordIndex+1; i < endWordIndex; i++)
		words[i] = WORD_MASK;

	    // Handle last word (restores invariants)
	    words[endWordIndex] |= lastWordMask;
	}

	checkInvariants();
    }

    /**
     * Sets the bits from the specified <tt>fromIndex</tt> (inclusive) to the
     * specified <tt>toIndex</tt> (exclusive) to the specified value.
     *
     * @param     fromIndex   index of the first bit to be set.
     * @param     toIndex index after the last bit to be set
     * @param     value value to set the selected bits to
     * @exception IndexOutOfBoundsException if <tt>fromIndex</tt> is negative,
     *            or <tt>toIndex</tt> is negative, or <tt>fromIndex</tt> is
     *            larger than <tt>toIndex</tt>.
     * @since     1.4
     */
    public void set(long fromIndex, long toIndex, boolean value) {
	if (value)
            set(fromIndex, toIndex);
        else
            clear(fromIndex, toIndex);
    }

    /**
     * Sets the bit specified by the index to <code>false</code>.
     *
     * @param     bitIndex   the index of the bit to be cleared.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void clear(long bitIndex) {
	if (bitIndex < 0)
	    throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);

	int wordIndex = wordIndex(bitIndex);
	if (wordIndex >= wordsInUse)
	    return;

	words[wordIndex] &= ~(1L << bitIndex);

	recalculateWordsInUse();
	checkInvariants();
    }

    /**
     * Sets the bits from the specified <tt>fromIndex</tt> (inclusive) to the
     * specified <tt>toIndex</tt> (exclusive) to <code>false</code>.
     *
     * @param     fromIndex   index of the first bit to be cleared.
     * @param     toIndex index after the last bit to be cleared.
     * @exception IndexOutOfBoundsException if <tt>fromIndex</tt> is negative,
     *            or <tt>toIndex</tt> is negative, or <tt>fromIndex</tt> is
     *            larger than <tt>toIndex</tt>.
     * @since     1.4
     */
    public void clear(long fromIndex, long toIndex) {
	checkRange(fromIndex, toIndex);

	if (fromIndex == toIndex)
	    return;

        int startWordIndex = wordIndex(fromIndex);
	if (startWordIndex >= wordsInUse)
	    return;

        int endWordIndex = wordIndex(toIndex - 1);
	if (endWordIndex >= wordsInUse) {
	    toIndex = length();
	    endWordIndex = wordsInUse - 1;
	}

	long firstWordMask = WORD_MASK << fromIndex;
	long lastWordMask  = WORD_MASK >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            // Case 1: One word
            words[startWordIndex] &= ~(firstWordMask & lastWordMask);
        } else {
	    // Case 2: Multiple words
	    // Handle first word
	    words[startWordIndex] &= ~firstWordMask;

	    // Handle intermediate words, if any
	    for (int i = startWordIndex+1; i < endWordIndex; i++)
		words[i] = 0;

	    // Handle last word
	    words[endWordIndex] &= ~lastWordMask;
	}

	recalculateWordsInUse();
	checkInvariants();
    }

    /**
     * Sets all of the bits in this BitSet to <code>false</code>.
     *
     * @since   1.4
     */
    public void clear() {
        while (wordsInUse > 0)
            words[--wordsInUse] = 0;
    }

    /**
     * Returns the value of the bit with the specified index. The value
     * is <code>true</code> if the bit with the index <code>bitIndex</code>
     * is currently set in this <code>BitSet</code>; otherwise, the result
     * is <code>false</code>.
     *
     * @param     bitIndex   the bit index.
     * @return    the value of the bit with the specified index.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     */
    public boolean get(long bitIndex) {
	if (bitIndex < 0)
	    throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);

	checkInvariants();

	int wordIndex = wordIndex(bitIndex);
	return (wordIndex < wordsInUse)
	    && ((words[wordIndex] & (1L << bitIndex)) != 0);
    }

    /**
     * Returns a new <tt>BitSet</tt> composed of bits from this <tt>BitSet</tt>
     * from <tt>fromIndex</tt> (inclusive) to <tt>toIndex</tt> (exclusive).
     *
     * @param     fromIndex   index of the first bit to include.
     * @param     toIndex     index after the last bit to include.
     * @return    a new <tt>BitSet</tt> from a range of this <tt>BitSet</tt>.
     * @exception IndexOutOfBoundsException if <tt>fromIndex</tt> is negative,
     *            or <tt>toIndex</tt> is negative, or <tt>fromIndex</tt> is
     *            larger than <tt>toIndex</tt>.
     * @since   1.4
     */
    public LargeBitSet get(long fromIndex, long toIndex) {
	checkRange(fromIndex, toIndex);

	checkInvariants();

	int len = length();

        // If no set bits in range return empty bitset
        if (len <= fromIndex || fromIndex == toIndex)
            return new LargeBitSet(0);

        // An optimization
        if (toIndex > len)
            toIndex = len;

        LargeBitSet result = new LargeBitSet(toIndex - fromIndex);
        int targetWords = wordIndex(toIndex - fromIndex - 1) + 1;
        int sourceIndex = wordIndex(fromIndex);
	boolean wordAligned = ((fromIndex & BIT_INDEX_MASK) == 0);

        // Process all words but the last word
        for (int i = 0; i < targetWords - 1; i++, sourceIndex++)
            result.words[i] = wordAligned ? words[sourceIndex] :
		(words[sourceIndex] >>> fromIndex) |
		(words[sourceIndex+1] << -fromIndex);

        // Process the last word
	long lastWordMask = WORD_MASK >>> -toIndex;
        result.words[targetWords - 1] =
	    ((toIndex-1) & BIT_INDEX_MASK) < (fromIndex & BIT_INDEX_MASK)
	    ? /* straddles source words */
	    ((words[sourceIndex] >>> fromIndex) |
	     (words[sourceIndex+1] & lastWordMask) << -fromIndex)
	    :
	    ((words[sourceIndex] & lastWordMask) >>> fromIndex);

        // Set wordsInUse correctly
        result.wordsInUse = targetWords;
        result.recalculateWordsInUse();
	result.checkInvariants();

	return result;
    }

   


    /**
     * Returns the "logical size" of this <code>BitSet</code>: the index of
     * the highest set bit in the <code>BitSet</code> plus one. Returns zero
     * if the <code>BitSet</code> contains no set bits.
     *
     * @return  the logical size of this <code>BitSet</code>.
     * @since   1.2
     */
    public int length() {
        if (wordsInUse == 0)
            return 0;

        return BITS_PER_WORD * (wordsInUse - 1) +
	    (BITS_PER_WORD - Long.numberOfLeadingZeros(words[wordsInUse - 1]));
    }

    /**
     * Returns true if this <code>BitSet</code> contains no bits that are set
     * to <code>true</code>.
     *
     * @return    boolean indicating whether this <code>BitSet</code> is empty.
     * @since     1.4
     */
    public boolean isEmpty() {
        return wordsInUse == 0;
    }

    

    /**
     * Returns the number of bits set to <tt>true</tt> in this
     * <code>BitSet</code>.
     *
     * @return  the number of bits set to <tt>true</tt> in this
     *          <code>BitSet</code>.
     * @since   1.4
     */
    public int cardinality() {
        int sum = 0;
        for (int i = 0; i < wordsInUse; i++)
            sum += Long.bitCount(words[i]);
        return sum;
    }

   

    
  

    /**
     * Returns a hash code value for this bit set. The hash code
     * depends only on which bits have been set within this
     * <code>BitSet</code>. The algorithm used to compute it may
     * be described as follows.<p>
     * Suppose the bits in the <code>BitSet</code> were to be stored
     * in an array of <code>long</code> integers called, say,
     * <code>words</code>, in such a manner that bit <code>k</code> is
     * set in the <code>BitSet</code> (for nonnegative values of
     * <code>k</code>) if and only if the expression
     * <pre>((k&gt;&gt;6) &lt; words.length) && ((words[k&gt;&gt;6] & (1L &lt;&lt; (bit & 0x3F))) != 0)</pre>
     * is true. Then the following definition of the <code>hashCode</code>
     * method would be a correct implementation of the actual algorithm:
     * <pre>
     * public int hashCode() {
     *      long h = 1234;
     *      for (int i = words.length; --i &gt;= 0; ) {
     *           h ^= words[i] * (i + 1);
     *      }
     *      return (int)((h &gt;&gt; 32) ^ h);
     * }</pre>
     * Note that the hash code values change if the set of bits is altered.
     * <p>Overrides the <code>hashCode</code> method of <code>Object</code>.
     *
     * @return  a hash code value for this bit set.
     */
    public int hashCode() {
	long h = 1234;
	for (int i = wordsInUse; --i >= 0; )
            h ^= words[i] * (i + 1);

	return (int)((h >> 32) ^ h);
    }

    /**
     * Returns the number of bits of space actually in use by this
     * <code>BitSet</code> to represent bit values.
     * The maximum element in the set is the size - 1st element.
     *
     * @return  the number of bits currently in this bit set.
     */
    public int size() {
	return words.length * BITS_PER_WORD;
    }

    /**
     * Compares this object against the specified object.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and is a <code>Bitset</code> object that has
     * exactly the same set of bits set to <code>true</code> as this bit
     * set. That is, for every nonnegative <code>int</code> index <code>k</code>,
     * <pre>((BitSet)obj).get(k) == this.get(k)</pre>
     * must be true. The current sizes of the two bit sets are not compared.
     * <p>Overrides the <code>equals</code> method of <code>Object</code>.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @see     java.util.BitSet#size()
     */
    public boolean equals(Object obj) {
	if (!(obj instanceof LargeBitSet))
	    return false;
	if (this == obj)
	    return true;

	LargeBitSet set = (LargeBitSet) obj;

	checkInvariants();
	set.checkInvariants();

	if (wordsInUse != set.wordsInUse)
            return false;

	// Check words in use by both BitSets
	for (int i = 0; i < wordsInUse; i++)
	    if (words[i] != set.words[i])
		return false;

	return true;
    }

    /**
     * Cloning this <code>BitSet</code> produces a new <code>BitSet</code>
     * that is equal to it.
     * The clone of the bit set is another bit set that has exactly the
     * same bits set to <code>true</code> as this bit set.
     *
     * <p>Overrides the <code>clone</code> method of <code>Object</code>.
     *
     * @return  a clone of this bit set.
     * @see     java.util.BitSet#size()
     */
    public Object clone() {
	if (! sizeIsSticky)
	    trimToSize();

	try {
		LargeBitSet result = (LargeBitSet) super.clone();
	    result.words = words.clone();
	    result.checkInvariants();
	    return result;
	} catch (CloneNotSupportedException e) {
	    throw new InternalError();
	}
    }

    /**
     * Attempts to reduce internal storage used for the bits in this bit set.
     * Calling this method may, but is not required to, affect the value
     * returned by a subsequent call to the {@link #size()} method.
     */
    private void trimToSize() {
	if (wordsInUse != words.length) {
            words = Arrays.copyOf(words, wordsInUse);
	    checkInvariants();
	}
    }

    /**
     * Save the state of the <tt>BitSet</tt> instance to a stream (i.e.,
     * serialize it).
     */
    private void writeObject(ObjectOutputStream s)
	throws IOException {

	checkInvariants();

	if (! sizeIsSticky)
	    trimToSize();

	ObjectOutputStream.PutField fields = s.putFields();
	fields.put("bits", words);
	s.writeFields();
    }

    /**
     * Reconstitute the <tt>BitSet</tt> instance from a stream (i.e.,
     * deserialize it).
     */
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {

	ObjectInputStream.GetField fields = s.readFields();
	words = (long[]) fields.get("bits", null);

        // Assume maximum length then find real length
        // because recalculateWordsInUse assumes maintenance
        // or reduction in logical size
        wordsInUse = words.length;
        recalculateWordsInUse();
	sizeIsSticky = (words.length > 0 && words[words.length-1] == 0L); // heuristic
	checkInvariants();
    }

    
}
