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

import java.util.*;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.residue.DecodingOptions;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.util.IntList;
import org.jcvi.jillion.core.util.Offsets;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.spi.InvalidCharacterHandler;

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
	public static final Offsets.AddOptions GAP_ADD_AND_SHIFT_OPTIONS = Offsets.AddOptions.builder()
			.shift(true)
			.include(true).build();
	public static final Offsets.AddOptions AMB_ADD_OPTIONS = Offsets.AddOptions.builder()
			.include(true).build();

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
	private Offsets gapOffsets;
	private Offsets ambiguityOffsets;
	private boolean turnOffCompression=false;
	
	private boolean includeStopCodon = true;

	private DecodingOptions decodingOptions = DecodingOptions.DEFAULT;

	/**
	 * Sets the {@link DecodingOptions}
	 * used to help parse {@link AminoAcid}s from a String or char[].
	 * @param decodingOptions the options to use; if {@code null}
	 * use the default options which will throw an IllegalArgumentException on invalid characters.
	 *
	 * @since 6.1
	 */
	public ProteinSequenceBuilder setDecodingOptions(DecodingOptions decodingOptions) {
		this.decodingOptions = decodingOptions==null? DecodingOptions.DEFAULT: decodingOptions;
		return this;
	}
	/**
	 * Sets the {@link InvalidCharacterHandler}
	 * used to help parse {@link AminoAcid}s from a String or char[].
	 * @param invalidCharacterHandler the options to use; if {@code null}
	 * use the default options which will throw an IllegalArgumentException on invalid characters.
	 *
	 * @since 6.1
	 */
	public ProteinSequenceBuilder setInvalidCharacterHandler(InvalidCharacterHandler invalidCharacterHandler) {
		this.decodingOptions = invalidCharacterHandler==null? DecodingOptions.DEFAULT: DecodingOptions.builder()
				.invalidCharacterHandler(invalidCharacterHandler)
				.build();
		return this;
	}

	/**
     * Creates a new ProteinSequenceBuilder instance
     * which currently contains no amino acids.
     */
	public ProteinSequenceBuilder(){
		this((DecodingOptions) null);
	}

	@Override
	public ProteinSequenceBuilder replaceWithGaps(Range range, int numberOfGaps) {
		if(numberOfGaps <0){
			throw new IllegalArgumentException("number of gaps can not be negative");
		}
		if(numberOfGaps==0){
			return delete(range);
		}
		AminoAcid[] gaps = new AminoAcid[numberOfGaps];
		Arrays.fill(gaps, AminoAcid.Gap);
		return replace(range, gaps);
	}

	/**
	 * Creates a new ProteinSequenceBuilder instance
	 * which currently contains no amino acids.
	 *
	 * @param decodingOptions the {@link DecodingOptions} to use;
	 *                        if set to {@code null}, then the default will be used which
	 *                        will throw an error for any invalid characters.
	 *
	 * @since 6.1
	 */
	public ProteinSequenceBuilder(DecodingOptions decodingOptions){
		this(DEFAULT_CAPACITY);

		this.setDecodingOptions(decodingOptions);
	}
	/**
	 * Creates a new ProteinSequenceBuilder instance
	 * which currently contains no amino acids.
	 *
	 * @param invalidCharacterHandler the {@link InvalidCharacterHandler} to use;
	 *                        if set to {@code null}, then the default will be used which
	 *                        will throw an error for any invalid characters.
	 *
	 * @since 6.1
	 */
	public ProteinSequenceBuilder(InvalidCharacterHandler invalidCharacterHandler){
		this(DEFAULT_CAPACITY);
		this.setInvalidCharacterHandler(invalidCharacterHandler);
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
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
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
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
		append(parse(sequence.toString()));
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
	 *
	 * @param invalidCharacterHandler the {@link InvalidCharacterHandler} to use;
	 *                        if set to {@code null}, then the default will be used which
	 *                        will throw an error for any invalid characters.
	 *
	 * @since 6.1
	 */
	public ProteinSequenceBuilder(CharSequence sequence, InvalidCharacterHandler invalidCharacterHandler){
		builder = new GrowableByteArray(sequence.length());
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
		setInvalidCharacterHandler(invalidCharacterHandler);
		append(parse(sequence.toString()));
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
	 *
	 * @param decodingOptions the {@link DecodingOptions} to use;
	 *                        if set to {@code null}, then the default will be used which
	 *                        will throw an error for any invalid characters.
	 *
	 * @since 6.1
	 */
	public ProteinSequenceBuilder(CharSequence sequence, DecodingOptions decodingOptions){
		builder = new GrowableByteArray(sequence.length());
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
		setDecodingOptions(decodingOptions);
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
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
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
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
        Iterator<AminoAcid> iter = sequence.iterator(range);
        while(iter.hasNext()){
            append(iter.next());
        }
    }
	public ProteinSequenceBuilder(ProteinSequence sequence, List<Range> ranges) {
		builder = new GrowableByteArray();
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
		for(Range r: ranges){
			append(sequence.toBuilder(r));
		}

	}
	private ProteinSequenceBuilder(ProteinSequenceBuilder copy){
		builder = copy.builder.copy();
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
		updateMetaData();
	}
	
	private ProteinSequenceBuilder(GrowableByteArray growableArray) {
		this.builder = growableArray;
		gapOffsets = Offsets.withInitialCapacity(5);
		ambiguityOffsets = Offsets.withInitialCapacity(1);
		updateMetaData();
	}
	/**
	 * Update the metadata of number of gaps and ambiguities.
	 */
	private void updateMetaData() {
		GrowableIntArray gapList = new GrowableIntArray();
		GrowableIntArray ambiguityList = new GrowableIntArray();

		builder.forEachIndexed((i, ordinal)->{
			if(ordinal == GAP_ORDINAL){
				gapList.append(i);
			}else if(AMBIGUOUS_AMINO_ACIDS.binarySearch(ordinal)>=0) {
				ambiguityList.append(i);
			}
		});
		this.gapOffsets = Offsets.fromSortedList(gapList.toBoxedList());
		this.ambiguityOffsets = Offsets.fromSortedList(ambiguityList.toBoxedList());
	}

	@Override
	public ProteinSequenceBuilder appendGap() {
		return append(AminoAcid.Gap);
	}

	private List<AminoAcid> parse(String aminoAcids){
		InvalidCharacterHandler invalidCharacterHandler = decodingOptions.getInvalidCharacterHandler();
		List<AminoAcid> result = new ArrayList<>(aminoAcids.length());
        for(int i=0; i<aminoAcids.length(); i++){
            char charAt = aminoAcids.charAt(i);
            if(!Character.isWhitespace(charAt)){
				AminoAcid aa = AminoAcid.parse(charAt, invalidCharacterHandler);
				if(aa!=null){
					result.add(aa);
				}
            }
        }
        return result;
	}


	@Override
	public Range toUngappedRange(Range gappedRange) {
		Objects.requireNonNull(gappedRange);
		long gappedBegin = gappedRange.getBegin();
		long gappedEnd = gappedRange.getEnd();

		long currentLength = getLength();
		if(gappedBegin >= currentLength || gappedEnd >= currentLength){
			throw new IndexOutOfBoundsException("gapped Range of " + gappedRange +" is beyond the gapped sequence length of " + currentLength);
		}


		if(gapOffsets.isEmpty()){
			//no gaps
			return gappedRange;
		}

		long ungappedStart = gappedBegin - gapOffsets.computeInsertionPointOf((int)gappedBegin);
		long ungappedEnd = gappedEnd - gapOffsets.computeInsertionPointOf((int)gappedEnd);

		return Range.of(ungappedStart, ungappedEnd);
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
			gapOffsets.add((int) getLength());
		}else if(residue.isAmbiguity()) {
			ambiguityOffsets.add((int) getLength());
		}
		builder.append(residue.getOrdinalAsByte());
		return this;
	}

	
	@Override
	public ProteinSequenceBuilder clear() {
		gapOffsets.clear();
		ambiguityOffsets.clear();
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
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the protein sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
	@Override
	public ProteinSequenceBuilder append(AminoAcid[] sequence) {
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
		Offsets insertedOffsets = Offsets.withInitialCapacity(5);
		Offsets insertedAmbiguities = Offsets.withInitialCapacity(5);

		for(AminoAcid aa :list){
			if(aa == AminoAcid.Gap){
				insertedOffsets.add(i);
			}else if(aa.isAmbiguity()) {
				insertedAmbiguities.add(i);
			}
			array[i]=(aa.getOrdinalAsByte());
			i++;
		}		
		builder.insert(offset, array);
		gapOffsets.add(insertedOffsets, GAP_ADD_AND_SHIFT_OPTIONS);
		ambiguityOffsets.add(insertedAmbiguities, GAP_ADD_AND_SHIFT_OPTIONS);
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
		return builder.getCurrentLength() - gapOffsets.size();
	}
	
	@Override
	public ProteinSequenceBuilder replace(
			int offset, AminoAcid replacement) {
		if(builder.getCurrentLength() == offset){
			return append(replacement);
		}
		byte v = builder.get(offset);
		boolean handledGap=false;
		boolean handledAmb=false;
		if(AMINO_ACID_VALUES[v] == AminoAcid.Gap){
			if(replacement == AminoAcid.Gap){
				//don't need to touch gap offsets
				handledGap=true;
			}else{
				gapOffsets.removeAndShift(offset);
			}
		}else if(AMBIGUOUS_AMINO_ACIDS.binarySearch(v) >=0) {
			if(replacement.isAmbiguity()){
				//don't need to touch ambiguity count
				handledAmb=true;
			}else{
				ambiguityOffsets.remove(offset);
			}
		}
		if(!handledGap && replacement == AminoAcid.Gap){
			gapOffsets.add(offset);
		}else if(!handledAmb && replacement.isAmbiguity()) {
			ambiguityOffsets.add(offset);
		}
		builder.replace(offset, replacement.getOrdinalAsByte());
		return this;
	}
	
	

	@Override
	public ProteinSequenceBuilder replace(Range range, AminoAcid[] replacement) {
		return _replace(range, Arrays.asList(replacement) );
	}
	@Override
	public ProteinSequenceBuilder replace(Range range, ProteinSequence replacement) {
		return _replace(range, replacement );

	}
	@Override
	public ProteinSequenceBuilder replace(Range range, Iterable<AminoAcid> replacement) {
		return _replace(range, replacement );

	}

	@Override
	public boolean isGap(int offset) {
		return gapOffsets.contains(offset);
	}

	private ProteinSequenceBuilder _replace(Range range, Iterable<AminoAcid> replacement) {


		if(range.getBegin() == builder.getCurrentLength()){
			return append(replacement);
		}
		delete(range);
		insert((int)range.getBegin(), replacement);

		return this;
	}
	@Override
	public ProteinSequenceBuilder replace(Range range, ProteinSequenceBuilder replacement) {
		return _replace(range, replacement );
	}
	@Override
	public ProteinSequenceBuilder delete(
			Range range) {
		builder.remove(range);
		gapOffsets.delete(range);
		ambiguityOffsets.remove(range);
		return this;
	}

	@Override
	public int getNumGaps() {
		return gapOffsets.size();
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
		Offsets gapList = Offsets.withInitialCapacity(5);
		Offsets ambList = Offsets.withInitialCapacity(5);
		int j=0;
		for(AminoAcid aa :sequence){
			if(aa == AminoAcid.Gap){
				gapList.add(j);
			}else if(aa.isAmbiguity()) {
				ambList.add(j);
			}
			temp.append(aa.getOrdinalAsByte());
			j++;
		}		
		builder.insert(offset, temp);
		gapOffsets.insertAndShift(gapList, j, offset);
		ambList.add(ambList, AMB_ADD_OPTIONS);
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
			gapOffsets.addAndShift(offset);
		}else if(base.isAmbiguity()) {
			ambiguityOffsets.add(offset);
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
		return new CodecDecider(getNumGaps(), !ambiguityOffsets.isEmpty(), turnOffCompression)
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
	            if (numberOfGaps > 0 && (!doubleCheck || hasGaps(asList))) {
	            	if(hasAmbiguities && (!doubleCheck || hasAmbiguities(asList))) {
	            		return new UnCompressedGappedProteinSequence(asList);
	            	}
	                return new UnCompressedGappedNoAmbiguityProteinSequence(asList);
	            }
	            if(hasAmbiguities && (!doubleCheck || hasAmbiguities(asList))) {
	            	return new UnCompressedUngappedProteinSequence(asList);
	            }
	            
	            return new UnCompressedUnGappedNoAmbiguityProteinSequence(asList);
	        }else {
	            if (numberOfGaps > 0 && (!doubleCheck || hasGaps(asList))) {
	            	if(hasAmbiguities && (!doubleCheck || hasAmbiguities(asList))) {
	            		return new CompactProteinSequence(asList);
	            	}
	                return new GappedNoAmbiguityProteinSequence(asList);
	            }
	            //no gaps
	            if(hasAmbiguities && (!doubleCheck || hasAmbiguities(asList))) {

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
		int newBegin = (int) range.getBegin();
		this.gapOffsets =gapOffsets.intersection(intersection);
		this.gapOffsets.replaceAll( v-> v - newBegin);

		ambiguityOffsets = ambiguityOffsets.complement(range);

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

		if(getNumGaps()==0) {
			return this;
		}
		ambiguityOffsets.ungap(gapOffsets);

		gapOffsets.forEachReversed( builder::remove);
		
		this.gapOffsets.clear();
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

	@Override
	public List<Range> getRangesOfGaps() {
		return Ranges.asRanges(gapOffsets.toArray());
	}

	@Override
	public IntList getGapOffsets() {
		return gapOffsets.asList();
	}
}
