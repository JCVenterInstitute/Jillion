/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
/**
 * {@code ProteinSequenceBuilder}  is a way to
 * construct a {@link ProteinSequence}
 * similar to how a {@link StringBuilder} can be used
 * to create a String.  The contents of the {@link ProteinSequence}
 * can be changed by method calls.  This class
 * is not thread safe.
 * @author dkatzel
 *
 *
 */
public final class ProteinSequenceBuilder implements ResidueSequenceBuilder<AminoAcid,ProteinSequence>{
	private static final AminoAcid[] AMINO_ACID_VALUES = AminoAcid.values();
	private static final byte GAP_ORDINAL = AminoAcid.Gap.getOrdinalAsByte();
	
	private static final int DEFAULT_CAPACITY = 20;
	private GrowableByteArray builder;
	private int numberOfGaps=0;
	 /**
     * Creates a new ProteinSequenceBuilder instance
     * which currently contains no amino acids.
     */
	public ProteinSequenceBuilder(){
		builder = new GrowableByteArray(DEFAULT_CAPACITY);
	}
	/**
     * Creates a new ProteinSequenceBuilder instance
     * which currently contains no amino acids
     * but is expected to be eventually take up
     * the given capacity.
     * @param initialCapacity the initial capacity 
     * of the array backing the {@link ProteinSequence}
     * (will be grown if sequence gets too large)
     * @throws IllegalArgumentException if initialCapacity < 1.
     */
	public ProteinSequenceBuilder(int initialCapacity){
		builder = new GrowableByteArray(initialCapacity);
	}
	/**
     * Creates a new ProteinSequenceBuilder instance
     * which currently contains the given sequence.
     *  Any whitespace in the input string will be ignored.
     * @param sequence the initial nucleotide sequence.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into an {@link AminoAcid}.
     */
	public ProteinSequenceBuilder(CharSequence sequence){
		builder = new GrowableByteArray(sequence.length());
		append(parse(sequence.toString()));
	}
	/**
     * Creates a new ProteinSequenceBuilder instance
     * which currently contains the given sequence.
     * @param sequence the initial protein sequence.
     * @throws NullPointerException if sequence is null.
     */
	public ProteinSequenceBuilder(ProteinSequence sequence){
		builder = new GrowableByteArray((int)sequence.getLength());
		append(sequence);
	}
	private ProteinSequenceBuilder(ProteinSequenceBuilder copy){
		builder = copy.builder.copy();
	}
	
	private static List<AminoAcid> parse(String aminoAcids){
		List<AminoAcid> result = new ArrayList<AminoAcid>(aminoAcids.length());
        for(int i=0; i<aminoAcids.length(); i++){
            char charAt = aminoAcids.charAt(i);
            if(!Character.isWhitespace(charAt)){
            	result.add(AminoAcid.parse(charAt));
            }
        }
        return result;
	}
	/**
     * Appends the given residue to the end
     * of the builder's mutable sequence.
     * @param base a single {@link AminoAcid} to be appended
     * to the end our builder.
     * @throws NullPointerException if residue is null.
     */
	@Override
	public ProteinSequenceBuilder append(AminoAcid residue) {
		if(residue==AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.append(residue.getOrdinalAsByte());
		return this;
	}

	
	@Override
	public ProteinSequenceBuilder clear() {
		numberOfGaps=0;
		builder.clear();
		return this;
	}
	/**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the protein sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
	@Override
	public ProteinSequenceBuilder append(Iterable<AminoAcid> sequence) {
		for(AminoAcid aa : sequence){
			append(aa);
		}
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
	public ProteinSequenceBuilder append(
			ProteinSequenceBuilder otherBuilder) {
		builder.append(otherBuilder.builder.toArray());
		return this;
	}

	@Override
	public ProteinSequenceBuilder append(
			String sequence) {
		return append(parse(sequence));
	}

	@Override
	public ProteinSequenceBuilder insert(
			int offset, String sequence) {
		List<AminoAcid> list = parse(sequence);
		byte[] array = new byte[list.size()];
		int i=0;
		for(AminoAcid aa :list){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			array[i]=(aa.getOrdinalAsByte());
		}		
		builder.insert(offset, array);
		return this;
	}

	
	@Override
	public AminoAcid get(int offset) {
		return AMINO_ACID_VALUES[builder.get(offset)];
	}

	@Override
	public long getLength() {
		return builder.getCurrentLength();
	}
	@Override
	public long getUngappedLength() {
		return builder.getCurrentLength() - numberOfGaps;
	}
	
	@Override
	public ProteinSequenceBuilder replace(
			int offset, AminoAcid replacement) {
		if(AMINO_ACID_VALUES[builder.get(offset)] == AminoAcid.Gap){
			numberOfGaps--;			
		}
		if(replacement == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.replace(offset, replacement.getOrdinalAsByte());
		return this;
	}

	@Override
	public ProteinSequenceBuilder delete(
			Range range) {
		for(AminoAcid aa : asList(range)){
			if(aa == AminoAcid.Gap){
				numberOfGaps --;
			}
		}
		builder.remove(range);
		return this;
	}

	@Override
	public int getNumGaps() {
		return numberOfGaps;
	}

	@Override
	public ProteinSequenceBuilder prepend(
			String sequence) {			
		return insert(0, sequence);
	}

	@Override
	public ProteinSequenceBuilder insert(
			int offset, Iterable<AminoAcid> sequence) {
		GrowableByteArray temp = new GrowableByteArray(DEFAULT_CAPACITY);
		for(AminoAcid aa :sequence){
			if(aa == AminoAcid.Gap){
				numberOfGaps++;
			}
			temp.append(aa.getOrdinalAsByte());
		}		
		builder.insert(offset, temp);
		return this;
	}

	@Override
	public ProteinSequenceBuilder insert(
			int offset,
			ResidueSequenceBuilder<AminoAcid, ProteinSequence> otherBuilder) {
		return insert(offset,otherBuilder.toString());
	}

	@Override
	public ProteinSequenceBuilder insert(
			int offset, AminoAcid base) {
		if(base == AminoAcid.Gap){
			numberOfGaps++;
		}
		builder.insert(offset, base.getOrdinalAsByte());
		return this;
	}

	@Override
	public ProteinSequenceBuilder prepend(
			Iterable<AminoAcid> sequence) {
		return insert(0, sequence);
	}

	@Override
	public ProteinSequenceBuilder prepend(
			ResidueSequenceBuilder<AminoAcid, ProteinSequence> otherBuilder) {
		return prepend(otherBuilder.toString());
	}

	@Override
	public ProteinSequence build() {
		return build(builder.toArray());
	}


	
	private AminoAcid[] convertFromBytes(byte[] array){
		AminoAcid[] aas = new AminoAcid[array.length];
		for(int i=0; i<array.length; i++){
			aas[i]=AMINO_ACID_VALUES[array[i]];
		}
		return aas;
	}
	private ProteinSequence build(byte[] seqToBuild){
		AminoAcid[] asList = convertFromBytes(seqToBuild);
		if(numberOfGaps>0 && hasGaps(asList)){
			return new CompactProteinSequence(asList);
		}
		//no gaps
		
		return new UngappedProteinSequence(asList);
	}
	private boolean hasGaps(AminoAcid[] asArray) {
		for(AminoAcid aa : asArray){
			if(aa == AminoAcid.Gap){
				return true;
			}
		}
		return false;
	}

	private List<AminoAcid> asList(Range range) {
		ProteinSequence s = build();
		List<AminoAcid> list = new ArrayList<AminoAcid>((int)range.getLength());
		Iterator<AminoAcid> iter = s.iterator(range);
		while(iter.hasNext()){
			list.add(iter.next());
		}
		return list;
	}


	@Override
	public ProteinSequenceBuilder trim(Range range) {
		Range intersection = range.intersection(Range.ofLength(getLength()));
		builder =builder.subArray(intersection);		
		this.numberOfGaps =builder.getCount(GAP_ORDINAL);
		return this;
		
		
	}


	@Override
	public ProteinSequenceBuilder copy() {
		return new ProteinSequenceBuilder(this);
		
	}

	@Override
	public ProteinSequenceBuilder reverse() {
		builder.reverse();
		return this;
	}

	@Override
	public ProteinSequenceBuilder ungap() {

		ProteinSequence list = build(builder.toArray());
		if(list.getNumberOfGaps() !=0){
			List<Integer> gapOffsets =list.getGapOffsets();
			for(int i=gapOffsets.size()-1; i>=0; i--){
				builder.remove(i);
			}
		}
		numberOfGaps=0;
		return this;
	}

	@Override
	public String toString() {
		byte[] array =builder.toArray();
		StringBuilder stringBuilder = new StringBuilder(array.length);
		AminoAcid[] values = AminoAcid.values();
		for(int i=0; i<array.length; i++){
			
			stringBuilder.append(values[array[i]]);
		}
		return stringBuilder.toString();
	}

	@Override
	public Iterator<AminoAcid> iterator() {
		return new IteratorImpl();
	}

	private class IteratorImpl implements Iterator<AminoAcid>{
		private int currentOffset=0;

		@Override
		public boolean hasNext() {
			return currentOffset<builder.getCurrentLength();
		}

		@Override
		public AminoAcid next() {
			AminoAcid next = AMINO_ACID_VALUES[builder.get(currentOffset)];
			currentOffset++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}
}
