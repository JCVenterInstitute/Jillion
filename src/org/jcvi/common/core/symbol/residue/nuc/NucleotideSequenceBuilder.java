/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.symbol.residue.nuc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.util.Builder;

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
public final class NucleotideSequenceBuilder implements Builder<NucleotideSequence>{
    private static final int GAP_VALUE = Nucleotide.Gap.ordinal();
    private byte[] array;
    private int currentLength=0;
    private int numGaps=0;
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     */
    public NucleotideSequenceBuilder(){
        array = new byte[16];
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     * @param initialCapacity the initial capacity 
     * of the array backing the nucleotidesequence
     * (will be grown if sequence gets too large)
     * @throws IllegalArgumentException if initialCapacity < 1.
     */
    public NucleotideSequenceBuilder(int initialCapacity){
        if(initialCapacity<1){
            throw new IllegalArgumentException("initial capacity must be >=1");
        }
        array = new byte[initialCapacity];
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder(Iterable<Nucleotide> sequence){
        assertNotNull(sequence);
        this.array = convertToEncodedArray(sequence);
        this.currentLength =array.length;
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder(String sequence){
        this(Nucleotides.parse(sequence));
        
    }
    
    private byte[] convertToEncodedArray(Iterable<Nucleotide> nucleotides){
        List<Byte> list = new ArrayList<Byte>();
        for(Nucleotide n : nucleotides){
            int value = n.ordinal();
            if(value == GAP_VALUE){
                numGaps++;
            }
            list.add((byte)value);
        }
        ByteBuffer buf = ByteBuffer.allocate(list.size());
        for(Byte b : list){
            buf.put(b.byteValue());
        }
        return buf.array();
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
        byte[] newData = convertToEncodedArray(sequence);
        return appendArray(newData);
    }
    
    /**
     * Appends the contents of the given {@link NucleotideSequenceBuilder} to the end
     * of the builder's mutable sequence.
     * @param sequenceBuilder the {@link NucleotideSequenceBuilder} to be appended
     * to the end our builder.
     * @throws NullPointerException if sequenceBuilder is null.
     */
    public NucleotideSequenceBuilder append(NucleotideSequenceBuilder sequenceBuilder){
        assertNotNull(sequenceBuilder);
        appendArray(sequenceBuilder.array);
        numGaps += sequenceBuilder.numGaps;
        return this;
    }
    
    private NucleotideSequenceBuilder appendArray(byte[] newData) {
        int newLength = currentLength+newData.length;
        ensureCapacity(newLength);
        System.arraycopy(newData, 0, array, currentLength, newData.length);
        this.currentLength = newLength;
        return this;
    }
    
    private void ensureCapacity(int newSize){
        if(array.length <newSize){
            byte[] increasedArray = new byte[newSize];
            System.arraycopy(array, 0, increasedArray, 0, array.length);
            this.array = increasedArray;
        }
    }
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(String sequence){
        return append(Nucleotides.parse(sequence));
    }
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    public NucleotideSequenceBuilder insert(int offset, String sequence){
        return insert(offset, Nucleotides.parse(sequence));
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
        return currentLength;
    }
    
    public NucleotideSequenceBuilder replace(int offset, Nucleotide replacement){
        if(offset <0 || offset > array.length){
            throw new IllegalArgumentException(
                    String.format("offset %d out of range (length = %d)",array.length,offset));
        }
        if(replacement ==null){
            throw new NullPointerException("replacement base can not be null");
        }
        array[offset]= (byte)replacement.ordinal();
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
            int start = (int)range.getStart();
            if(start<0){
                throw new IllegalArgumentException("range can not have negatives coordinates: "+ start);
            }
            if(start> getLength()){
                throw new IllegalArgumentException(
                        String.format("range can not start beyond current length (%d) : %d", getLength(),start));
            }   
            int end = Math.min(currentLength-1, (int)range.getEnd());
            int len = end - start+1;
            if (len > 0) {
                System.arraycopy(array, start+len, array, start, currentLength -end-1);
                this.currentLength -=len;
            }
            //  System.arraycopy(value, start+len, value, start, count-end);
            
           
        }
        return this;
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
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    public NucleotideSequenceBuilder insert(int offset, Iterable<Nucleotide> sequence){
        assertNotNull(sequence);
        if(offset<0){
            throw new IllegalArgumentException("offset can not have negatives coordinates: "+ offset);
        }
        if(offset> getLength()){
            throw new IllegalArgumentException(
                    String.format("offset can not start beyond current length (%d) : %d", getLength(),offset));
        }   
        byte[] newData = convertToEncodedArray(sequence);
        int newDataLength = currentLength+newData.length;
        ensureCapacity(newDataLength);       
        //shift downstream bases
        System.arraycopy(array, offset, array, offset+newData.length, currentLength -offset);
        //add new data
        System.arraycopy(newData, 0, array, offset, newData.length);
        this.currentLength=newDataLength;
        return this;
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
        return insert(0, Nucleotides.asString(sequence));
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
        NucleotideCodec codec = NucleotideCodecs.getCodecForGappedSequence(numGaps, currentLength);
        return DefaultNucleotideSequence.create(asList(),codec);
    }
    /**
     * Create a new NucleotideSequence instance
     * from containing only current mutable nucleotides
     * in the given range.
     * @param range the range of nucleotides to build (gapped).
     * @return a new NucleotideSequence never null
     * but may be empty.
     */
    public NucleotideSequence build(Range range) {
        return NucleotideSequenceFactory.create(asList(range));
    }
    /**
     * Get a sublist of the current nucleotide sequence as a list
     * of Nucleotide objects.
     * @param range the  range of the sublist to generate.
     * @return a new List of Nucleotides.
     * @throws NullPointerException if range is null.
     * @throws IllegalArgumentException if range is not a sublist of the current
     * sequence.
     */
    public List<Nucleotide> asList(Range range){
    	Range currentRange = Range.buildRangeOfLength(currentLength);
    	if(!range.isSubRangeOf(currentRange)){
    		throw new IllegalArgumentException(
    				"range is not a sub-range of the sequence: "+ range);
    	}
        List<Nucleotide> bases = new ArrayList<Nucleotide>((int)range.getLength());
        int start = (int)range.getStart();
        int end = (int)range.getEnd();
        
        Nucleotide[] values = Nucleotide.values();
        for(int i=start; i<=end; i++){            
            bases.add(values[array[i]]);
        }
        return bases;
    }
    /**
     * Get the entire current nucleotide sequence as a list
     * of Nucleotide objects.
     * @return a new List of Nucleotides.
     */
    public List<Nucleotide> asList(){
        return asList(Range.buildRangeOfLength(currentLength));
    }
    
    /**
     * Get the current Nucleotides as a String
     * this will return the same string 
     * as Nucleotides.asString(this.asList()))
     */
    @Override
    public String toString(){
        return Nucleotides.asString(asList());
    }
    /**
     * Reverse compliment all the nucleotides currently in this builder.
     * Calling this method will only reverse compliment bases that 
     * already exist in this builder; any additional operations
     * to insert bases will not be affected.
     * <p/>
     * For example:
     * <pre>
     *      new NucleotideSequenceBuilder("CGGC")
                .reverseCompliment()
                .append("N");                
     * </pre>
     * will generate a Sequence "GCCGN".
     * @return this.
     */
    public NucleotideSequenceBuilder reverseCompliment(){
        int pivotOffset = currentLength/2;
        Nucleotide[] values = Nucleotide.values();
        for(int i=0; i<pivotOffset; i++){
            int compOffset = currentLength-1-i;
            Nucleotide tmp = values[array[i]].compliment();
            array[i] = (byte) values[array[compOffset]].compliment().ordinal();
            array[compOffset] = (byte) tmp.ordinal();
        }
        if(currentLength%2!=0){
            array[pivotOffset] = (byte)values[array[pivotOffset]].compliment().ordinal();
        }
        return this;
    }
    /**
     * Remove all gaps currently present in this builder.
     * @return this.
     */
    public NucleotideSequenceBuilder ungap(){
        if(numGaps>0){
            byte[] ungapped = new byte[currentLength-numGaps];
            
            for(int oldOffset=0, newOffset=0; oldOffset<currentLength; oldOffset++){
                if(array[oldOffset]!=GAP_VALUE){
                    ungapped[newOffset] = array[oldOffset];
                    newOffset++;
                }
            }
            array = ungapped;
            currentLength= array.length;
            numGaps=0;
        }
        return this;
    }

}
