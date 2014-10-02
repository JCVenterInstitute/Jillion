/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.core.residue.nt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableIterator;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

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
	/**
	 * Initial buffer size is {@value} which should
	 * be enough for most next-gen reads that are seen.
	 * This should greatly reduce the number of resizes we need to do.
	 */
	private static final int INTITAL_BUFFER_SIZE =200;
	
    private static final String NULL_SEQUENCE_ERROR_MSG = "sequence can not be null";
	private static final byte GAP_VALUE = Nucleotide.Gap.getOrdinalAsByte();
    private static final byte N_VALUE = Nucleotide.Unknown.getOrdinalAsByte();
    private static final byte A_VALUE = Nucleotide.Adenine.getOrdinalAsByte();
    private static final byte C_VALUE = Nucleotide.Cytosine.getOrdinalAsByte();
    private static final byte G_VALUE = Nucleotide.Guanine.getOrdinalAsByte();
    private static final byte T_VALUE = Nucleotide.Thymine.getOrdinalAsByte();
   
    
    private GrowableByteArray data;
    /**
     * The CodecDecider will keep track of what types of
     * bases we have and how many in order to decide
     * the best codec to use when we
     * build our immutable NucleotideSequence
     * via  {@link #build()}.
     */
    private CodecDecider codecDecider;

    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     */
    public NucleotideSequenceBuilder(){
        this(INTITAL_BUFFER_SIZE);
    }
    
    
    @Override
	public NucleotideSequenceBuilder clear() {		
    	data.clear();
		codecDecider.clear();
		return this;
	}


	/**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides 
     * @param initialCapacity the initial capacity 
     * but is expected to be eventually take up
     * the given capacity.
     * of the array backing the {@link NucleotideSequence}
     * (will be grown if sequence gets too large)
     * @throws IllegalArgumentException if initialCapacity < 1.
     */
    public NucleotideSequenceBuilder(int initialCapacity){
        if(initialCapacity<1){
            throw new IllegalArgumentException("initial capacity must be >=1");
        }
        data = new GrowableByteArray(initialCapacity);
        codecDecider = new CodecDecider();
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder(NucleotideSequence sequence){
        assertNotNull(sequence);
        NewValues newValues = new NewValues(sequence);
        this.data = newValues.getData();
        codecDecider = new CodecDecider(newValues);
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
        this.data = newValues.getData();
        codecDecider = new CodecDecider(newValues);
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
		if (sequence == null) {
			throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
		}
		NewValues newValues = new NewValues(sequence);
		this.data = newValues.getData();
		codecDecider = new CodecDecider(newValues);
    }
    
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence as a char[].
     *  Any whitespace or '\0' characters in the input array will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param sequence the initial nucleotide sequence as a character array
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link Nucleotide}.
     */
    public NucleotideSequenceBuilder(char[] sequence){
		if (sequence == null) {
			throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
		}
		NewValues newValues = new NewValues(sequence);
		this.data = newValues.getData();
		codecDecider = new CodecDecider(newValues);
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given single nucleotide.
     * @param singleNucleotide the initial nucleotide sequence.
     * @throws NullPointerException if singleNucleotide is null.
     */
    public NucleotideSequenceBuilder(Nucleotide singleNucleotide){
		if (singleNucleotide == null) {
			throw new NullPointerException("singleNucleotide can not be null");
		}
		NewValues newValues = new NewValues(singleNucleotide);
		this.data = newValues.getData();
		codecDecider = new CodecDecider(newValues);
	}

    
    private NucleotideSequenceBuilder(NucleotideSequenceBuilder copy){    	
        this.data = copy.data.copy();
        this.codecDecider = copy.codecDecider.copy();
    }
    private NucleotideSequenceBuilder(GrowableByteArray data){
    	this.data = data;
    	NewValues newValues = new NewValues(data);
    	this.codecDecider = new CodecDecider(newValues);
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
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(NucleotideSequence sequence){
        assertNotNull(sequence);
        NewValues newValues = new NewValues(sequence);
        return append(newValues);
    }
	private NucleotideSequenceBuilder append(NewValues newValues) {
		//this will force the bitset to 
		//grow to the max new size so we don't keep growing each time
		data.append(newValues.data);		
        this.codecDecider.append(newValues);
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
    	this.data.append(otherBuilder.data);
    	this.codecDecider.append(otherBuilder);
    	return this;
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
    		throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
    	}
        return append(new NewValues(sequence));
    }
    
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder; any '\0' characters are ignored.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(char[] sequence){
    	if(sequence ==null){
    		throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
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
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * Any whitespace or '\0' characters will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    public NucleotideSequenceBuilder insert(int offset, char[] sequence){
    	assertInsertionParametersValid(offset, sequence);
		return insert(offset, new NewValues(sequence));
    }
    
    /**
     * Replace the sequence currently located at the given
     * {@link Range} with the given replacementSequence.
     * 
     * @apiNote This is the same as calling:
     * <pre>
     * 	delete(gappedRangeToBeReplaced);
     * 	insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);
     * </pre>
     * @param gappedRangeToBeReplaced the range of this sequence to be replaced.
     * @param replacementSeq the sequence use in this range.
     */
	public NucleotideSequenceBuilder replace(Range gappedRangeToBeReplaced, NucleotideSequenceBuilder replacementSeq) {
		delete(gappedRangeToBeReplaced);
		insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);	
		return this;
	}
	/**
     * Replace the sequence currently located at the given
     * {@link Range} with the given replacementSequence.
     * 
     * @apiNote This is the same as calling:
     * <pre>
     * 	delete(gappedRangeToBeReplaced);
     * 	insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);
     * </pre>
     * @param gappedRangeToBeReplaced the range of this sequence to be replaced.
     * @param replacementSeq the sequence use in this range.
     */
	public NucleotideSequenceBuilder replace(Range gappedRangeToBeReplaced, char[] replacementSeq) {
		delete(gappedRangeToBeReplaced);
		insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);	
		return this;
	}
	
	/**
     * Replace the sequence currently located at the given
     * {@link Range} with the given replacementSequence.
     * 
     * @apiNote This is the same as calling:
     * <pre>
     * 	delete(gappedRangeToBeReplaced);
     * 	insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);
     * </pre>
     * @param gappedRangeToBeReplaced the range of this sequence to be replaced.
     * @param replacementSeq the sequence use in this range.
     */
	public NucleotideSequenceBuilder replace(Range gappedRangeToBeReplaced, String replacementSeq) {
		delete(gappedRangeToBeReplaced);
		insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);	
		return this;
	}
	
	/**
     * Replace the sequence currently located at the given
     * {@link Range} with the given replacementSequence.
     * 
     * @apiNote This is the same as calling:
     * <pre>
     * 	delete(gappedRangeToBeReplaced);
     * 	insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);
     * </pre>
     * @param gappedRangeToBeReplaced the range of this sequence to be replaced.
     * @param replacementSeq the sequence use in this range.
     */
	public NucleotideSequenceBuilder replace(Range gappedRangeToBeReplaced, NucleotideSequence replacementSeq) {
		delete(gappedRangeToBeReplaced);
		insert((int)gappedRangeToBeReplaced.getBegin(), replacementSeq);	
		return this;
	}
    
    private void assertNotNull(Object sequence) {
        if(sequence ==null){
            throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
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
		return codecDecider.getCurrentLength() - codecDecider.getNumberOfGaps();
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
    	
        if(offset <0 || offset >= data.getCurrentLength()){
            throw new IllegalArgumentException(
                    String.format("offset %d out of range (length = %d)",data.getCurrentLength(),offset));
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
		final byte oldValue = data.get(offset);
		
		
        codecDecider.replace(offset, oldValue, value);
        data.replace(offset, value);
         
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
        	Range rangeToDelete = Range.of(Math.max(0, range.getBegin()),
        			Math.min(data.getCurrentLength()-1, range.getEnd()));
        	GrowableByteArray deletedBytes = data.subArray(rangeToDelete);
        	
            NewValues newValues = new NewValues(deletedBytes);
            this.codecDecider.delete((int)range.getBegin(),newValues);
            data.remove(rangeToDelete);
        }
        return this;
    }
	
	
	
    
    @Override
	public Nucleotide get(int offset) {
    	if(offset<0){
            throw new IndexOutOfBoundsException("offset can not have negatives coordinates: "+ offset);
        }
        if(offset> getLength()){
            throw new IndexOutOfBoundsException(
                    String.format("offset can not start beyond current length (%d) : %d", getLength(),offset));
        }
		return Nucleotide.VALUES.get(data.get(offset));
	}
	public int getNumGaps(){
        return codecDecider.getNumberOfGaps();
    }
	public int[] getGapOffsets() {
		return codecDecider.gapOffsets.toArray();		
	}

	
    int[] getNOffsets(){
    	return codecDecider.nOffsets.toArray();
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
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, String) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, char[])
     */
    public NucleotideSequenceBuilder prepend(char[] sequence){
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
    public NucleotideSequenceBuilder insert(int offset, NucleotideSequence sequence){
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
		data.insert(offset, newValues.data);
		
        this.codecDecider.insert(offset,newValues);
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
        if(offset> getLength()){
            throw new IllegalArgumentException(
                    String.format("offset can not start beyond current length (%d) : %d", getLength(),offset));
        }
        NucleotideSequenceBuilder otherSequenceBuilder = (NucleotideSequenceBuilder)otherBuilder;
        NewValues newValues = new NewValues(otherSequenceBuilder);
        if(offset == getLength()){
        	//act like append!
        	return append(newValues);
        }
       
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
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, NucleotideSequence) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, Iterable)
     */
    public NucleotideSequenceBuilder prepend(NucleotideSequence sequence){
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
        	
        	return codecDecider.encode(iterator());

    }
    @Override
    public Iterator<Nucleotide> iterator() {
    	return new Iterator<Nucleotide>(){
    		int currentOffset=0;
    		int length = data.getCurrentLength();
    		
    		
			@Override
			public boolean hasNext() {
				return currentOffset<length;
			}
			@Override
			public Nucleotide next() {
				return Nucleotide.getByOrdinal(data.get(currentOffset++));
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
    	NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(data.subArray(trimRange));
		if(codecDecider.hasAlignedReference()){
			builder.setReferenceHint(codecDecider.alignedReference.reference, codecDecider.alignedReference.offset+ (int)range.getBegin());
		}
		this.codecDecider = builder.codecDecider;
		this.data = builder.data;
		return this;
    }
   
	
    
	/**
	 * 
	 * {@inheritDoc}
	 */
	public NucleotideSequenceBuilder copy(){	
		return new NucleotideSequenceBuilder(this);
	}
    /**
     * Create a copy of only the {@link Range}
     * to use. If the range extends beyond this builder's
     * sequence, then only the intersecting portion is used.
     * 
     * @param gappedRange the range in gapped coordinates; can not be null.
     * 
     * @return a new NucleotideSequenceBuilder; will never be null.
     * 
     * @since 5.0
     */
	public NucleotideSequenceBuilder copy(Range gappedRange) {
		return new NucleotideSequenceBuilder(data.subArray(gappedRange));
	}
   
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data.toArray());
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
		
		
		return Arrays.equals(data.toArray(),other.data.toArray());
	}
	/**
	 * Convenience equality check against a {@link NucleotideSequence}
	 * so that we don't have to build this builder
	 * just to check.
	 * @param other the {@link NucleotideSequence} to check against;
	 * may be null.
	 * @return {@code false} if other is null or does not have the 
	 * exact same length and {@link Nucleotide}s in the same order;
	 * {@code true} otherwise.
	 * @since 5.0
	 */
	public boolean isEqualTo(NucleotideSequence other){
		if(other ==null){
			return false;
		}
		if(getLength() != other.getLength()){
			return false;
		}
		Iterator<Nucleotide> iter = iterator();
		Iterator<Nucleotide> otherIter = other.iterator();
		while(iter.hasNext()){
			if(!iter.next().equals(otherIter.next())){
				return false;
			}
		}
		return true;
	}
	
	public boolean isEqualToIgnoringGaps(NucleotideSequence other){
	    	if(other ==null){
	    		return false;
	    	}
	    	if(getUngappedLength() != other.getUngappedLength()){
	    		return false;
	    	}
	    	Iterator<Nucleotide> iter = iterator();
	    	Iterator<Nucleotide> otherIter = other.iterator(); 
	    	while(iter.hasNext()){
	    		//have to duplicate get non-gap
	    		//code because can't use private helper method
	    		//inside a default method.
	    		Nucleotide nextNonGap = getNextNonGapBaseFrom(iter);
	    		
	    		if(nextNonGap !=null){    			
	    			//haven't reached the end of our sequence
	    			//yet so check the other sequence for equality
	    			Nucleotide nextOtherNonGap=getNextNonGapBaseFrom(otherIter);
		    		
		    		//if we get this far,
		    		//then the our next base is NOT a gap
		    		//so the other seq better equal
		    		if(!nextNonGap.equals(nextOtherNonGap)){
		    			return false;
		    		}
	    		}
	    		
	    	}
	    	//if we get this far then our entire sequences
	    	//matched. because we previously
	    	//checked that the ungapped lengths matched
	    	//so if either iterator still has elements
	    	//they must all be gaps.
	    	return true;
	    }


	private Nucleotide getNextNonGapBaseFrom(Iterator<Nucleotide> iter) {
		Nucleotide nextNonGap;
		do{
			nextNonGap =iter.next();
		}while(nextNonGap.isGap() && iter.hasNext());
		if(nextNonGap.isGap()){
			return null;
		}
		return nextNonGap;
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
    	byte[] array = data.toArray();
    	for(int i=0; i< array.length;i++){
    		builder.append(Nucleotide.VALUES.get(array[i]));
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
    	byte[] bytes = data.toArray();
        int currentLength = bytes.length;
        int pivotOffset = currentLength/2;
        
        
        for(int i=0; i<pivotOffset; i++){
            int compOffset = currentLength-1-i;
            
            Nucleotide tmp = Nucleotide.VALUES.get(bytes[i]).complement();
           
            byte complementOrdinal = Nucleotide.VALUES.get(bytes[compOffset]).complement().getOrdinalAsByte();
            bytes[i] = complementOrdinal;
            bytes[compOffset] = tmp.getOrdinalAsByte();
        }
        if(currentLength%2!=0){
        	bytes[pivotOffset] = Nucleotide.VALUES.get(bytes[pivotOffset]).complement().getOrdinalAsByte();
        }
        data = new GrowableByteArray(bytes);
        codecDecider.reverse();
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
        byte[] complementedData = new byte[currentLength];
        byte[] originalData = data.toArray();
        for(int i=0; i<originalData.length; i++){
        	complementedData[i]=Nucleotide.VALUES.get(originalData[i]).complement().getOrdinalAsByte();
        }
        this.data = new GrowableByteArray(complementedData);
        //codec decider shouldn't change since number
        //of ambiguities, Ns and gaps wont change
        //and the offsets of N's and gaps won't change
        //either since they are self-complementing.
        return this;
    }
    
    /**
     * Turn off more extreme data compression which
     * will improve cpu performance at the cost
     * of the built {@link NucleotideSequence} taking up more memory.
     * By default, if this method is not called, then 
     * the data compression is turned ON which is the equivalent
     * of calling this method with the parameter set to {@code false}.
     * @param turnOffDataCompression {@code true} to turn off data compression;
     * {@code false} to keep data compression on.  Defaults to {@code false}. 
     * @return this.
     */
    public NucleotideSequenceBuilder turnOffDataCompression(boolean turnOffDataCompression){
    	codecDecider.forceBasicCompression(turnOffDataCompression);
    	return this;
    }
    /**
     * {@inheritDoc}
     * 
     * @see #reverseComplement()
     */
    @Override
	public NucleotideSequenceBuilder reverse() {
        data.reverse();        
        codecDecider.reverse();
		return this;
	}
	/**
     * Remove all gaps currently present in this builder.
     * @return this.
     */
    public NucleotideSequenceBuilder ungap(){
		final int numGaps = codecDecider.getNumberOfGaps();
		// if we have no gaps then we can short circuit
		// and do nothing
		if (numGaps == 0) {
			return this;
		}
		
		byte[] oldBytes = data.toArray();
		byte[] newBytes = new byte[oldBytes.length-codecDecider.gapOffsets.getCurrentLength()];
		// bulk copy all bits that aren't
		// for the gaps
		Iterator<Integer> gapIterator = codecDecider.gapOffsets.iterator();
		
		int oldOffset = 0;
		int newOffset = 0;
		while (gapIterator.hasNext()) {
			int nextGapOffset = gapIterator.next().intValue();
			for (; oldOffset < nextGapOffset; oldOffset++,newOffset++) {
				newBytes[newOffset] = oldBytes[oldOffset];
			}
			// skip gap
			oldOffset ++;
		}
		// fill in rest of bits after the gaps
		for (; oldOffset < oldBytes.length; oldOffset++,newOffset++) {
			newBytes[newOffset] = oldBytes[oldOffset];
		}
		data = new GrowableByteArray(newBytes);
		codecDecider.ungap();
		return this;
    }
    
    public Range toGappedRange(Range ungappedRegion) {
    	int ungappedStart = (int)ungappedRegion.getBegin();
    	int ungappedEnd = (int)ungappedRegion.getEnd();
    	return Range.of(getGappedOffsetFor(ungappedStart),
    			getGappedOffsetFor(ungappedEnd)
    			); 
	}
    
    public int getGappedOffsetFor(int ungappedOffset){
    	SingleThreadAdder currentOffset = new SingleThreadAdder(ungappedOffset);
    	codecDecider.gapOffsets.stream()
    							.forEach(i ->{
    								if( i <= currentOffset.intValue()){
    									currentOffset.increment();
    								}
    							});
    	
    	return currentOffset.intValue();
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
       // private int numberOfGaps=0;
        private int numberOfNonNAmbiguities=0;
       // private int numberOfNs=0;
        private int currentLength=0;
        private AlignedReference alignedReference=null;
        private GrowableIntArray gapOffsets;
        private GrowableIntArray nOffsets;
        
        private boolean forceBasicCodec = false;
        
        CodecDecider(){
        	//needs to be initialized
        	gapOffsets = new GrowableIntArray(12);
        	nOffsets = new GrowableIntArray(12);
        }
        public NucleotideSequence encode(Iterator<Nucleotide> iterator) {
        	
        	int numberOfGaps = gapOffsets.getCurrentLength();
            int numberOfNs = nOffsets.getCurrentLength();
            
			if(forceBasicCodec || numberOfNonNAmbiguities>0 || (numberOfGaps>0 && numberOfNs >0)){
                byte[] encodedBytes= BasicNucleotideCodec.INSTANCE.encode(currentLength, gapOffsets.toArray(), iterator);
                return new DefaultNucleotideSequence(BasicNucleotideCodec.INSTANCE, encodedBytes);
			}
			//if we get this far then we don't have any non-N ambiguities
			//AND we have either only gaps or only Ns
            int fourBitBufferSize =BasicNucleotideCodec.INSTANCE.getNumberOfEncodedBytesFor(currentLength, numberOfGaps);
            int twoBitBufferSize = AcgtnNucloetideCodec.INSTANCE.getNumberOfEncodedBytesFor(currentLength,
            		Math.max(numberOfGaps, numberOfNs));
            if(fourBitBufferSize < twoBitBufferSize){
                byte[] encodedBytes= BasicNucleotideCodec.INSTANCE.encode(currentLength, gapOffsets.toArray(), iterator);
                return new DefaultNucleotideSequence(BasicNucleotideCodec.INSTANCE, encodedBytes);
            }
            if(numberOfGaps==0 ){
                byte[] encodedBytes= AcgtnNucloetideCodec.INSTANCE.encode(currentLength, nOffsets.toArray(), iterator);
                return new DefaultNucleotideSequence(AcgtnNucloetideCodec.INSTANCE, encodedBytes);
            }
            
            byte[] encodedBytes= AcgtGapNucleotideCodec.INSTANCE.encode(currentLength, gapOffsets.toArray(), iterator);
            return new DefaultNucleotideSequence(AcgtGapNucleotideCodec.INSTANCE, encodedBytes);
       
		}
		CodecDecider(NewValues newValues){
        	nOffsets = newValues.getNOffsets().copy();
			currentLength = newValues.getLength();
			numberOfNonNAmbiguities = newValues.getnumberOfNonNAmiguities();
			gapOffsets = newValues.getGapOffsets().copy();
        }
        CodecDecider copy(){
        	CodecDecider copy = new CodecDecider();
        	copy.numberOfNonNAmbiguities = numberOfNonNAmbiguities;
        	
        	copy.currentLength= currentLength;
        	copy.nOffsets = nOffsets.copy();
        	copy.alignedReference = alignedReference;
        	copy.gapOffsets = gapOffsets.copy();
        	return copy;
        	
        }
        
        void forceBasicCompression(boolean forceBasicCompression){
        	this.forceBasicCodec = forceBasicCompression;
        	
        }
        
        void clear(){
        	 gapOffsets.clear();
        	 nOffsets.clear();
        	 
             numberOfNonNAmbiguities=0;            
             currentLength=0;
             alignedReference=null;
        }
        
        void alignedReference(AlignedReference ref){
        	this.alignedReference = ref;
        }
        
        boolean hasAlignedReference(){
        	return alignedReference!=null;
        }
        
        private void append(GrowableIntArray src, GrowableIntArray dest){
        	int[] newGaps =src.toArray();        	
        	for(int i=0; i< newGaps.length; i++){
        		newGaps[i] +=currentLength;
        	}
        	//should already be in sorted order
        	//so we don't have to re-sort        	
        	dest.append(newGaps);
        }
        public void append(NucleotideSequenceBuilder other) {
        	CodecDecider otherDecider = other.codecDecider;
        	
        	append(otherDecider.gapOffsets, gapOffsets);
        	append(otherDecider.nOffsets, nOffsets);
        
			currentLength += other.getLength();
			numberOfNonNAmbiguities += otherDecider.numberOfNonNAmbiguities;
        }
        public void append(NewValues newValues) {
        	append(newValues.getGapOffsets(), gapOffsets);
        	append(newValues.getNOffsets(), nOffsets);
        
			currentLength += newValues.getLength();
			numberOfNonNAmbiguities += newValues.getnumberOfNonNAmiguities();
			
			
        }
        
        private void insert(GrowableIntArray src, GrowableIntArray dest, int insertionOffset, int insertionLength){
        	int currentGapLength=dest.getCurrentLength();
        	int insertLength = insertionLength;
        	//shift downstream gaps we already have
        	for(int i=0; i<currentGapLength; i++){
        		int currentValue = dest.get(i);
        		if(currentValue>=insertionOffset){
        			dest.replace(i, currentValue +insertLength);
        		}
        	}
        	int[] newGaps =src.toArray();
        	for(int i=0; i< newGaps.length; i++){
        		newGaps[i] +=insertionOffset;
        	}
        	dest.sortedInsert(newGaps);
        	
        }
        
        public void insert(int startOffset, NewValues newValues){
        	int insertLength = newValues.getLength();
        	if(startOffset ==0){
        		//use optimized prepend
        		gapOffsets = prepend(newValues.getGapOffsets(), gapOffsets, insertLength);
        		nOffsets = prepend(newValues.getNOffsets(), nOffsets, insertLength);
        	}else{        	
	        	insert(newValues.getGapOffsets(), gapOffsets, startOffset, insertLength);
	        	insert(newValues.getNOffsets(), nOffsets, startOffset, insertLength);
        	}

			currentLength += insertLength;
			numberOfNonNAmbiguities += newValues.getnumberOfNonNAmiguities();		
			
        }
        
        private GrowableIntArray prepend(GrowableIntArray src, GrowableIntArray original, int insertionLength){
        	int oldGaps[] =original.toArray();
        	for(int i=0; i< oldGaps.length; i++){
        		oldGaps[i] +=insertionLength;
        	}
        	//should already be in sorted order
        	//so we don't have to re-sort        	
        	GrowableIntArray newOffsets= new GrowableIntArray(insertionLength + original.getCurrentCapacity());
        	newOffsets.append(src);
        	newOffsets.append(oldGaps);
        	
        	return newOffsets;
        }
        
        
        public void reverse(){
        	
        	gapOffsets = reverseCoordinates(gapOffsets);
        	nOffsets = reverseCoordinates(nOffsets);
        	
        }
		private GrowableIntArray reverseCoordinates(GrowableIntArray array) {
			int gaps[] =array.toArray();
        	int delta = currentLength-1;
        	for(int i=0; i<gaps.length; i++){
        		gaps[i]= delta-gaps[i];
        	}
        	GrowableIntArray newArray = new GrowableIntArray(array.getCurrentCapacity());
        	newArray.append(gaps);
        	newArray.reverse();
			return newArray;
		}
        
        private void delete(GrowableIntArray array, int startOffset, int[] gapsToDelete, int lengthDeleted){
        	
			for(int i=0; i<gapsToDelete.length; i++){
				array.sortedRemove(gapsToDelete[i]+startOffset);				
			}
			
			//shift all downstream offsets accordingly
			int lastGap = startOffset+lengthDeleted-1;
			int remainingGapLength = array.getCurrentLength();
			//we know that we won't have to shift any offsets
			//upstream of the deleted region
			//return of binarySearch is guaranteed to be
			//negative (because we would have deleted it above
			for(int i=-array.binarySearch(lastGap) -1; i<remainingGapLength; i++){
				try{
				if(array.get(i)> lastGap){
					array.replace(i, array.get(i) - lengthDeleted);
				}
				}catch(Throwable t){
					throw new RuntimeException(t);
				}
			}
        }
        
        public void delete(int startOffset, NewValues newValues) {
        	delete(gapOffsets, startOffset, newValues.getGapOffsets().toArray(), newValues.getLength());
        	delete(nOffsets, startOffset, newValues.getNOffsets().toArray(),newValues.getLength());
        	
			currentLength -= newValues.getLength();
			numberOfNonNAmbiguities -= newValues.getnumberOfNonNAmiguities();
        }
       
		
        public void replace(int offset,byte oldValue, byte newValue) {
            handleReplacementValue(offset, oldValue,false);
            handleReplacementValue(offset,newValue,true);
        }

       
        
        void handleReplacementValue(int offset, int value, boolean insert) {
            if(value == GAP_VALUE){
            	replaceValue(gapOffsets, offset, insert);               
            }else if(value == N_VALUE){
            	replaceValue(nOffsets, offset, insert);
            }else if(value != A_VALUE && value != C_VALUE 
                    && value != G_VALUE && value != T_VALUE){
                handleAmbiguity(insert);                
            }
        }

        private void replaceValue(GrowableIntArray array, int offset, boolean insert){
        	if(insert){
        		array.sortedInsert(offset);
         	  
            }else{
            	array.sortedRemove(offset);         	   
            }
        }
        
        private void handleAmbiguity(boolean increment) {
            if(increment){
                numberOfNonNAmbiguities++;
            }else{
                numberOfNonNAmbiguities--;
            }
        }

      
        
        void ungap(){
        	//first we have to shift the N's
        	int[] gaps =gapOffsets.toArray();
        	int[] newNOffsets = new int[nOffsets.getCurrentLength()];
        	
        	PeekableIterator<Integer> gapOffsetIter = IteratorUtil.createPeekableIterator(gapOffsets.iterator());
        	Iterator<Integer> nOffsetIter = nOffsets.iterator();
        	
        	int shiftSize=0;
        	int i=0;
        	while(nOffsetIter.hasNext()){
        		int currentNOffset = nOffsetIter.next();
        		while(gapOffsetIter.hasNext()){
        			int nextGapOffset =gapOffsetIter.peek();
        			if(nextGapOffset < currentNOffset){
        				shiftSize++;
        				gapOffsetIter.next();
        			}else{
        				break;
        			}
        		}
        		newNOffsets[i] = currentNOffset - shiftSize;
        		i++;
        	}
        	nOffsets = new GrowableIntArray(newNOffsets);
        	//now we can remove the gaps
            currentLength-=gaps.length;
            gapOffsets.clear();
            
        }
        /**
         * @return the numberOfGaps
         */
        int getNumberOfGaps() {
            return gapOffsets.getCurrentLength();
        }
        /**
         * @return the numberOfNonNAmbiguities
         */
        int getNumberOfAmbiguities() {
            return numberOfNonNAmbiguities + getNumberOfNs();
        }
        /**
         * @return the numberOfNs
         */
        int getNumberOfNs() {
            return nOffsets.getCurrentLength();
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
    
    
    
    private static class NewValues{
    	
    	private final GrowableByteArray data;
    	private int numberOfACGTs;
    	
    	private final GrowableIntArray nOffsets;
    	private final GrowableIntArray gapOffsets;

    	public NewValues(GrowableByteArray data){
    		this.data = data.copy();
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
			
    		
    		SingleThreadAdder offset = new SingleThreadAdder();
    		data.stream()
    				.forEach(i->{
    					handleOrdinal((byte)i, offset.intValue());
    					offset.increment();
    				});
    		
    	}
    	
    	public NewValues(Nucleotide nucleotide){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
    		data = new GrowableByteArray(1);
    		
            handle(nucleotide, 0);
            //only one value so we
            //don't need to sort
    	}
    	public NewValues(String sequence){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
			data = new GrowableByteArray(sequence.length());
			
    		int offset=0;
            //convert sequence to char[] which 
            //will run faster than sequence.charAt(i)
            char[] chars = sequence.toCharArray();
            
    		for(int i=0; i<chars.length; i++){
    			char c = chars[i];    			
				Nucleotide n = Nucleotide.parseOrNull(c);
				if(n !=null){
    				handle(n, offset);
                	offset++;
    			}
    		}
    		
    	}
    	public NewValues(char[] sequence){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
			data = new GrowableByteArray(sequence.length);
			
    		int offset=0;
            
    		for(int i=0; i<sequence.length; i++){
    			char c = sequence[i];    			
				Nucleotide n = Nucleotide.parseOrNull(c);
				if(n !=null){
    				handle(n, offset);
                	offset++;
    			}
    		}
    		
    	}
    	
    	
    	/**
    	 * Convenience construcutor that allocates
    	 * the gap Offsets and bitSet fields to the needed
    	 * sizes
    	 * since we know those sizes before processing. 
    	 * @param sequence
    	 */
    	public NewValues(NucleotideSequence sequence){
    		nOffsets = new GrowableIntArray(12);
    		gapOffsets = new GrowableIntArray(sequence.getNumberOfGaps());
    		data = new GrowableByteArray((int)sequence.getLength());
    		
            int offset=0;
            for(Nucleotide n : sequence){
            	handle(n, offset);
            	offset++;            	
            }
    	}
    	
    	public NewValues(Iterable<Nucleotide> nucleotides){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
    		data = new GrowableByteArray(100);
            int offset=0;
            for(Nucleotide n : nucleotides){
            	handle(n, offset);
            	offset++;            	
            }
    	}
    	@SuppressWarnings("fallthrough")
		private void handle(Nucleotide n, int offset) {
			byte value=n.getOrdinalAsByte();
			//switch statements has been optimized using profiler 
			//this will cause a special tableswitch opcode
			//which is is an O(1) lookup instead of an
			//o(n) lookupswitch opcode.  
			//This switch will also increment
			//the nuclotide counts usually
			//done by handle(value) so
			//we don't need to do the lookup twice
			data.append(value);
			
			switch(value){
				case 0:nOffsets.append(offset);  break;
				case 1:
				case 2: 
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10: break;
				case 11: gapOffsets.append(offset);
							break;
				case 12:
				case 13:
				case 14:
				case 15: numberOfACGTs++;
						break;
				default: break;
			}

		}
    	@SuppressWarnings("fallthrough")
		private void handleOrdinal(byte ordinal, int offset) {
			//switch statements has been optimized using profiler 
			//this will cause a special tableswitch opcode
			//which is is an O(1) lookup instead of an
			//o(n) lookupswitch opcode
			switch(ordinal){
			case 0 :nOffsets.append(offset); break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:break;
			case 11:gapOffsets.append(offset); break;
			case 12:
			case 13:
			case 14:
			case 15:numberOfACGTs++; break;
			default: break;
			}
		
		}

		public int getnumberOfNonNAmiguities() {
			return getLength() - (getNumberOfGaps() + getNumberOfNs()+ numberOfACGTs);
		}

		

		public GrowableByteArray getData() {
			return data;
		}

		public int getLength() {
			return data.getCurrentLength();
		}

		public int getNumberOfGaps() {
			return gapOffsets.getCurrentLength();
		}

		public int getNumberOfNs() {
			return nOffsets.getCurrentLength();
		}
		public GrowableIntArray getGapOffsets() {
			return gapOffsets;
		}
		public GrowableIntArray getNOffsets() {
			return nOffsets;
		}
    	
    	
    	
    }



	





	



	
}
