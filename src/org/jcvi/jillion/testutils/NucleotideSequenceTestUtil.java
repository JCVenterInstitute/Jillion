package org.jcvi.jillion.testutils;

import java.util.Random;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
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
	
	private static final Random random = new Random();
	
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
		return new NucleotideSequenceBuilder(seq)
							.turnOffDataCompression(true)
							.build();
	}
	
	public static NucleotideSequence create(String s, int times){
		int motifLength = s.length();
		NucleotideSequenceBuilder builder= new NucleotideSequenceBuilder(motifLength *times)
													.turnOffDataCompression(true);
		for(int i=0; i<times; i++){
			builder.append(s);
		}
		return builder.build();
	}
	/**
	 * Create a new {@link NucleotideSequence} of the given
	 * length that is randomly populated with A, C, G and Ts.
	 * @param length the length of the sequence to return; must be >0
	 * @return a new {@link NucleotideSequence}; will never be null or 
	 * and always the specified length;
	 * @throws IllegalArgumentException if length <1
	 */
	public static NucleotideSequence createRandom(int length){
		NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(length);
		for(int i=0; i<length; i++){
			builder.append(nextRandom());
		}
		builder.turnOffDataCompression(true);
		return builder.build();
	}
	
	private static Nucleotide nextRandom(){
		switch(random.nextInt(4)){
			case 0: return Nucleotide.Adenine;
			case 1: return Nucleotide.Cytosine;
			case 2: return Nucleotide.Guanine;
			default : return Nucleotide.Thymine;
			
		}
	}
}
