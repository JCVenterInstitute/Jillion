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
package org.jcvi.jillion.core.residue.nt;

import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;

/**
 * {@code NucleotideSequenceBuilder}  is a way to
 * construct a {@link NucleotideSequence}
 * similar to how a {@link StringBuilder} can be used
 * to create a String.  The contents of the NucleotideSequence
 * can be changed by method calls.  This class
 * is not thread safe.
 * @author dkatzel
 *
 *
 */
public final class NucleotideSequenceBuilder implements ResidueSequenceBuilder<Nucleotide,NucleotideSequence>{
    private static final byte GAP_VALUE = Nucleotide.Gap.getOrdinalAsByte();
    private static final byte N_VALUE = Nucleotide.Unknown.getOrdinalAsByte();
    private static final byte A_VALUE = Nucleotide.Adenine.getOrdinalAsByte();
    private static final byte C_VALUE = Nucleotide.Cytosine.getOrdinalAsByte();
    private static final byte G_VALUE = Nucleotide.Guanine.getOrdinalAsByte();
    private static final byte T_VALUE = Nucleotide.Thymine.getOrdinalAsByte();
    /**
     * We store the current values of our sequence as bits in
     * a {@link BitSet}.  This allows us to put multiple nucleotides
     * inside a single byte.
     */
    private BitSet bits;
    /**
     * The CodecDecider will keep track of what types of
     * bases we have and how many in order to decide
     * the best codec to use when we
     * build our immutable NucleotideSequence
     * via  {@link #build()}.
     */
    private CodecDecider codecDecider;
    /**
     * Points to the next bit that will
     * be set if we append to our {@link BitSet}.
     * This also acts as the value of our length of bits written.
     * We need to keep track of this ourselves 
     * since {@link BitSet} automatically
     * grows and provides lots of padding to improve
     * I/O performance, and doesn't keep track
     * of the actual number of bits written so far.
     */
    private int tail=0;
    /**
     * Currently we can store each base
     * in {@value} bits.
     */
    private static final int NUM_BITS_PER_VALUE=4;
    /**
     * Cache of the nucleotides in ordinal order
     * for quick lookups.
     */
    private static Nucleotide[] NUCLEOTIDE_VALUES = Nucleotide.values();
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     */
    public NucleotideSequenceBuilder(){
        bits = new BitSet();
        codecDecider = new CodecDecider();
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     * @param initialCapacity the initial capacity 
     * of the array backing the {@link NucleotideSequence}
     * (will be grown if sequence gets too large)
     * @throws IllegalArgumentException if initialCapacity < 1.
     */
    public NucleotideSequenceBuilder(int initialCapacity){
        if(initialCapacity<1){
            throw new IllegalArgumentException("initial capacity must be >=1");
        }
        bits = new BitSet(initialCapacity*NUM_BITS_PER_VALUE);
        codecDecider = new CodecDecider();
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder(Iterable<Nucleotide> sequence){
        assertNotNull(sequence);
        NewValues newValues = new NewValues(sequence);
        this.bits = newValues.getBits();
        codecDecider = new CodecDecider(newValues);
        this.tail = newValues.getLength()*NUM_BITS_PER_VALUE;
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     *  Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link Nucleotide}.
     */
    public NucleotideSequenceBuilder(String sequence){
    	if(sequence ==null){
    		throw new NullPointerException("sequence can not be null");
    	}
    	NewValues newValues = new NewValues(sequence);
        this.bits = newValues.getBits();
        codecDecider = new CodecDecider(newValues);
        this.tail = newValues.getLength()*NUM_BITS_PER_VALUE;  
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given single nucleotide.
     * @param singleNucleotide the initial nucleotide sequence.
     * @throws NullPointerException if singleNucleotide is null.
     */
    public NucleotideSequenceBuilder(Nucleotide singleNucleotide){
    	if(singleNucleotide ==null){
    		throw new NullPointerException("singleNucleotide can not be null");
    	}
    	NewValues newValues = new NewValues(singleNucleotide);
        this.bits = newValues.getBits();
        codecDecider = new CodecDecider(newValues);
        this.tail = newValues.getLength()*NUM_BITS_PER_VALUE;  
    }

    
    
    private NucleotideSequenceBuilder(BitSet subBits, int numberOfBitsUsed) {
    	NewValues newValues = new NewValues(subBits,numberOfBitsUsed);
        this.bits = newValues.getBits();
        this.codecDecider = new CodecDecider(newValues);
        this.tail = numberOfBitsUsed;
	}
    private NucleotideSequenceBuilder(BitSet subBits, int numberOfBitsUsed, CodecDecider codecDecider) {
    	NewValues newValues = new NewValues(subBits,numberOfBitsUsed);
        this.bits = newValues.getBits();
        this.codecDecider = codecDecider.copy();
        this.tail = numberOfBitsUsed;
	}
	/**
     * Appends the given base to the end
     * of the builder's mutable sequence.
     * @param base a single nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if base is null.
     */
    public NucleotideSequenceBuilder append(Nucleotide base){
        if(base==null){
            throw new NullPointerException("base can not be null");
        }
        return append(Collections.singleton(base));
    }
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(Iterable<Nucleotide> sequence){
        assertNotNull(sequence);
        NewValues newValues = new NewValues(sequence);
        return append(newValues);
    }
	private NucleotideSequenceBuilder append(NewValues newValues) {
		BitSet newBits = newValues.getBits();
        int length = newBits.length();
		for(int i=0; i<length; i++){
        	if(newBits.get(i)){
        		bits.set(tail+i);
        	}
        }
        tail += newValues.getLength()*NUM_BITS_PER_VALUE;
        this.codecDecider.increment(newValues);
        return this;
	}
    
    /**
     * Appends the current contents of the given {@link NucleotideSequenceBuilder} to the end
     * of the builder's mutable sequence.  Any further modifications to the passed in builder
     * will not be reflected in this builder.  This is an equivalent but more efficient way operation
     * as {@code this.append(otherBuilder.build())}
     * 
     * @param otherBuilder the {@link NucleotideSequenceBuilder} whose current
     * nucleotides are to be appended.
     * @throws NullPointerException if otherBuilder is null.
     * @throws IllegalArgumentException if otherBuilder is not a NucleotideSequenceBuilder.
     */
    public NucleotideSequenceBuilder append(NucleotideSequenceBuilder otherBuilder){
        
    	assertNotNull(otherBuilder);    	
    	NewValues newValues = new NewValues(otherBuilder.bits, otherBuilder.tail);
        return append(newValues);
    }
   
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(String sequence){
    	if(sequence ==null){
    		throw new NullPointerException("sequence can not be null");
    	}
        return append(new NewValues(sequence));
    }
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    public NucleotideSequenceBuilder insert(int offset, String sequence){
    	 assertInsertionParametersValid(offset, sequence);
    	return insert(offset, new NewValues(sequence));
    }
    private void assertNotNull(Object sequence) {
        if(sequence ==null){
            throw new NullPointerException("sequence can not be null");
        }
    }
    /**
     * Get the current length of the mutable
     * sequence. 
     * @return the current length
     * of the nucleotide sequence.
     */
    public long getLength(){
        return codecDecider.getCurrentLength();
    }
    
    @Override
	public long getUngappedLength() {
		return codecDecider.getCurrentLength() - codecDecider.numberOfGaps;
	}
    /**
     * Replace the Nucleotide at the given offset with a different nucleotide.
     * @param offset the gapped offset to modify.
     * @param replacement the new {@link Nucleotide} to replace the old
     * {@link Nucleotide} at that location.
     * @return this
     * @throws NullPointerException if replacement is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    public NucleotideSequenceBuilder replace(int offset, Nucleotide replacement){
    	int length = tail/NUM_BITS_PER_VALUE;
        if(offset <0 || offset >= length){
            throw new IllegalArgumentException(
                    String.format("offset %d out of range (length = %d)",length,offset));
        }
        if(replacement ==null){
            throw new NullPointerException("replacement base can not be null");
        }
        return privateReplace(offset, replacement);
    }
    /**
     * Method that actually performs the replace which assumes all
     * of the input values are valid.
     * @param offset
     * @param replacement
     * @return
     */
	private NucleotideSequenceBuilder privateReplace(int offset,
			Nucleotide replacement) {
		byte value = (byte)replacement.ordinal();
        int bitStartOffset = offset*NUM_BITS_PER_VALUE;
        int bitEndOffset = bitStartOffset+NUM_BITS_PER_VALUE;
		BitSet subBits = bits.get(bitStartOffset, bitEndOffset);
		final byte oldValue = getNucleotideOrdinalFor(subBits, 0);
		
		
        codecDecider.replace(oldValue, value);
        bits.clear(bitStartOffset, bitEndOffset);
        NewValues newValues = new NewValues(replacement);
        BitSet newBits = newValues.getBits();
        for(int i=0; i< NUM_BITS_PER_VALUE; i++){
        	 if(newBits.get(i)){
             	bits.set(bitStartOffset+i);
             }
        }      
        return this;
	}
    /**
     * Deletes the nucleotides from the given range of this 
     * partially constructed NucleotideSequence.  If the given
     * range is empty, then the nucleotideSequence will not
     * be modified. If the range extends beyond the currently
     * built sequence, then this will delete until the end of
     * the sequence.
     * @param range the range to delete can not be null.
     * @return this.
     * @throws NullPointerException if range is null.
     * @throws IllegalArgumentException if range's start is negative
     * or greater than this nucleotide sequence's current length.
     */
    public NucleotideSequenceBuilder delete(Range range){
        if(range ==null){
            throw new NullPointerException("range can not be null");
        }
        if(!range.isEmpty()){
            Range bitRange = convertBaseRangeIntoBitRange(range);
            int numberOfDeletedBits = (int)bitRange.getLength();
			BitSet subBits = bits.get((int)bitRange.getBegin(), (int)bitRange.getEnd()+1);
			NewValues newValues = new NewValues(subBits, numberOfDeletedBits);
            delete(bitRange, numberOfDeletedBits, newValues);
              
        }
        return this;
    }
	private Range convertBaseRangeIntoBitRange(Range range) {
		int start = (int)range.getBegin();
		assertStartCoordinateIsValid(start);   
		int bitOffsetOfStart = start*NUM_BITS_PER_VALUE;
		int maxEnd = Math.min((tail-1)/NUM_BITS_PER_VALUE, (int)range.getEnd());
		int bitOffsetOfEnd = maxEnd * NUM_BITS_PER_VALUE+3;
		
		return Range.of(bitOffsetOfStart,bitOffsetOfEnd);
	}
	private void assertStartCoordinateIsValid(int start) {
		if(start<0){
		    throw new IllegalArgumentException("range can not have negatives coordinates: "+ start);
		}
		if(start> getLength()){
		    throw new IllegalArgumentException(
		            String.format("range can not start beyond current length (%d) : %d", getLength(),start));
		}
	}
	private void delete(Range bitRange,
			int numberOfDeletedBits, NewValues newValues) {
		BitSet shrunkBits = new BitSet(tail-numberOfDeletedBits);
		int bitOffsetOfStart = (int) bitRange.getBegin();
		for(int i=0; i<bitOffsetOfStart; i++){
			if(bits.get(i)){
				shrunkBits.set(i);
			}
		}
		for(int i=(int)bitRange.getEnd()+1, j=0; i<tail; i++, j++){
			if(bits.get(i)){
				shrunkBits.set(bitOffsetOfStart + j);
			}
		}
		
		this.codecDecider.decrement(newValues);
		tail -= numberOfDeletedBits;
		this.bits = shrunkBits;
	}
    
    @Override
	public Nucleotide get(int offset) {
    	if(offset<0){
            throw new IllegalArgumentException("offset can not have negatives coordinates: "+ offset);
        }
        if(offset> getLength()){
            throw new IllegalArgumentException(
                    String.format("offset can not start beyond current length (%d) : %d", getLength(),offset));
        }
		return NUCLEOTIDE_VALUES[getNucleotideOrdinalFor(bits,offset)];
	}
	public int getNumGaps(){
        return codecDecider.getNumberOfGaps();
    }
    
    public int getNumNs(){
        return codecDecider.getNumberOfNs();
    }
    public int getNumAmbiguities(){
        return codecDecider.getNumberOfAmbiguities();
    }
    
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, String) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, String)
     */
    public NucleotideSequenceBuilder prepend(String sequence){
        return insert(0, sequence);
    }
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * @param offset the <strong>gapped</strong> offset into this mutable sequence
     * to begin insertion.  If the offset = the current length then this insertion
     * is treated as an append.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    public NucleotideSequenceBuilder insert(int offset, Iterable<Nucleotide> sequence){
        assertInsertionParametersValid(offset, sequence);   
        NewValues newValues = new NewValues(sequence);
        return insert(offset, newValues);
    }
	private void assertInsertionParametersValid(int offset,
			Object sequence) {
		assertNotNull(sequence);
        if(offset<0){
            throw new IllegalArgumentException("offset can not have negatives coordinates: "+ offset);
        }
        if(offset> getLength()){
            throw new IllegalArgumentException(
                    String.format("offset can not start beyond current length (%d) : %d", getLength(),offset));
        }
	}
	private NucleotideSequenceBuilder insert(int offset, NewValues newValues) {
		BitSet insertedBits = newValues.getBits();
        int numberOfInsertedBits = newValues.getLength()*NUM_BITS_PER_VALUE;
		BitSet expandedBits = new BitSet(tail+numberOfInsertedBits);
        int bitValueOfOffset = offset*NUM_BITS_PER_VALUE;
        for(int i=0; i< bitValueOfOffset; i++){
        	if(bits.get(i)){
        		expandedBits.set(i);
        	}
        }
        for(int i=0; i< numberOfInsertedBits; i++ ){
        	if(insertedBits.get(i)){
        		expandedBits.set(bitValueOfOffset+i);
        	}
        }
        for(int i=bitValueOfOffset; i< tail; i++){
        	if(bits.get(i)){
        		expandedBits.set(i+numberOfInsertedBits);
        	}
        }
        this.codecDecider.increment(newValues);
        tail +=numberOfInsertedBits;
        this.bits = expandedBits;
        return this;
	}
    /**
     * Inserts the contents of the given other  {@link NucleotideSequenceBuilder}
     *  into this builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * Any further modifications to the passed in builder
     * will not be reflected in this builder.  This is an equivalent but more efficient operation
     * as {@code this.insert(offset, otherBuilder.build())}
     * 
     * @param offset the <strong>gapped</strong> offset into this mutable sequence
     * to begin insertion.
     * @param otherBuilder the {@link NucleotideSequenceBuilder} whose current
     * nucleotides are to be inserted at the given offset.
     * @return this
     * @throws NullPointerException if otherBuilder is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length or if otherBuilder is not a NucleotideSequenceBuilder.
     */
    public NucleotideSequenceBuilder insert(int offset, ResidueSequenceBuilder<Nucleotide, NucleotideSequence> otherBuilder){
        assertNotNull(otherBuilder);
        if(!(otherBuilder instanceof NucleotideSequenceBuilder)){
        	throw new IllegalArgumentException("otherBuilder must be a NucleotideSequenceBuilder");
        }
        if(offset<0){
            throw new IllegalArgumentException("offset can not have negatives coordinates: "+ offset);
        }
        if(offset>= getLength()){
            throw new IllegalArgumentException(
                    String.format("offset can not start beyond current length (%d) : %d", getLength(),offset));
        }   
        NucleotideSequenceBuilder otherSequenceBuilder = (NucleotideSequenceBuilder)otherBuilder;
        NewValues newValues = new NewValues(otherSequenceBuilder.bits, otherSequenceBuilder.tail);
        return insert(offset, newValues);
    }
    
   
    
    /**
     * Inserts the given {@link Nucleotide} to the builder's mutable sequence
     * at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by 1
     * base.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param base the {@link Nucleotide} to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if base is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    public NucleotideSequenceBuilder insert(int offset, Nucleotide base){
    	if(base ==null){
    		throw new NullPointerException("base can not be null");
    	}
        return insert(offset, Collections.singleton(base));
     }
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, Iterable) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, Iterable)
     */
    public NucleotideSequenceBuilder prepend(Iterable<Nucleotide> sequence){
        return insert(0, sequence);
    }
    
    /**
     * Inserts the current contents of the given {@link NucleotideSequenceBuilder}
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, ResidueSequenceBuilder) insert(0,otherBuilder)}
     * @param otherBuilder {@link NucleotideSequenceBuilder} whose current
     * nucleotides are to be inserted at the beginning.
     * @return this.
     * @throws NullPointerException if otherBuilder is null.
     * @see #insert(int, ResidueSequenceBuilder)
     */
    public NucleotideSequenceBuilder prepend(ResidueSequenceBuilder<Nucleotide, NucleotideSequence> otherBuilder){
        return insert(0, otherBuilder);
    }
    /**
    * {@inheritDoc}
    * <p>
    * Create a new {@link NucleotideSequence} instance
    * from the current mutable nucleotides.  This method
    * does not destroy any temp data so this method
    * could be called multiple times each time 
    * creating a new {@link NucleotideSequence}.
    * @return a new NucleotideSequence never null
    * but may be empty.
    */
    @Override
    public NucleotideSequence build() {    
    		
        	if(codecDecider.hasAlignedReference()){
        		return new DefaultReferenceEncodedNucleotideSequence(
        				codecDecider.alignedReference.reference, this, codecDecider.alignedReference.offset);
        	
        	}
        	
        	NucleotideCodec optimalCodec = codecDecider.getOptimalCodec();
        	byte[] encodedBytes =optimalCodec.encode(codecDecider.currentLength, iterator());
        	NucleotideSequence seq= new DefaultNucleotideSequence(optimalCodec, encodedBytes);
        	
        	return seq;

    }
    @Override
    public Iterator<Nucleotide> iterator() {
    	return new Iterator<Nucleotide>(){
            private final int end = codecDecider.currentLength*NUM_BITS_PER_VALUE-1;
            private int currentOffset=0;
            private final BitSet bits = NucleotideSequenceBuilder.this.bits.get(0,tail);
			@Override
			public boolean hasNext() {
				return currentOffset<end;
			}
			@Override
			public Nucleotide next() {
				if(!hasNext()){
					throw new NoSuchElementException();
				}
				Nucleotide next = NUCLEOTIDE_VALUES[getNucleotideOrdinalFor(bits,currentOffset)];
				currentOffset+= NUM_BITS_PER_VALUE;
				return next;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
				
			}
    	};
    	
	}
	/**
     * Return the built {@link NucleotideSequence} as {@link ReferenceMappedNucleotideSequence} 
     * assuming {@link #setReferenceHint(NucleotideSequence, int)} has been set.
     * This is the same as {@code (ReferenceEncodedNucleotideSequence) build()}
     * @return the built NucleotideSequence as a {@link ReferenceMappedNucleotideSequence}.
     * @throws IllegalStateException if a reference
     * has not been provided via the {@link #setReferenceHint(NucleotideSequence, int)}
     */
    public ReferenceMappedNucleotideSequence buildReferenceEncodedNucleotideSequence() {    
    	if(!codecDecider.hasAlignedReference()){
    		throw new IllegalStateException("must provide reference");
    	}
        return (ReferenceMappedNucleotideSequence)build();
    }
   
    /**
     * Provide another {@link NucleotideSequence} and a start coordinate
     * that can be used as a reference alignment for this sequence to be built.
     * This information may or may not be actually used during {@link #build()}
     *  to construct a more memory efficient
     * {@link NucleotideSequence} implementation.  The given sequence and start coordinate
     * provided should be the coordinates used in the final fully built sequence.
     * <br/>
     * For example:
     * <pre>
     * 
     * NucleotideSequence reference = ... //reference = A-GCCGTT
     * 
     *  new NucleotideSequenceBuilder("CGGC")
     *  		.setReference(reference, 2)
                .reverseCompliment()
                .append("N");     
     * </pre>
     * might use the part of the reference "GCCGT"
     * that aligns to this sequence being built with only one SNP (T ->N )
     * to save memory. 
     * 
     * @param referenceSequence the reference sequence 
     * that aligns well to this sequence and that may be used
     * to improve memory performance.  A reference
     * can be a contig or scaffold consensus or anything else
     * that will have a high percent identity for the length 
     * of this sequence being built. This sequence and the reference
     * sequence must be in the same orientation to align well.  Can not be null.
     * @param gappedStartOffset the <strong>gapped</strong> offset into
     * this reference where the final version of this built sequence will
     * start to align. Can not be negative or start beyond
     * the length of this reference.
     * @return this.
     * @throws NullPointerException if referenceSequence is null.
     * @throws IllegalArgumentException if gappedStartOffset is <0 or beyond the reference.
     */
    public NucleotideSequenceBuilder setReferenceHint(NucleotideSequence referenceSequence, int gappedStartOffset){
    	codecDecider.alignedReference(new AlignedReference(referenceSequence, gappedStartOffset));
    	return this;
    }
    /**
     * Create a new NucleotideSequence instance
     * from containing only current mutable nucleotides
     * in the given range.  If the range extends beyond the current
     * sequence, then this will build all the bases until the end of
     * the sequence.
     * @param range the range of nucleotides to build (gapped).
     * @return a new NucleotideSequence never null
     * but may be empty.
     */
    public NucleotideSequence build(Range range) {
    	Range bitRange = convertBaseRangeIntoBitRange(range);
        int numberOfDeletedBits = (int)bitRange.getLength()-1;
		BitSet subBits = bits.get((int)bitRange.getBegin(), (int)bitRange.getEnd()+1);
		NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(subBits,numberOfDeletedBits);
		if(codecDecider.hasAlignedReference()){
			builder.setReferenceHint(codecDecider.alignedReference.reference, codecDecider.alignedReference.offset+ (int)range.getBegin());
		}
		return builder.build();
    }
    
    /**
     * {@inheritDoc}
     * 
     * If a previous reference hint is provided
     * via {@link #setReferenceHint(NucleotideSequence, int)},
     * then the reference coordinates will be automatically
     * adjusted to compensate for the new trimmed sequence.
     * 
     * @param range the range of nucleotides to keep (gapped).
     * @return this.
     */
    @Override
    public NucleotideSequenceBuilder trim(Range range){
    	if(range.getEnd() <0 || range.isEmpty()){
    		return delete(Range.ofLength(this.getLength()));
    	}
    	Range trimRange = range.intersection(Range.ofLength(getLength()));
    	
    	Range bitRange = convertBaseRangeIntoBitRange(trimRange);
        int numberOfBitsUsed = (int)bitRange.getLength();
		BitSet subBits = bits.get((int)bitRange.getBegin(), (int)bitRange.getEnd()+1);
		NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(subBits,numberOfBitsUsed);
		if(codecDecider.hasAlignedReference()){
			builder.setReferenceHint(codecDecider.alignedReference.reference, codecDecider.alignedReference.offset+ (int)range.getBegin());
		}
		this.bits = subBits;
        this.codecDecider = builder.codecDecider;
        this.tail = numberOfBitsUsed;
		return this;
    }
   
    
   
	private Nucleotide getNucleotideFor(int bitStartOffset) {
		int ordinal = getNucleotideOrdinalFor(bitStartOffset);
		return NUCLEOTIDE_VALUES[ordinal];
	}
	private byte getNucleotideOrdinalFor(BitSet bits, int bitStartOffset) {
		
		int bit3 =bits.get(bitStartOffset)?8:0; 
		int bit2 =bits.get(bitStartOffset+1)?4:0; 
		int bit1 =bits.get(bitStartOffset+2)?2:0; 
		int bit0 =bits.get(bitStartOffset+3)?1:0;
		return (byte)(bit3+bit2+bit1+bit0);
	}
	private byte getNucleotideOrdinalFor(int bitStartOffset) {
		return getNucleotideOrdinalFor(bits, bitStartOffset);
	}
    
	/**
	 * 
	 * {@inheritDoc}
	 */
	public NucleotideSequenceBuilder copy(){
		BitSet copyOfBits = bits.get(0,tail);		
		return new NucleotideSequenceBuilder(copyOfBits, tail,codecDecider);
	}
    
   
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bits.get(0, tail).hashCode();
		return result;
	}
	/**
	 * Two {@link NucleotideSequenceBuilder}s are equal
	 * if they currently both contain
	 * the exact same Nucleotide sequence.
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NucleotideSequenceBuilder)) {
			return false;
		}
		NucleotideSequenceBuilder other = (NucleotideSequenceBuilder) obj;
		if(tail !=other.tail){
			return false;
		}
		BitSet ourPopulatedBits =bits.get(0, tail);
		BitSet otherPopulatedBits =other.bits.get(0, other.tail);
		if (!ourPopulatedBits.equals(otherPopulatedBits)) {
			return false;
		}
		return true;
	}
	
	
	/**
     * Get the current Nucleotides sequence as 
     * one long String without any whitespace.
     * For example:
     * <pre>
     *  new NucleotideSequenceBuilder("ACGT")
     *  .append("-TAG")
     *  .toString();
     * </pre>
     * will return "ACGT-TAG".
     */
    @Override
    public String toString(){
    	StringBuilder builder = new StringBuilder(codecDecider.getCurrentLength());
    	for(int i=0; i<tail; i+=NUM_BITS_PER_VALUE){
        	Nucleotide base = getNucleotideFor(i);
        	builder.append(base);
        }
        return builder.toString();
    }
    /**
     * Reverse complement all the nucleotides currently in this builder.
     * Calling this method will only reverse complement bases that 
     * already exist in this builder; any additional operations
     * to insert bases will not be affected.
     * <p/>
     * For example:
     * <pre>
     *      new NucleotideSequenceBuilder("CGGC")
                .reverseComplement()
                .append("N");                
     * </pre>
     * will generate a Sequence "GCCGN".
     * @return this.
     */
    public NucleotideSequenceBuilder reverseComplement(){
        int currentLength = codecDecider.getCurrentLength();
        int pivotOffset = currentLength/2;
        for(int i=0; i<pivotOffset; i++){
            int compOffset = currentLength-1-i;
            int startBitOfI = i*NUM_BITS_PER_VALUE;
            Nucleotide tmp = getNucleotideFor(startBitOfI).complement();
            int startBitOfComplementOffset = compOffset*NUM_BITS_PER_VALUE;
            byte complementOrdinal = (byte) getNucleotideFor(startBitOfComplementOffset).complement().ordinal();
            setBitsFor(startBitOfI, complementOrdinal);
            setBitsFor(startBitOfComplementOffset, (byte) tmp.ordinal());
        }
        if(currentLength%2!=0){
        	int bitOffset = pivotOffset*NUM_BITS_PER_VALUE;
        	byte complementOrdinal = (byte) getNucleotideFor(bitOffset).complement().ordinal();
        	setBitsFor(bitOffset, complementOrdinal);
        }
        return this;
    }
    /**
     * Complements all the nucleotides currently in this builder
     * but does not reverse the sequence.
     * Calling this method will only complement bases that 
     * already exist in this builder; any additional operations
     * to insert bases will not be affected.
     * <p/>
     * For example:
     * <pre>
     *      new NucleotideSequenceBuilder("ATGT")
                .compliment()
                .append("N");                
     * </pre>
     * will generate a Sequence "TACAN".
     * @return this.
     */
    public NucleotideSequenceBuilder complement(){
        int currentLength = codecDecider.getCurrentLength();
        for(int i=0; i<currentLength; i++){
            int startBitOfI = i*NUM_BITS_PER_VALUE;
            Nucleotide complement = getNucleotideFor(startBitOfI).complement();
            byte complementOrdinal = (byte) complement.ordinal();
            setBitsFor(startBitOfI, complementOrdinal);
        }
        return this;
    }
    
    
	private void setBitsFor(int offset, byte twoBitValue) {
		setBitsFor(bits, offset, twoBitValue);
	}
	
	private void setBitsFor(BitSet bits, int offset, byte fourBitValue) {
		bits.clear(offset, offset+NUM_BITS_PER_VALUE);
		if((fourBitValue & 0x8 ) !=0){
			bits.set(offset);            	
		}
		if((fourBitValue & 0x4 ) !=0){
			bits.set(offset+1);            	
		}
		if((fourBitValue & 0x2 ) !=0){
			bits.set(offset+2);            	
		}
		if((fourBitValue & 0x1 ) !=0){
			bits.set(offset+3);            	
		}
	}
    
    /**
     * {@inheritDoc}
     * 
     * @see #reverseComplement()
     */
    @Override
	public NucleotideSequenceBuilder reverse() {
    	int currentLength = codecDecider.getCurrentLength();
        int pivotOffset = currentLength/2;
        for(int i=0; i<pivotOffset; i++){
            int jOffset = currentLength-1-i;
            int startBitOfI = i*NUM_BITS_PER_VALUE;
            byte ordinalOfI = getNucleotideFor(startBitOfI).getOrdinalAsByte();
            int startBitOfJ = jOffset*NUM_BITS_PER_VALUE;
            byte ordinalOfJ = getNucleotideFor(startBitOfJ).getOrdinalAsByte();
            setBitsFor(startBitOfI, ordinalOfJ);
            setBitsFor(startBitOfJ, ordinalOfI);
        }
		return this;
	}
	/**
     * Remove all gaps currently present in this builder.
     * @return this.
     */
    public NucleotideSequenceBuilder ungap(){
        final int numGaps = codecDecider.getNumberOfGaps();
        if(numGaps>0){
        	BitSet newBits = new BitSet(tail);
            int newOffset=0;
            for(int oldOffset=0; oldOffset<tail; oldOffset+=NUM_BITS_PER_VALUE){
            	byte ordinal = getNucleotideOrdinalFor(oldOffset);
            	if(ordinal !=GAP_VALUE){
            		setBitsFor(newBits, newOffset, ordinal);
            		newOffset+=NUM_BITS_PER_VALUE;
            	}                
            }
           bits = newBits;
           tail = newOffset;
           codecDecider.ungap();
        }
        return this;
    }

    /**
     * This class keeps track of the number of special
     * nucleotides (gaps, N, ambiguities etc)
     * that we have so far in our sequence.
     * We can use this information during
     * {@link #build()} to determine the best
     * {@link NucleotideCodec} to use.
     * @author dkatzel
     */
    private static final class CodecDecider{
        private int numberOfGaps=0;
        private int numberOfAmbiguities=0;
        private int numberOfNs=0;
        private int currentLength=0;
        private AlignedReference alignedReference=null;
        CodecDecider(){
        	//needs to be initialized
        }
        CodecDecider(NewValues newValues){
        	increment(newValues);
        }
        CodecDecider copy(){
        	CodecDecider copy = new CodecDecider();
        	copy.numberOfAmbiguities = numberOfAmbiguities;
        	copy.numberOfGaps = numberOfGaps;
        	copy.currentLength= currentLength;
        	copy.numberOfNs = numberOfNs;
        	copy.alignedReference = alignedReference;
        	return copy;
        	
        }
        
        
        void alignedReference(AlignedReference ref){
        	this.alignedReference = ref;
        }
        
        boolean hasAlignedReference(){
        	return alignedReference!=null;
        }
        NucleotideCodec getOptimalCodec() {
        	
            if(numberOfAmbiguities>0 || (numberOfGaps>0 && numberOfNs >0)){
                return DefaultNucleotideCodec.INSTANCE;
            }
            int fourBitBufferSize =currentLength/2;
            int twoBitBufferSize = TwoBitEncodedNucleotideCodec.getNumberOfEncodedBytesFor(currentLength,
            		Math.max(numberOfGaps, numberOfNs));
            if(fourBitBufferSize < twoBitBufferSize){
            	return DefaultNucleotideCodec.INSTANCE;
            }
            if(numberOfGaps==0 ){
            	return ACGTNNucloetideCodec.INSTANCE;
            }
            return NoAmbiguitiesEncodedNucleotideCodec.INSTANCE;
        }
        
        public void increment(NewValues newValues) {
			numberOfGaps +=newValues.getNumberOfGaps();
			numberOfNs += newValues.getNumberOfNs();
			currentLength += newValues.getLength();
			numberOfAmbiguities += newValues.getnumberOfAmiguities();
		}
        
        public void decrement(NewValues newValues) {
			numberOfGaps -=newValues.getNumberOfGaps();
			numberOfNs -= newValues.getNumberOfNs();
			currentLength -= newValues.getLength();
			numberOfAmbiguities -= newValues.getnumberOfAmiguities();
		}

		
        public void replace(byte oldValue, byte newValue) {
            handleValue(oldValue,false);
            handleValue(newValue,true);
        }

       
        
        void handleValue(int value, boolean increment) {
            if(value == GAP_VALUE){
                handleGap(increment);
            }else if(value == N_VALUE){
                handleN(increment);
            }else if(value != A_VALUE && value != C_VALUE 
                    && value != G_VALUE && value != T_VALUE){
                handleAmbiguity(increment);                
            }
        }

        private void handleAmbiguity(boolean increment) {
            if(increment){
                numberOfAmbiguities++;
            }else{
                numberOfAmbiguities--;
            }
        }

        private void handleN(boolean increment) {
            if(increment){
            numberOfNs++;
            }else{
                numberOfNs--;
            }
        }

        private void handleGap(boolean increment) {
            if(increment){
                numberOfGaps++;
            }else{
                numberOfGaps--;
            }
        }
        
        
        void ungap(){
            currentLength-=numberOfGaps;
            numberOfGaps=0;
            
        }
        /**
         * @return the numberOfGaps
         */
        int getNumberOfGaps() {
            return numberOfGaps;
        }
        /**
         * @return the numberOfAmbiguities
         */
        int getNumberOfAmbiguities() {
            return numberOfAmbiguities;
        }
        /**
         * @return the numberOfNs
         */
        int getNumberOfNs() {
            return numberOfNs;
        }
        /**
         * @return the currentLength
         */
        int getCurrentLength() {
            return currentLength;
        }
        
        
    }
    
    
    private static class AlignedReference{
    	private final NucleotideSequence reference;
    	private final int offset;
		public AlignedReference(NucleotideSequence reference, int offset) {
			long length = reference.getLength();
			if(offset > length){
				throw new IllegalArgumentException(
						String.format("invalid offset %d is beyond reference length %d", offset, length));
			}
			this.reference = reference;
			this.offset = offset;
		}
    	
    }
    
    private class NewValues{
    	private final  BitSet bits;
    	private int length;
    	private int numberOfGaps;
    	private int numberOfACGTs;
    	private int numberOfNs;
    	
    	
    	public NewValues(BitSet encodedBits, int numberOfBitsUsed){
    		for(int i=0; i< numberOfBitsUsed; i+=NUM_BITS_PER_VALUE){
    			handleOrdinal(getNucleotideOrdinalFor(encodedBits,i));    			
    		}
    		this.bits = encodedBits;
    	}
    	public NewValues(Nucleotide nucleotide){
    		bits = new BitSet();
            handle(nucleotide, 0);
    	}
    	public NewValues(String sequence){
    		bits = new BitSet();
            int offset=0;
    		for(int i=0; i<sequence.length(); i++){
    			char c = sequence.charAt(i);
    			if(!Character.isWhitespace(c)){
    				Nucleotide n = Nucleotide.parse(c);
    				handle(n, offset);
                	offset+=NUM_BITS_PER_VALUE;
    			}
    		}
    	}
    	public NewValues(Iterable<Nucleotide> nucleotides){
    		bits = new BitSet();
            int offset=0;
            for(Nucleotide n : nucleotides){
            	handle(n, offset);
            	offset+=NUM_BITS_PER_VALUE;            	
            }
    	}
		private void handle(Nucleotide n, int offset) {
			byte value=n.getOrdinalAsByte();
			
			if((value & 0x1) !=0){
				bits.set(offset+3);
			}
			if((value & 0x2) !=0){
				bits.set(offset+2);
			}
			if((value & 0x4) !=0){
				bits.set(offset+1);
			}
			if((value & 0x8) !=0){
				bits.set(offset);
			}
			handleOrdinal(value);
		}

		private void handleOrdinal(byte ordinal) {
			length++;
			//order of if statements has been optimized using profiler 
			
			if (ordinal == A_VALUE || ordinal == C_VALUE || ordinal == G_VALUE|| ordinal == T_VALUE){
				numberOfACGTs++;
			}else if(ordinal == GAP_VALUE){
				numberOfGaps++;
			}else if(ordinal ==N_VALUE){
				numberOfNs++;
			}
		}

		public int getnumberOfAmiguities() {
			return length - (numberOfGaps + numberOfNs+ numberOfACGTs);
		}

		public BitSet getBits() {
			return bits;
		}

		public int getLength() {
			return length;
		}

		public int getNumberOfGaps() {
			return numberOfGaps;
		}

		public int getNumberOfNs() {
			return numberOfNs;
		}
    	
    	
    	
    }
}
