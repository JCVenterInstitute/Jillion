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
package org.jcvi.jillion.core.residue.nt;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandlers;
import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableIterator;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
public final class NucleotideSequenceBuilder implements INucleotideSequenceBuilder<NucleotideSequence, NucleotideSequenceBuilder>{
	/**
	 * Initial buffer size is {@value} which should
	 * be enough for most next-gen reads that are seen.
	 * This should greatly reduce the number of resizes we need to do.
	 */
	private static final int INITITAL_BUFFER_SIZE =200;
	
    private static final String NULL_SEQUENCE_ERROR_MSG = "sequence can not be null";
	private static final byte GAP_VALUE = Nucleotide.Gap.getOrdinalAsByte();
    private static final byte N_VALUE = Nucleotide.Unknown.getOrdinalAsByte();
    private static final byte A_VALUE = Nucleotide.Adenine.getOrdinalAsByte();
    private static final byte C_VALUE = Nucleotide.Cytosine.getOrdinalAsByte();
    private static final byte G_VALUE = Nucleotide.Guanine.getOrdinalAsByte();
    private static final byte T_VALUE = Nucleotide.Thymine.getOrdinalAsByte();
   
    /**
     * Options for how to decode Nucleotide's from Strings/characters into {@link Nucleotide}
     * objects.  This object handles invalid characters via and
     * additional options such as converting ambigious bases all to Ns.
     * Create using the {@link #builder()} and {@link #toBuilder()}
     * methods.
     * 
     * @author dkatzel
     * @since 6.0
     *
     * see {@link DecodingOptionsBuilder}
     */
    @Builder(toBuilder = true)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DecodingOptions{
    	
    	public static DecodingOptions DEFAULT =  DecodingOptions.builder().build();
    	@NonNull
    	private final Nucleotide.InvalidCharacterHandler invalidCharacterHandler;
    	@NonNull
    	@Getter(value = AccessLevel.PRIVATE)
    	private final NewValuesFactory newValuesFactory;
    	
    	
    	protected NewValues create(String sequence) {
    		return newValuesFactory.create(sequence, invalidCharacterHandler);
    	}
    	protected NewValues create(char[] sequence) {
    		return newValuesFactory.create(sequence, invalidCharacterHandler);
    	}
    	protected NewValues create(NucleotideSequence sequence) {
    		return newValuesFactory.create(sequence);
    	}
    	protected NewValues create(Iterable<Nucleotide> sequence) {
    		return newValuesFactory.create(sequence);
    	}
    	protected NewValues create(Iterator<Nucleotide> sequence) {
    		return newValuesFactory.create(sequence);
    	}
    	/**
    	 * Builder class to create new {@link DecodingOptions}.
    	 * @author dkatzel
    	 * 
    	 * @since 6.0
    	 *
    	 */
    	public static class DecodingOptionsBuilder{
    		private Nucleotide.InvalidCharacterHandler invalidCharacterHandler = DEFAULT_INVALD_CHAR_HANDLER;
    		
        	private NewValuesFactory newValuesFactory = DefaultNewValuesFactory.INSTANCE;
        	/**
        	 * Set the {@link org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler}.
        	 * @param invalidCharacterHandler the handler to use; if set to {@code null},
        	 * then the default handler is used.
        	 * @return this
        	 */
        	public DecodingOptionsBuilder invalidCharacterHandler(Nucleotide.InvalidCharacterHandler invalidCharacterHandler) {
        		if(invalidCharacterHandler==null) {
        			this.invalidCharacterHandler = DEFAULT_INVALD_CHAR_HANDLER;
        		}else{
        			this.invalidCharacterHandler = invalidCharacterHandler;
        		}
        		return this;
        	}
        	/**
             * Replace all ambiguous bases with Ns.  This only affects
             * downstream calls to append/prepend/insert, previously
             * added bases are not changed.
             *  
             * @param replaceAllAmbigutiesWithNs {@code true} if any future encountered
             * ambiguities should be changed to Ns, {@code false} otherwise.
             * 
             * @return this
             */
            public DecodingOptionsBuilder replaceAllAmbiguitiesWithNs(boolean replaceAllAmbigutiesWithNs) {
            	if(replaceAllAmbigutiesWithNs) {
            		newValuesFactory = AdjustedNewValuesFactory.INSTANCE;
            	}else {
            		newValuesFactory = DefaultNewValuesFactory.INSTANCE;
            	}
            	return this;
            }
        	
        	
    	}
    }
    /**
     * handler for invalid chars if set.
     */
    private static Nucleotide.InvalidCharacterHandler DEFAULT_INVALD_CHAR_HANDLER = InvalidCharacterHandlers.ERROR_OUT;

    
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
     * handler for invalid chars if set.
     */
    private DecodingOptions decodingOptions = DecodingOptions.DEFAULT;
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     */
    public NucleotideSequenceBuilder(){
        this(INITITAL_BUFFER_SIZE);
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains no nucleotides.
     * @param InvalidCharacterHandler an {@link org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler}
     * for how to handle parsing invalid nucleotide characters, set to {@code null}, then use the default handler
     * which will throw an IllegalArgumentException.
     * 
     * @since 6.0
     */
    public NucleotideSequenceBuilder(Nucleotide.InvalidCharacterHandler invalidCharacterHandler){
        this(INITITAL_BUFFER_SIZE);
        _setInvalidCharacterHandler(invalidCharacterHandler);
    }
    /**
     * Sets the {@link org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler}
     * used to help parse {@link Nucleotide}s from a String or char[].
     * @param invalidCharacterHandler the handler to use; if {@code null}
     * use the default handler which will throw an IllegalArgumentException on invalid characters.
     * 
     * @since 6.0
     */
    @Override
    public NucleotideSequenceBuilder setInvalidCharacterHandler(Nucleotide.InvalidCharacterHandler invalidCharacterHandler) {
    	_setInvalidCharacterHandler(invalidCharacterHandler==null? DEFAULT_INVALD_CHAR_HANDLER: invalidCharacterHandler);
    	return this;
    }
    private void _setInvalidCharacterHandler(Nucleotide.InvalidCharacterHandler invalidCharacterHandler) {
    	if(invalidCharacterHandler !=null) {
    		this.decodingOptions = this.decodingOptions.toBuilder().invalidCharacterHandler(invalidCharacterHandler).build();
    	}
    }
    
    @Override
    public NucleotideSequenceBuilder setDecodingOptions(DecodingOptions decodingOptions) {
    	this.decodingOptions = decodingOptions==null? DecodingOptions.DEFAULT: decodingOptions;
    	return this;
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
     * @throws IllegalArgumentException if initialCapacity &lt; 1.
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
        NewValues newValues = decodingOptions.create(sequence);
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
        NewValues newValues = decodingOptions.newValuesFactory.create(sequence);
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
		this(sequence, DecodingOptions.DEFAULT);
    }
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     *  Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param sequence the initial nucleotide sequence.
     *  @param InvalidCharacterHandler an {@link org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler}
     * for how to handle parsing invalid nucleotide characters, set to {@code null}, then use the default handler
     * which will throw an IllegalArgumentException.
     * 
     * @since 6.0
     * 
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link Nucleotide}.
     */
    public NucleotideSequenceBuilder(String sequence, Nucleotide.InvalidCharacterHandler invalidCharacterHandler){
		this(sequence, DecodingOptions.builder().invalidCharacterHandler(invalidCharacterHandler).build());
		
	}
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the given sequence.
     *  Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param sequence the initial nucleotide sequence.
     *  @param InvalidCharacterHandler an {@link org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler}
     * for how to handle parsing invalid nucleotide characters, set to {@code null}, then use the default handler
     * which will throw an IllegalArgumentException.
     * 
     * @since 6.0
     * 
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link Nucleotide}.
     */
    public NucleotideSequenceBuilder(String sequence,DecodingOptions decodingOptions){
		if (sequence == null) {
			throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
		}
		this.decodingOptions= decodingOptions==null? DecodingOptions.DEFAULT: decodingOptions;
		NewValues newValues = this.decodingOptions.create(sequence);
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
		this(sequence, DecodingOptions.DEFAULT);
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
    public NucleotideSequenceBuilder(char[] sequence, DecodingOptions decodingOptions){
		if (sequence == null) {
			throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
		}
		this.decodingOptions = Objects.requireNonNull(decodingOptions);
		NewValues newValues = decodingOptions.create(sequence);
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
		NewValues newValues = decodingOptions.newValuesFactory.create(singleNucleotide);
		this.data = newValues.getData();
		codecDecider = new CodecDecider(newValues);
	}

    
    private NucleotideSequenceBuilder(NucleotideSequenceBuilder copy){    	
        this.data = copy.data.copy();
        this.codecDecider = copy.codecDecider.copy();
        this.decodingOptions = copy.decodingOptions;
    }
    private NucleotideSequenceBuilder(GrowableByteArray data, DecodingOptions decodingOptions){
    	this.data = data;
    	NewValues newValues = decodingOptions.newValuesFactory.create(data);
    	this.codecDecider = new CodecDecider(newValues);
    	this.decodingOptions = decodingOptions;
    }
    
    /**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the only the portion
     * of the given sequence within the specified {@link Range}.
     * 
     * @apiNote This should be a more efficient way of achieving
     *  <pre>
     *  {@code new NucleotideSequenceBuilder(seq)
     *  			.trim(range)}
     *  </pre>
     * 
     * @param seq the initial nucleotide sequence.
     * @param range the range of the sequence to use; can not be null.
     * @throws NullPointerException if sequence is null.
     * @throws IndexOutOfBoundsException if Range contains values outside of the possible sequence offsets.
     */
	public NucleotideSequenceBuilder(NucleotideSequence seq, Range range) {
		NewValues newValues = decodingOptions.newValuesFactory.create(seq.iterator(range), (int) range.getLength());
		this.data = newValues.getData();
		codecDecider = new CodecDecider(newValues);
	}

	/**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the only the portion(s)
     * of the given sequence within the specified {@link Range}s.
     * 
     * @apiNote This should produce the same result as but more efficient than: 
     *  <pre>
     *  {@code NucleotideSequenceBuilder builder= new NucleotideSequenceBuilder();
     *  for(Range r : ranges){
     *      builder.append(seq.trim(r));
     *  }}
     *  </pre>
     * 
     * @param seq the initial nucleotide sequence.
     * @param ranges the ranges of the sequence to use; can not be null or contain any nulls.
     * @throws NullPointerException if sequence or ranges are null.
     * @throws IndexOutOfBoundsException if Range contains values outside of the possible sequence offsets.
     * 
     * @since 6.0
     */
	public NucleotideSequenceBuilder(NucleotideSequence seq, Range... ranges) {
		Range firstRange = ranges[0];
		NewValues newValues = decodingOptions.newValuesFactory.create(seq.iterator(firstRange), (int) firstRange.getLength());
		this.data = newValues.getData();
		codecDecider = new CodecDecider(newValues);
		for(int i=1; i< ranges.length; i++) {
			Range r = ranges[i];
			append(decodingOptions.newValuesFactory.create(seq.iterator(r), (int) r.getLength()));
		}
		
	}
	/**
     * Creates a new NucleotideSequenceBuilder instance
     * which currently contains the only the portion(s)
     * of the given sequence within the specified {@link Range}s.
     * 
     * @apiNote This should produce the same result as but more efficient than: 
     *  <pre>
     *  {@code NucleotideSequenceBuilder builder= new NucleotideSequenceBuilder();
     *  for(Range r : ranges){
     *      builder.append(seq.trim(r));
     *  }}
     *  </pre>
     * 
     * @param seq the initial nucleotide sequence.
     * @param ranges the ranges of the sequence to use; can not be null or contain any nulls.
     * @throws NullPointerException if sequence or ranges are null.
     * @throws IndexOutOfBoundsException if Range contains values outside of the possible sequence offsets.
     * 
     * @since 6.0
     */
	public NucleotideSequenceBuilder(NucleotideSequence seq, Iterable<Range> ranges) {
		Objects.requireNonNull(seq);
		
		Iterator<Range> iter = ranges.iterator();
		if(!iter.hasNext()) {
			//empty ?
			this.data = new GrowableByteArray(INITITAL_BUFFER_SIZE);
			codecDecider = new CodecDecider();
			return; 
		}
		Range firstRange = iter.next();
		NewValues newValues = decodingOptions.newValuesFactory.create(seq.iterator(firstRange), (int) firstRange.getLength());
		this.data = newValues.getData();
		codecDecider = new CodecDecider(newValues);
		while(iter.hasNext()) {
			Range r = iter.next();
			append(decodingOptions.newValuesFactory.create(seq.iterator(r), (int) r.getLength()));
		}
		
	}

	/**
     * Appends the given base to the end
     * of the builder's mutable sequence.
     * @param base a single nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if base is null.
     * 
     * @return this.
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
     * 
     * @return this.
     * 
     * @throws NullPointerException if sequence is null.
     */
    public NucleotideSequenceBuilder append(Iterable<Nucleotide> sequence){
        assertNotNull(sequence);
        NewValues newValues = decodingOptions.newValuesFactory.create(sequence);
        return append(newValues);
    }
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     * 
     * @return this.
     */
    @Override
    public NucleotideSequenceBuilder append(NucleotideSequence sequence){
        assertNotNull(sequence);
        NewValues newValues = decodingOptions.newValuesFactory.create(sequence);
        return append(newValues);
    }
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @param range the Range of the sequence to append.
     * @throws NullPointerException if sequence is null.
     * 
     * @return this.
     * 
     * @since 6.0
     * 
     * @implNote This should be the same but more efficient as {@link #append(NucleotideSequence) append(sequence.trim(range))}.
     * 
     */
    @Override
    public NucleotideSequenceBuilder append(NucleotideSequence sequence, Range range){
        assertNotNull(sequence);
        assertNotNull(range);
        NewValues newValues = decodingOptions.newValuesFactory.create(sequence.iterator(range), (int) range.getLength());
        return append(newValues);
    }
	private NucleotideSequenceBuilder append(NewValues newValues) {
		//this will force the array to 
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
     * 
     * @return this.
     * 
     * @throws NullPointerException if otherBuilder is null.
     * @throws IllegalArgumentException if otherBuilder is not a NucleotideSequenceBuilder.
     */
	@Override
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
	@Override
    public NucleotideSequenceBuilder append(String sequence){
    	if(sequence ==null){
    		throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
    	}
        return append(decodingOptions.create(sequence));
    }
    
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * 
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder; any '\0' characters are ignored.
     * 
     * @return this.
     * 
     * @throws NullPointerException if sequence is null.
     */
    @Override
    public NucleotideSequenceBuilder append(char[] sequence){
    	if(sequence ==null){
    		throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
    	}
        return append(decodingOptions.create(sequence));
    }
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * 
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder; any nulls are ignored.
     * 
     * @return this.
     * 
     * @throws NullPointerException if sequence is null.
     * 
     * @since 5.3
     */
    @Override
    public NucleotideSequenceBuilder append(Nucleotide[] sequence){
        if(sequence ==null){
                throw new NullPointerException(NULL_SEQUENCE_ERROR_MSG);
        }
        return append(decodingOptions.newValuesFactory.create(sequence));
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
     * If the offset = the current length then this insertion
     * is treated as an append.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    @Override
    public NucleotideSequenceBuilder insert(int offset, String sequence){
    	assertInsertionParametersValid(offset, sequence);
    	return insert(offset, decodingOptions.create(sequence));
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
     * If the offset = the current length then this insertion
     * is treated as an append.
     * 
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * 
     * @return this.
     * 
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    @Override
	public NucleotideSequenceBuilder insert(int offset, char[] sequence){
    	assertInsertionParametersValid(offset, sequence);
		return insert(offset, decodingOptions.create(sequence));
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
     * 
     * @return this.
     * 
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
     * 
     * @return this.
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
     * 
     * @return this.
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
     * 
     * @return this.
     */
	public NucleotideSequenceBuilder replace(Range gappedRangeToBeReplaced, NucleotideSequence replacementSeq) {
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
     * @param replacementSeq the array of Nucleotides use in this range; any nulls are ignored.
     * 
     * @return this.
     * @since 6.0
     */
	public NucleotideSequenceBuilder replace(Range gappedRangeToBeReplaced, Nucleotide[] replacementSeq) {
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
	@Override
    public NucleotideSequenceBuilder delete(Range range){
        if(range ==null){
            throw new NullPointerException("range can not be null");
        }
        if(!range.isEmpty()){
        	Range rangeToDelete = Range.of(Math.max(0, range.getBegin()),
        			Math.min(data.getCurrentLength()-1, range.getEnd()));
        	GrowableByteArray deletedBytes = data.subArray(rangeToDelete);
        	
            NewValues newValues = decodingOptions.newValuesFactory.create(deletedBytes);
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
		return Nucleotide.getByOrdinal(data.get(offset));
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
     * {@link #insert(int, Nucleotide) insert(0,n)}
     * @param n the nucleotide to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if n is null.
     * @see #insert(int, Nucleotide)
     * @since 6.0
     */
    public NucleotideSequenceBuilder prepend(Nucleotide n){
        return insert(0, n);
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
     * @throws IllegalArgumentException if offset &lt; 0 or &gt; current sequence length.
     */
    public NucleotideSequenceBuilder insert(int offset, Iterable<Nucleotide> sequence){
        assertInsertionParametersValid(offset, sequence);   
        NewValues newValues = decodingOptions.newValuesFactory.create(sequence);
        return insert(offset, newValues);
    }
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * 
     * @param offset the <strong>gapped</strong> offset into this mutable sequence
     * to begin insertion.  If the offset = the current length then this insertion
     * is treated as an append.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset; any nulls are ignored.
     * 
     * @return this
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset &lt; 0 or &gt; current sequence length.
     * 
     * @since 5.3
     */
    @Override
	public NucleotideSequenceBuilder insert(int offset, Nucleotide[] sequence) {
        assertInsertionParametersValid(offset, sequence);   
        NewValues newValues = decodingOptions.newValuesFactory.create(sequence);
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
     * @throws IllegalArgumentException if offset &lt; 0 or &gt; current sequence length.
     */
    @Override
	public NucleotideSequenceBuilder insert(int offset, NucleotideSequence sequence){
        assertInsertionParametersValid(offset, sequence);   
        NewValues newValues = decodingOptions.newValuesFactory.create(sequence);
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
     * as {@code this.insert(offset, otherBuilder.build())}.
     * If the offset = the current length then this insertion
     * is treated as an append.
     * 
     * @param offset the <strong>gapped</strong> offset into this mutable sequence
     * to begin insertion.
     * @param otherBuilder the {@link NucleotideSequenceBuilder} whose current
     * nucleotides are to be inserted at the given offset.
     * @return this
     * @throws NullPointerException if otherBuilder is null.
     * @throws IllegalArgumentException if offset &lt; 0 or &gt; current sequence length or if otherBuilder is not a NucleotideSequenceBuilder.
     */
    @Override
	public NucleotideSequenceBuilder insert(int offset, NucleotideSequenceBuilder otherBuilder){
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
        NucleotideSequenceBuilder otherSequenceBuilder = otherBuilder;
        NewValues newValues = decodingOptions.newValuesFactory.create(otherSequenceBuilder);
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
     * If the offset = the current length then this insertion
     * is treated as an append.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param base the {@link Nucleotide} to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if base is null.
     * @throws IllegalArgumentException if offset &lt; 0 or &gt; current sequence length.
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
    @Override
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
    public NucleotideSequenceBuilder prepend(NucleotideSequenceBuilder otherBuilder){
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
        
    		return codecDecider.decide(this);

    }

    /**
     * Replace all Uracils in this current sequence
     * into Thymines.
     *
     * @return this.
     *
     * @since 5.3
     */
    public NucleotideSequenceBuilder convertToDna(){
        if(codecDecider.numUs ==0){
            return this;
        }
        codecDecider.numTs +=codecDecider.numUs;
        codecDecider.numUs=0;
        this.data.forEachIndexed((i, v)->{
            if(v == Nucleotide.Uracil.getOrdinalAsByte()){
                this.data.replace(i, Nucleotide.Thymine.getOrdinalAsByte());
            }
        });
        return this;
    }
    /**
     * Replace all Thymines in this current sequence
     * into Uracils.
     *
     * @return this.
     *
     * @since 5.3
     */
    public NucleotideSequenceBuilder convertToRna(){
        if(codecDecider.numTs ==0){
            return this;
        }
        codecDecider.numUs +=codecDecider.numTs;
        codecDecider.numTs=0;
        this.data.forEachIndexed((i, v)->{
            if(v == Nucleotide.Thymine.getOrdinalAsByte()){
                this.data.replace(i, Nucleotide.Uracil.getOrdinalAsByte());
            }
        });
        return this;
    }

    private Iterator<Nucleotide> iterator(boolean convertRnaToDna) {
        if (convertRnaToDna) {
            return new Iterator<Nucleotide>() {
                int currentOffset = 0;
                int length = data.getCurrentLength();


                @Override
                public boolean hasNext() {
                    return currentOffset < length;
                }

                @Override
                public Nucleotide next() {
                    Nucleotide n = Nucleotide.getByOrdinal(data.get(currentOffset++));

                    if (Nucleotide.Uracil == n) {
                        return Nucleotide.Thymine;
                    }
                    return n;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();

                }
            };
        }
        return new Iterator<Nucleotide>() {
            int currentOffset = 0;
            int length = data.getCurrentLength();


            @Override
            public boolean hasNext() {
                return currentOffset < length;
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
    @Override
    public Iterator<Nucleotide> iterator() {
       return iterator(false);
    	
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
     * <br>
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
     * that aligns to this sequence being built with only one SNP ({@code T ->N} )
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
     * @throws IllegalArgumentException if gappedStartOffset is &lt; 0 or beyond the reference.
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
    	NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(data.subArray(trimRange), decodingOptions);
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
		return new NucleotideSequenceBuilder(data.subArray(gappedRange), decodingOptions);
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
		Iterator<Nucleotide> iter = iterator(false);
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
	    	Iterator<Nucleotide> iter = iterator(false);
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
    	Iterator<Nucleotide> iter = iterator();
    	while(iter.hasNext()){
    	    builder.append(iter.next());
        }
        return builder.toString();
    }
    /**
     * Reverse complement all the nucleotides currently in this builder.
     * Calling this method will only reverse complement bases that 
     * already exist in this builder; any additional operations
     * to insert bases will not be affected.
     * <p>
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
            
            Nucleotide tmp = Nucleotide.getDnaValues().get(bytes[i]).complement();
           
            byte complementOrdinal = Nucleotide.getDnaValues().get(bytes[compOffset]).complement().getOrdinalAsByte();
            bytes[i] = complementOrdinal;
            bytes[compOffset] = tmp.getOrdinalAsByte();
        }
        if(currentLength%2!=0){
        	bytes[pivotOffset] = Nucleotide.getDnaValues().get(bytes[pivotOffset]).complement().getOrdinalAsByte();
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
     * <p>
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
        	complementedData[i]=Nucleotide.getDnaValues().get(originalData[i]).complement().getOrdinalAsByte();
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
    @Override
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
     * Remove "islands" of gaps that are currently present that match the given filter function.  
     * 
     *
     * @param gapFilter a {@link Predicate} that takes in a Range whose start is the <strong>gapped</strong> start of a region of consecutive gaps and whose length
     * is the number of consecutive gaps.  If the Function returns {@code true} then that gap range should be removed from this sequence;
     * if the function returns {@code false}, then the gaps should remain in the sequence. 
     * Note that single gaps are still passed to the function as ranges of length 1.
     * 
     * @return this.
     * @since 6.0
     * @throws NullPointerException if gapFilter is null.
     * 
     * @implNote To reduce complexity and improve efficiency, gaps are only removed
     * after the entire sequence has been analyzed to avoid having to adjust coordinates
     * given to the predicate. 
     * 
     * @apiNote For example, to remove all gaps but keep gaps that stay in frame use a gapFilter 
     * of {@code r-> r.getLength()% 3 !=0} which will make this test below pass:
     * <pre>
     *{@code assertEquals("ACGTACGTGTT---GTGTG------GT",
     *   new NucleotideSequenceBuilder("ACGT-ACGT--GTT---GTGTG------G-T")
		.ungap(r-> r.getLength()% 3 !=0)
		.toString());
     *  </pre>
     */
    public NucleotideSequenceBuilder ungap(Predicate<Range> gapFilter){
    	Objects.requireNonNull(gapFilter);
    	
    	final int numGaps = codecDecider.getNumberOfGaps();
		// if we have no gaps then we can short circuit
		// and do nothing
		if (numGaps == 0) {
			return this;
		}
		
		
		List<Range> deleteList = new ArrayList<>(); 
		for(Range r : Ranges.asRanges(codecDecider.gapOffsets.toArray())){
			if(gapFilter.test(r)) {
				deleteList.add(r);
			}
		}
		//now we have found all the ranges of gaps to delete
		//easier to remove each range starting at the end so we don't have to adjust any coords
		Collections.reverse(deleteList);
		for(Range r: deleteList) {
			this.delete(r);
		}
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
    /**
     * Get the corresponding gapped Range (where the start and end values
     * of the range are in gapped coordinate space) for the given
     * ungapped {@link Range}.
     * @param ungappedRange the Range of ungapped coordinates; can not be null.
     * @return a new Range never null.
     * @throws NullPointerException if the gappedRange is null.
     * 
     * 
     */
    @Override
    public Range toGappedRange(Range ungappedRange) {
    	int ungappedStart = (int)ungappedRange.getBegin();
    	int ungappedEnd = (int)ungappedRange.getEnd();
    	return Range.of(getGappedOffsetFor(ungappedStart),
    			getGappedOffsetFor(ungappedEnd)
    			); 
	}
    /**
     * Get the corresponding ungapped Range (where the start and end values
     * of the range are in ungapped coordinate space) for the given
     * gapped {@link Range}.
     * @param gappedRange the Range of gapped coordinates; can not be null.
     * @return a new Range never null.
     * @throws NullPointerException if the gappedRange is null.
     * @throws IndexOutOfBoundsException if the given Range goes beyond
     * the gapped sequence.
     * 
     * @since 5.2
     */
    @Override
    public Range toUngappedRange(Range gappedRange) {
        Objects.requireNonNull(gappedRange);
        long gappedBegin = gappedRange.getBegin();
        long gappedEnd = gappedRange.getEnd();
        
        long currentLength = codecDecider.getCurrentLength();
        if(gappedBegin >= currentLength || gappedEnd >= currentLength){
            throw new IndexOutOfBoundsException("gapped Range of " + gappedRange +" is beyond the gapped sequence length of " + currentLength);
        }
        
        GrowableIntArray gaps = codecDecider.gapOffsets;
        if(gaps.getCurrentLength() == 0){
            //no gaps
            return gappedRange;
        }
        
        long ungappedStart = gappedBegin - numGapsUntil(gaps, (int)gappedBegin);
        long ungappedEnd = gappedEnd - numGapsUntil(gaps, (int)gappedEnd);
       
        return Range.of(ungappedStart, ungappedEnd); 
    }
    
    private int numGapsUntil(GrowableIntArray gaps, int gappedOffset){
        int insertionPoint = gaps.binarySearch(gappedOffset);
        if(insertionPoint >=0){
            //if we landed on a gap, then
            //the we want the length of the array
            //up until that offset so that's why it's +1
            return insertionPoint +1;
        }
        return -insertionPoint -1;
    }
    
    public int getGappedOffsetFor(int ungappedOffset){
    	SingleThreadAdder currentOffset = new SingleThreadAdder(ungappedOffset);
    	PrimitiveIterator.OfInt iter = codecDecider.gapOffsets.iterator();
    	while(iter.hasNext()) {
    		if(iter.nextInt() <= currentOffset.intValue()) {
    			currentOffset.increment();
    		}else {
    			break;
    		}
    	}
    	
    	return currentOffset.intValue();
    }

    public int getNumUs() {
        return codecDecider.numUs;
    }

    /**
     * Replace all ambiguous bases with Ns.  This only affects
     * downstream calls to append/prepend/insert, previously
     * added bases are not changed.
     *  
     * @param replaceAllAmbigutiesWithNs {@code true} if any future encountered
     * ambiguities should be changed to Ns, {@code false} otherwise.
     * 
     * @return this
     * @since 6.0
     */
    public NucleotideSequenceBuilder replaceAllAmbiguitiesWithNs(boolean replaceAllAmbigutiesWithNs) {
    	this.decodingOptions = decodingOptions.toBuilder().replaceAllAmbiguitiesWithNs(replaceAllAmbigutiesWithNs).build();
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
       // private int numberOfGaps=0;
        private int numberOfNonNAmbiguities=0;
        private int numUs=0;
        //we have to keep track of Ts AND Us because some weird sequences
        //used for medical research use oligos with both!!
        private int numTs=0;
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
        /**
         * Based on the current counts and metadata associated with the sequence
         * decide the best implementation to use.
         * @param nucleotideSequenceBuilder
         * @return
         */
        public NucleotideSequence decide(NucleotideSequenceBuilder nucleotideSequenceBuilder) {
        	if(hasAlignedReference()){
        		return new DefaultReferenceEncodedNucleotideSequence(
        				alignedReference.reference, nucleotideSequenceBuilder, alignedReference.offset);
        	
        	}
        	if((numUs >0 && numTs >0)){ 
                return new SimpleNucleotideSequence(nucleotideSequenceBuilder.data.copy());

        	}
        	//force Us to go through encoding
        	if(numUs ==0 && forceBasicCodec) {
        		int numberOfGaps = gapOffsets.getCurrentLength();
                
                if(numberOfGaps==0) {
                	int numberOfNs = nOffsets.getCurrentLength();
                	if(numberOfNs==0) {
                		return new ACGTOnlySimpleNucleotideSequence(nucleotideSequenceBuilder.data.copy());
                            	
                	}
                	return new ACGTNOnlySimpleNucleotideSequence(nucleotideSequenceBuilder.data.copy());
                        	
                }
                return new SimpleNucleotideSequence(nucleotideSequenceBuilder.data.copy());
        	}
            boolean convertUs2Ts;
        	if(numUs >0 && numTs >0){
                convertUs2Ts=false;
            }else{
                convertUs2Ts=true;
            }
        	return encode(nucleotideSequenceBuilder.iterator(convertUs2Ts));
		}
		public NucleotideSequence encode(Iterator<Nucleotide> iterator) {
        	
        	int numberOfGaps = gapOffsets.getCurrentLength();
            int numberOfNs = nOffsets.getCurrentLength();
            boolean hasUs = numUs >0;
            boolean hasTs = numTs >0;
			if(forceBasicCodec || numberOfNonNAmbiguities>0 || (numberOfGaps>0 && numberOfNs >0) || (hasUs && hasTs)){
                byte[] encodedBytes= BasicNucleotideCodec.INSTANCE.encode(currentLength, gapOffsets.toArray(), iterator);
                return new DefaultNucleotideSequence(BasicNucleotideCodec.INSTANCE, encodedBytes, hasUs, (hasUs && !hasTs));
			}
			//if we get this far then we don't have any non-N ambiguities
			//AND we have either only gaps or only Ns
            int fourBitBufferSize =BasicNucleotideCodec.INSTANCE.getNumberOfEncodedBytesFor(currentLength, numberOfGaps);
            int twoBitBufferSize = AcgtnNucloetideCodec.INSTANCE.getNumberOfEncodedBytesFor(currentLength,
            		Math.max(numberOfGaps, numberOfNs));
            if(fourBitBufferSize < twoBitBufferSize){
                byte[] encodedBytes= BasicNucleotideCodec.INSTANCE.encode(currentLength, gapOffsets.toArray(), iterator);
                return new DefaultNucleotideSequence(BasicNucleotideCodec.INSTANCE, encodedBytes,hasUs, (hasUs && !hasTs));
            }
            if(numberOfGaps==0 ){
                byte[] encodedBytes= AcgtnNucloetideCodec.INSTANCE.encode(currentLength, nOffsets.toArray(), iterator);
                return new DefaultNucleotideSequence(AcgtnNucloetideCodec.INSTANCE, encodedBytes,hasUs, (hasUs && !hasTs));
            }
            
            byte[] encodedBytes= AcgtGapNucleotideCodec.INSTANCE.encode(currentLength, gapOffsets.toArray(), iterator);
            return new DefaultNucleotideSequence(AcgtGapNucleotideCodec.INSTANCE, encodedBytes, hasUs, (hasUs && !hasTs));
       
		}
		CodecDecider(NewValues newValues){
        	nOffsets = newValues.getNOffsets().copy();
			currentLength = newValues.getLength();
			numberOfNonNAmbiguities = newValues.getnumberOfNonNAmiguities();
			numUs = newValues.numUs;
            numTs = newValues.numTs;
			gapOffsets = newValues.getGapOffsets().copy();
        }
        CodecDecider copy(){
        	CodecDecider copy = new CodecDecider();
        	copy.numberOfNonNAmbiguities = numberOfNonNAmbiguities;
        	copy.numUs = numUs;
        	copy.numTs = numTs;
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
             numUs=0;
             numTs=0;
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
            numUs+=otherDecider.numUs;
            numTs+=otherDecider.numTs;

        }
        public void append(NewValues newValues) {
        	append(newValues.getGapOffsets(), gapOffsets);
        	append(newValues.getNOffsets(), nOffsets);
        
			currentLength += newValues.getLength();
			numberOfNonNAmbiguities += newValues.getnumberOfNonNAmiguities();
			numUs +=newValues.numUs;
			numTs +=newValues.numTs;
			
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
			numUs += newValues.numUs;
            numTs +=newValues.numTs;
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
			numUs -= newValues.numUs;
            numTs -=newValues.numTs;
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
    
    interface NewValuesFactory{
		NewValues create(char[] sequence, InvalidCharacterHandler invalidCharacterHandler);

		NewValues create(GrowableByteArray data);

		NewValues create(Iterable<Nucleotide> nucleotides);

		NewValues create(Iterator<Nucleotide> iter, int length);

		NewValues create(Iterator<Nucleotide> iter);

		NewValues create(Nucleotide nucleotide);

		NewValues create(Nucleotide[] sequence);

		NewValues create(NucleotideSequence sequence);

		NewValues create(String sequence, InvalidCharacterHandler invalidCharacterHandler);
    }
    public enum DefaultNewValuesFactory implements NewValuesFactory{
    	INSTANCE;
    	
		@Override
		public NewValues create(char[] sequence, InvalidCharacterHandler invalidCharacterHandler) {
			return new NewValues(sequence, invalidCharacterHandler);
		}

		@Override
		public NewValues create(GrowableByteArray data) {
			return new NewValues(data);
		}

		@Override
		public NewValues create(Iterable<Nucleotide> nucleotides) {
			return new NewValues(nucleotides);
		}

		@Override
		public NewValues create(Iterator<Nucleotide> iter, int length) {
			return new NewValues(iter, length);
		}

		@Override
		public NewValues create(Iterator<Nucleotide> iter) {
			return new NewValues(iter);
		}

		@Override
		public NewValues create(Nucleotide nucleotide) {
			return new NewValues(nucleotide);
		}

		@Override
		public NewValues create(Nucleotide[] sequence) {
			return new NewValues(sequence);
		}

		@Override
		public NewValues create(NucleotideSequence sequence) {
			return new NewValues(sequence);
		}

		@Override
		public NewValues create(String sequence, InvalidCharacterHandler invalidCharacterHandler) {
			return new NewValues(sequence, invalidCharacterHandler);
		}
    	
    }
    public enum AdjustedNewValuesFactory implements NewValuesFactory{

    	INSTANCE;
    	
		@Override
		public NewValues create(char[] sequence, InvalidCharacterHandler invalidCharacterHandler) {
			return new AdjustedNewValues(sequence, invalidCharacterHandler);
		}

		@Override
		public NewValues create(GrowableByteArray data) {
			return new AdjustedNewValues(data);
		}

		@Override
		public NewValues create(Iterable<Nucleotide> nucleotides) {
			return new AdjustedNewValues(nucleotides);
		}

		@Override
		public NewValues create(Iterator<Nucleotide> iter, int length) {
			return new AdjustedNewValues(iter, length);
		}

		@Override
		public NewValues create(Iterator<Nucleotide> iter) {
			return new AdjustedNewValues(iter);
		}

		@Override
		public NewValues create(Nucleotide nucleotide) {
			return new AdjustedNewValues(nucleotide);
		}

		@Override
		public NewValues create(Nucleotide[] sequence) {
			return new AdjustedNewValues(sequence);
		}

		@Override
		public NewValues create(NucleotideSequence sequence) {
			return new AdjustedNewValues(sequence);
		}

		@Override
		public NewValues create(String sequence, InvalidCharacterHandler invalidCharacterHandler) {
			return new AdjustedNewValues(sequence, invalidCharacterHandler);
		}
    	
    }
    
    private static class AdjustedNewValues extends NewValues{
    	private static boolean[] nonNAmbiguityOffsets;
    	private static byte N_Ordinal = Nucleotide.Unknown.getOrdinalAsByte();
    	
    	static {
    		Nucleotide[] values = Nucleotide.values();
    		nonNAmbiguityOffsets = new boolean[values.length];
    		
    		for(int i=0; i< values.length; i++) {
    			Nucleotide nucleotide = values[i];
				if(i!=N_Ordinal && nucleotide.isAmbiguity()) {
					nonNAmbiguityOffsets[i] =true;
				}
    		}
    	}

		@Override
		protected byte adjustValue(byte nucleotideOrdinal) {
			if(nonNAmbiguityOffsets[nucleotideOrdinal]) {
				return N_Ordinal;
			}
			return nucleotideOrdinal;
		}

		public AdjustedNewValues(char[] sequence, InvalidCharacterHandler invalidCharacterHandler) {
			super(sequence, invalidCharacterHandler);
		}

		public AdjustedNewValues(GrowableByteArray data) {
			super(data.replaceIf(i-> nonNAmbiguityOffsets[i], N_Ordinal ));
		}

		public AdjustedNewValues(Iterable<Nucleotide> nucleotides) {
			super(nucleotides);
		}

		public AdjustedNewValues(Iterator<Nucleotide> iter, int length) {
			super(iter, length);
		}

		public AdjustedNewValues(Iterator<Nucleotide> iter) {
			super(iter);
		}

		public AdjustedNewValues(Nucleotide nucleotide) {
			super(nucleotide);
		}

		public AdjustedNewValues(Nucleotide[] sequence) {
			super(sequence);
		}

		public AdjustedNewValues(NucleotideSequence sequence) {
			super(sequence);
		}

		public AdjustedNewValues(String sequence, InvalidCharacterHandler invalidCharacterHandler) {
			super(sequence, invalidCharacterHandler);
		}
    	
    	
    }
    private static class NewValues{
    	
    	private final GrowableByteArray data;
    	private int numberOfACGTs;
    	private int numUs;
        private int numTs;

    	private final GrowableIntArray nOffsets;
    	private final GrowableIntArray gapOffsets;

    	public NewValues(GrowableByteArray data){
    		this.data = data.copy();
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
			
    		
    		data.forEachIndexed((offset, i)-> handleOrdinal(i, offset));
    		
    	}
    	
    	public NewValues(Nucleotide nucleotide){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
    		data = new GrowableByteArray(1);
    		
            handle(nucleotide, 0);
            //only one value so we
            //don't need to sort
    	}
    	
        public NewValues(Nucleotide[] sequence) {
            nOffsets = new GrowableIntArray();
            gapOffsets = new GrowableIntArray();
            data = new GrowableByteArray(sequence.length);

            int offset = 0;
            for (int i = 0; i < sequence.length; i++) {
                Nucleotide n = sequence[i];
                if (n != null) {
                    handle(n, offset);
                    offset++;
                }
            }
        }
    	public NewValues(String sequence, Nucleotide.InvalidCharacterHandler invalidCharacterHandler){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
			data = new GrowableByteArray(sequence.length());
			
    		int offset=0;
            //convert sequence to char[] which 
            //will run faster than sequence.charAt(i)
            char[] chars = sequence.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                Nucleotide n = Nucleotide.parseOrNull(c, invalidCharacterHandler);
                if (n != null) {
                    handle(n, offset);
                    offset++;
                }
            }
    		
    	}
    	public NewValues(char[] sequence, Nucleotide.InvalidCharacterHandler invalidCharacterHandler){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
			data = new GrowableByteArray(sequence.length);
			
    		int offset=0;
            
    		for(int i=0; i<sequence.length; i++){
    			char c = sequence[i];    			
				Nucleotide n = Nucleotide.parseOrNull(c, invalidCharacterHandler);
				if(n !=null){
    				handle(n, offset);
                	offset++;
    			}
    		}
    		
    	}
    	
    	
    	/**
    	 * Convenience constructor that allocates
    	 * the gap Offsets and bitSet fields to the needed
    	 * sizes
    	 * since we know those sizes before processing. 
    	 * @param sequence the sequence to use.
    	 * 
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
    	public NewValues(Iterator<Nucleotide> iter){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
    		data = new GrowableByteArray(100);
            int offset=0;
            while(iter.hasNext()){
            	Nucleotide n = iter.next();
            	handle(n, offset);
            	offset++;            	
            }
    	}
    	public NewValues(Iterator<Nucleotide> iter, int length){
    		nOffsets = new GrowableIntArray(12);
			gapOffsets = new GrowableIntArray(12);
    		data = new GrowableByteArray(length);
            int offset=0;
            while(iter.hasNext()){
            	Nucleotide n = iter.next();
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
    	
    	protected byte adjustValue(byte nucleotideOrdinal) {
    		return nucleotideOrdinal;
    	}
    	@SuppressWarnings("fallthrough")
		private void handle(Nucleotide n, int offset) {
			byte value=adjustValue(n.getOrdinalAsByte());
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
				case 14:numberOfACGTs++; break;
				case 15: {
                    numberOfACGTs++;
                    numTs++;
                    break;
                }
                case 16 : {
                    numberOfACGTs++;
                    numUs++;
                    break;
                }
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
			case 14:numberOfACGTs++; break;
			case 15:{
			    numberOfACGTs++;
			    numTs++;
			    break;
            }
			case 16 : {
                numberOfACGTs++;
                numUs++;
                break;
            }
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



	@Override
	public NucleotideSequenceBuilder getSelf() {
		return this;
	}



    



	





	



	
}
