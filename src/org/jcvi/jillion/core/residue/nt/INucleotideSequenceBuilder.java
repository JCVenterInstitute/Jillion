package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;

public interface INucleotideSequenceBuilder<S extends INucleotideSequence<S, B>, B extends INucleotideSequenceBuilder<S,B>> extends ResidueSequenceBuilder<Nucleotide,S, B>{
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
	B insert(int offset, B otherBuilder);

	B insert(int offset, NucleotideSequence sequence);
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
	B insert(int offset, Nucleotide[] sequence);
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
	B insert(int offset, char[] sequence);
	
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
    B append(char[] sequence);
    
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
    B append(Nucleotide[] sequence);
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     * 
     * @return this.
     */
    B append(NucleotideSequence sequence);
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
    B append(NucleotideSequence sequence, Range range);
    
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
    B append(NucleotideSequenceBuilder otherBuilder);
    
    /**
     * Sets the {@link org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler}
     * used to help parse {@link Nucleotide}s from a String or char[].
     * @param invalidCharacterHandler the handler to use; if {@code null}
     * use the default handler which will throw an IllegalArgumentException on invalid characters.
     * 
     * @since 6.0
     */
    B setInvalidCharacterHandler(Nucleotide.InvalidCharacterHandler invalidCharacterHandler);
    
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
    B prepend(Nucleotide n);
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
    B prepend(NucleotideSequence sequence);

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
    B reverseComplement();
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
    Range toGappedRange(Range ungappedRange);
    
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
    Range toUngappedRange(Range gappedRange);
    
    B replace(Range range, B otherBuilder);
    
    B replace(Range range, Nucleotide[] replacementSequence);
}
