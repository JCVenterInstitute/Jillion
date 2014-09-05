package org.jcvi.jillion.profile;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code MostFrequentTieBreakerRule} determines
 * which base to choose if more than one
 * base has the same highest count value at a given
 * position (must be >0).
 * @author dkatzel
 *
 */
public enum MostFrequentTieBreakerRule {
	/**
	 * Choose the base with lowest ascii value:
	 * A < C < G < T.
	 * This rule is consistent although may not 
	 * be the best choice for showing diversity.
	 */
	LOWEST_ASCII{

		@Override
		Nucleotide getMostFrequent(List<Nucleotide> mostFrequentBases) {

			Iterator<Nucleotide> iter =mostFrequentBases.iterator();
			
			Nucleotide n =iter.next();
			int value = n.getCharacter().charValue();
			while(iter.hasNext()){
				Nucleotide current =iter.next();
				int currentValue = current.getCharacter().charValue();
				
				if(currentValue < value){
					n = current;
					value = currentValue;
				}
			}
			return n;
		}
		
	},
	/**
	 * Randomly select one of the values.
	 */
	RANDOM{
		Random rnd = new Random();
		@Override
		Nucleotide getMostFrequent(List<Nucleotide> mostFrequentBases) {
			return mostFrequentBases.get(rnd.nextInt(mostFrequentBases.size()));
		}
		
	},
	/**
	 * Pick the ambiguity code that describes all
	 * the given bases.
	 */
	AMBIGUITY{
		@Override
		Nucleotide getMostFrequent(List<Nucleotide> mostFrequentBases) {
			return Nucleotide.getAmbiguityFor(mostFrequentBases);
		}
	};
	
	abstract Nucleotide getMostFrequent(List<Nucleotide> mostFrequentBases);
}
