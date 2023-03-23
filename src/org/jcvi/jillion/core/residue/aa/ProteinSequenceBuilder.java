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
package org.jcvi.jillion.core.residue.aa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
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
public final class ProteinSequenceBuilder implements ResidueSequenceBuilder<AminoAcid,ProteinSequence, ProteinSequenceBuilder>{
	private static final AminoAcid[] AMINO_ACID_VALUES = AminoAcid.values();
	private static final byte GAP_ORDINAL = AminoAcid.Gap.getOrdinalAsByte();
	
	private static final GrowableByteArray AMBIGUOUS_AMINO_ACIDS;
	static {
		AMBIGUOUS_AMINO_ACIDS = new GrowableByteArray();
		for(int i=0; i< AMINO_ACID_VALUES.length; i++) {
			AminoAcid aa = AMINO_ACID_VALUES[i];
			if(aa.isAmbiguity()) {
				AMBIGUOUS_AMINO_ACIDS.append(aa.getOrdinalAsByte());
			}
		}
	}
	private static final int DEFAULT_CAPACITY = 20;
	private GrowableByteArray builder;
	private int numberOfGaps=0;
	
	private int numberOfAmbiguities=0;
	private boolean turnOffCompression=false;
	
	private boolean includeStopCodon = true;
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
     * @throws IllegalArgumentException if initialCapacity &lt; 1.
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
	
    /**
     * Creates a new ProteinSequenceBuilder instance which currently contains
     * the given sequence.
     * 
     * @param sequence
     *            the initial protein sequence.
     *            
     *@param range the subrange to use
     * @throws NullPointerException
     *             if sequence is null.
     */
    public ProteinSequenceBuilder(ProteinSequence sequence, Range range) {
        builder = new GrowableByteArray((int) range.getLength());
        Iterator<AminoAcid> iter = sequence.iterator(range);
        while(iter.hasNext()){
            append(iter.next());
        }
    }
	private ProteinSequenceBuilder(ProteinSequenceBuilder copy){
		builder = copy.builder.copy();
		updateMetaData();
	}
	
	private ProteinSequenceBuilder(GrowableByteArray growableArray) {
		this.builder = growableArray;
		updateMetaData();
	}
	/**
	 * Update the metadata of number of gaps and ambiguities.
	 */
	private void updateMetaData() {
		this.numberOfGaps =builder.getCount(GAP_ORDINAL);
		this.numberOfAmbiguities =0;
		builder.forEachIndexed((i, ordinal)->{
			if(AMBIGUOUS_AMINO_ACIDS.binarySearch(ordinal)>=0) {
				numberOfAmbiguities++;
			}
		});
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
     * @param residue a single {@link AminoAcid} to be appended
     * to the end our builder.
     * @throws NullPointerException if residue is null.
     */
	@Override
	public ProteinSequenceBuilder append(AminoAcid residue) {
		if(residue==AminoAcid.Gap){
			numberOfGaps++;
		}else if(residue.isAmbiguity()) {
			numberOfAmbiguities++;
		}
		builder.append(residue.getOrdinalAsByte());
		return this;
	}

	
	@Override
	public ProteinSequenceBuilder clear() {
		numberOfGaps=0;
		numberOfAmbiguities=0;
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
     * Appends the current contents of the given {@link ProteinSequenceBuilder} to the end
     * of the builder's mutable sequence.  Any further modifications to the passed in builder
     * will not be reflected in this builder.  This is an equivalent but more efficient way operation
     * as {@code this.append(otherBuilder.build())}
     * 
     * @param otherBuilder the {@link ProteinSequenceBuilder} whose current
     * nucleotides are to be appended.
     * 
     * @return this.
     * 
     * @throws NullPointerException if otherBuilder is null.
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
			}else if(aa.isAmbiguity()) {
				numberOfAmbiguities++;
			}
			array[i]=(aa.getOrdinalAsByte());
			i++;
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
		}else if(replacement.isAmbiguity()) {
			numberOfAmbiguities++;
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
			}else if(aa.isAmbiguity()) {
				numberOfAmbiguities--;
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
			}else if(aa.isAmbiguity()) {
				numberOfAmbiguities++;
			}
			temp.append(aa.getOrdinalAsByte());
		}		
		builder.insert(offset, temp);
		return this;
	}

	@Override
	public ProteinSequenceBuilder insert(
			int offset,
			ProteinSequenceBuilder otherBuilder) {
		return insert(offset,otherBuilder.toString());
	}

	@Override
	public ProteinSequenceBuilder insert(
			int offset, AminoAcid base) {
		if(base == AminoAcid.Gap){
			numberOfGaps++;
		}else if(base.isAmbiguity()) {
			numberOfAmbiguities++;
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
			ProteinSequenceBuilder otherBuilder) {
		return prepend(otherBuilder.toString());
	}

	@Override
	public ProteinSequence build() {
		return new CodecDecider(numberOfGaps, numberOfAmbiguities>0, turnOffCompression)
				.build(convertFromBytes(builder.toArray()), false);
	}


	
	private AminoAcid[] convertFromBytes(byte[] array){
		if(includeStopCodon) {
			AminoAcid[] aas = new AminoAcid[array.length];
			for(int i=0; i<array.length; i++){
				aas[i]=AMINO_ACID_VALUES[array[i]];
			}
			return aas;
		}
		//trim off stop
		AminoAcid[] aas = new AminoAcid[array.length];
		int j=0;
		for(int i=0; i<array.length; i++){
			AminoAcid aa = AMINO_ACID_VALUES[array[i]];
			if(aa != AminoAcid.STOP) {
				aas[j++] = aa;
			}
		}
		return Arrays.copyOf(aas, j);
	}
	
	
	private static class CodecDecider{
		int numberOfGaps;
		boolean hasAmbiguities;
		boolean turnOffCompression;
		
		
		
		public CodecDecider(int numberOfGaps, boolean hasAmbiguities, boolean turnOffCompression) {
			super();
			this.numberOfGaps = numberOfGaps;
			this.hasAmbiguities = hasAmbiguities;
			this.turnOffCompression = turnOffCompression;
		}
		public ProteinSequence build(AminoAcid[] asList, boolean doubleCheck) {
			if(turnOffCompression) {
	            if (numberOfGaps > 0 && (!doubleCheck || (doubleCheck && hasGaps(asList)))) {
	            	if(hasAmbiguities && (!doubleCheck || (doubleCheck && hasAmbiguities(asList)))) {
	            		return new UnCompressedGappedProteinSequence(asList);
	            	}
	                return new UnCompressedGappedNoAmbiguityProteinSequence(asList);
	            }
	            if(hasAmbiguities && (!doubleCheck || (doubleCheck && hasAmbiguities(asList)))) {
	            	return new UnCompressedUngappedProteinSequence(asList);
	            }
	            
	            return new UnCompressedUnGappedNoAmbiguityProteinSequence(asList);
	        }else {
	            if (numberOfGaps > 0 && (!doubleCheck || (doubleCheck && hasGaps(asList)))) {
	            	if(hasAmbiguities && (!doubleCheck || (doubleCheck && hasAmbiguities(asList)))) {
	            		return new CompactProteinSequence(asList);
	            	}
	                return new GappedNoAmbiguityProteinSequence(asList);
	            }
	            //no gaps
	            if(hasAmbiguities && (!doubleCheck || (doubleCheck && hasAmbiguities(asList)))) {

		            return new UngappedProteinSequence(asList);
            	}
	          //no ambiguities
        		return new UngappedNoAmbiguityProteinSequence(asList);

	        }
		}
		private static boolean hasGaps(AminoAcid[] asArray) {
			for(AminoAcid aa : asArray){
				if(aa.isGap()){
					return true;
				}
			}
			return false;
		}
		private static boolean hasAmbiguities(AminoAcid[] asArray) {
			for(AminoAcid aa : asArray){
				if(aa.isAmbiguity()){
					return true;
				}
			}
			return false;
		}
		
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
		this.numberOfAmbiguities =0;
		builder.forEachIndexed((i, ordinal)->{
			if(AMBIGUOUS_AMINO_ACIDS.binarySearch(ordinal)>=0) {
				numberOfAmbiguities++;
			}
		});
		return this;
		
		
	}

	/**
     * Create a copy of only the {@link Range}
     * to use. If the range extends beyond this builder's
     * sequence, then only the intersecting portion is used.
     * 
     * @param gappedRange the range in gapped coordinates; can not be null.
     * 
     * @return a new ProteinSequenceBuilder; will never be null.
     * 
     * @since 6.0
     */
	public ProteinSequenceBuilder copy(Range gappedRange) {
		Range intersection = gappedRange.intersection(Range.ofLength(getLength()));
		ProteinSequenceBuilder copy= new ProteinSequenceBuilder(builder.subArray(intersection));
		copy.turnOffCompression = this.turnOffCompression;
		return copy;
	}

	@Override
	public ProteinSequenceBuilder copy() {
		ProteinSequenceBuilder copy= new ProteinSequenceBuilder(this);
		copy.turnOffCompression = this.turnOffCompression;
		return copy;
		
	}

	@Override
	public ProteinSequenceBuilder reverse() {
		builder.reverse();
		return this;
	}

	@Override
	public ProteinSequenceBuilder ungap() {

		if(numberOfGaps==0) {
			return this;
		}
		GrowableIntArray gaps = new GrowableIntArray();
		builder.forEachIndexed((i, b)->{
//			if(AMBIGUOUS_AMINO_ACIDS.binarySearch(b) >=0) {
//				//is ambiguous
//			}
			if(b == GAP_ORDINAL) {
				gaps.append(i);
			}
		});
		gaps.reverse();
		gaps.forEachIndexed((i, offset)-> builder.remove(offset));
		
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
	
	 /**
     * Create a new array of all the {@link AminoAcid}s
     * in the current builder.
     * @return a new array, will never be null but might be empty.
     * @since 6.0
     */
	public AminoAcid[] toArray() {
		AminoAcid[] array = new AminoAcid[builder.getCurrentLength()];
		AminoAcid[] values = AminoAcid.values();
		builder.forEachIndexed((i, v)->{
			array[i]= values[v];
		});
		
		return array;
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

    @Override
    public ProteinSequenceBuilder turnOffDataCompression(boolean turnOffDataCompression) {
	    turnOffCompression = true;
	    return this;
    }
	public ProteinSequenceBuilder trimOffStopCodon(boolean trimOffStopCodon) {
		this.includeStopCodon = !trimOffStopCodon;
		return this;
	}
	@Override
	public ProteinSequenceBuilder getSelf() {
		return this;
	}
}
