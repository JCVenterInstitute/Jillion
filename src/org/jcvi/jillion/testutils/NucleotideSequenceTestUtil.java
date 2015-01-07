package org.jcvi.jillion.testutils;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
/**
 * Utility class for making tests with
 * {@link NucleotideSequence}s more readable.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceTestUtil {

	private static final NucleotideSequence EMPTY = new NucleotideSequenceBuilder("").build();
	private NucleotideSequenceTestUtil(){
		//can not instantiate
	}
	/**
	 * Get a singleton empty {@link NucleotideSequence} instance
	 * (whose length is 0 ).
	 * @return a {@link NucleotideSequence}; will never be null.
	 */
	public static NucleotideSequence emptySeq(){
		return EMPTY;
	}
	/**
	 * Create a new {@link NucleotideSequence}
	 * using the given String.
	 * 
	 * @apiNote This is identical to
	 * <code>new NucleotideSequenceBuilder(seq).build();</code>
	 * 
	 * @param seq The sequence as a String; can not be null.
	 * @return a new NucleotideSequence will never be null.
	 * 
	 * @throws NullPointerException if seq is null.
	 * @throws IllegalArgumentException if seq has any invalid characters.
	 */
	public static NucleotideSequence create(String seq){
		return new NucleotideSequenceBuilder(seq).build();
	}
}
