package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
/**
 * {@code NucleotideSequencePermuter} is a class
 * that can transform a {@link NucleotideSequence}
 * into many other {@link NucleotideSequence}s.
 * @author dkatzel
 *
 */
public final class NucleotideSequencePermuter {

	private NucleotideSequencePermuter(){
		//can not instantiate
	}
	/**
	 * Compute all the different permutations of the given
	 * {@link NucleotideSequence} that contain all the combinations
	 * of non-ambiguous bases.
	 * @apiNote
	 * Example invocations :
	 * <ol>
	 * <li>	If the input sequence was "AAMA" then this method
	 * 		will return a set containing
	 * 		"AA<strong>A</strong>A" and "AA<strong>C</strong>A".
	 * </li>
	 * <li>	If the input sequence was "AAMAY" then this method
	 * 		will return a set containing
	 * "AA<strong>A</strong>A<strong>C</strong>",  "AA<strong>A</strong>A<strong>T</strong>",
		"AA<strong>C</strong>A<strong>C</strong>", "AA<strong>C</strong>A<strong>T</strong>"

	 * </li>
	 * </ol>
	 * @param seq the sequence to permute; may not be null.
	 * @return a Set of all the permutations; will never be empty.
	 * If the given sequence does not have any ambiguities, then
	 * a Set that only contains the original sequence is returned.
	 * @throws NullPointerException if seq is null.
	 */
	public static Set<NucleotideSequence> permuteAmbiguities(NucleotideSequence seq) {
		NucleotideSequenceBuilder seqBuilder = new NucleotideSequenceBuilder(seq);
		int numAmbiguties =seqBuilder.getNumAmbiguities();
		if(numAmbiguties ==0){
			//no reason to permute
			return Collections.singleton(seq);
		}
		
		List<NucleotideSequenceBuilder> permutations = new ArrayList<>();
		
		permutations.add(seqBuilder);
		for(int i=0; i<seqBuilder.getLength(); i++){
			if(seqBuilder.get(i).isAmbiguity()){
				permutations = permute(permutations, i);
			}
		}
		
		Set<NucleotideSequence> set= new HashSet<NucleotideSequence>(permutations.size());
		
		for(NucleotideSequenceBuilder builder : permutations){
			set.add(builder.build());
		}
		
		return set;
	}

	private static List<NucleotideSequenceBuilder> permute(List<NucleotideSequenceBuilder> permutations, int offset) {
		List<NucleotideSequenceBuilder> newPermutations = new ArrayList<>(permutations.size() *4);
		for(NucleotideSequenceBuilder builder : permutations){
			Nucleotide ambiguity =builder.get(offset);
			for(Nucleotide n :ambiguity.getBasesFor()){
				newPermutations.add(builder.copy()
									.replace(offset, n));
			}
		}
		
		return newPermutations;
		
	}
	/**
	 * Convenience method that takes a Collection of {@link NucleotideSequence}s
	 * and permutes the ambiguities of each one and returns the entire permutation set.
	 * @apiNote
	 * This is equivalent to :
	 * <pre>
	 * {@code 
	 * 	Set<NucleotideSequence> set = new LinkedHashSet<>();
	 * 		for(NucleotideSequence seq : sequences){
	 *		set.addAll(permuteAmbiguities(seq));
	 *	}
	 *	return set;
	 * 
	 * </pre>
	 * @param sequences
	 * @return
	 */
	public static Set<NucleotideSequence> permuteAmbiguities(Collection<NucleotideSequence> sequences){
		Set<NucleotideSequence> set = new LinkedHashSet<>();
		for(NucleotideSequence seq : sequences){
			set.addAll(permuteAmbiguities(seq));
		}
		return set;
	}

}
