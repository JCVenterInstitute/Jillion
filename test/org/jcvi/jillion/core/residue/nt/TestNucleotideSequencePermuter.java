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

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
public class TestNucleotideSequencePermuter {

	@Test(expected = NullPointerException.class)
	public void nullSeqShouldThrowNPE(){
		NucleotideSequencePermuter.permuteAmbiguities((NucleotideSequence)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullCollectionShouldThrowNPE(){
		NucleotideSequencePermuter.permuteAmbiguities((Collection<NucleotideSequence>)null);
	}
	@Test
	public void emptySequenceShouldReturnSetContainingEmptySequence(){
		NucleotideSequence seq = create("");
		
		assertEquals(Collections.singleton(seq), NucleotideSequencePermuter.permuteAmbiguities(seq));
	}
	@Test
	public void noAmbiguitiesShouldReturnSameSequence(){
		NucleotideSequence seq = create("AAAA");
		
		assertEquals(Collections.singleton(seq), NucleotideSequencePermuter.permuteAmbiguities(seq));
	}
	@Test
	public void oneAmbiguityThatCouldBe2BasesShouldReturn2Sequences(){
		NucleotideSequence seq = create("AAMA");
		
		assertEquals(setOf("AAAA", "AACA"), NucleotideSequencePermuter.permuteAmbiguities(seq));
	}
	
	@Test
	public void singletonCollectionWithReturnsSameResultAsPassingInThatOneSequence(){
		NucleotideSequence seq = create("AAMA");
		
		assertEquals(setOf("AAAA", "AACA"), NucleotideSequencePermuter.permuteAmbiguities(Collections.singleton(seq)));
	}
	@Test
	public void collectionWithDuplicateResultsWillReturnUniqueSet(){
		
		assertEquals(setOf("AAAA", "AACA","AAGA","AATA"), 
				NucleotideSequencePermuter.permuteAmbiguities(setOf("AAMA","AANA")));
	}
	@Test
	public void oneAmbiguityThatCouldBe4BasesShouldReturn4Sequences(){
		NucleotideSequence seq = create("AANA");
		
		assertEquals(setOf("AAAA", "AACA","AAGA","AATA"), NucleotideSequencePermuter.permuteAmbiguities(seq));
	}
	
	@Test
	public void oneAmbiguityThatCouldBe3BasesShouldReturn3Sequences(){
		NucleotideSequence seq = create("AAVA");
		
		assertEquals(setOf("AAAA", "AACA", "AAGA"), NucleotideSequencePermuter.permuteAmbiguities(seq));
	}
	
	@Test
	public void twoAmbiguitiesShouldReturnCascadingPermutations(){
		NucleotideSequence seq = create("AAMAY");
		
		assertEquals(setOf("AAAAC","AAAAT",
							"AACAC", "AACAT"), 
					NucleotideSequencePermuter.permuteAmbiguities(seq));
	}
	
	@Test
	public void primerSeqWithAmbiguties(){
		NucleotideSequence seq = create("CCTCMGTTTTTRTVTCTGGC");
		
		assertEquals(setOf(
				"CCTCCGTTTTTATATCTGGC", "CCTCAGTTTTTGTCTCTGGC", "CCTCAGTTTTTGTGTCTGGC", "CCTCAGTTTTTGTATCTGGC", "CCTCCGTTTTTATGTCTGGC", "CCTCCGTTTTTGTGTCTGGC", "CCTCAGTTTTTATCTCTGGC", "CCTCCGTTTTTGTATCTGGC", "CCTCAGTTTTTATATCTGGC", "CCTCAGTTTTTATGTCTGGC", "CCTCCGTTTTTATCTCTGGC", "CCTCCGTTTTTGTCTCTGGC"
				),
				NucleotideSequencePermuter.permuteAmbiguities(seq));
	}
	
	private Set<NucleotideSequence> setOf(String...seqs){
		Set<NucleotideSequence> set = new HashSet<>();
		for(String s : seqs){
			set.add(create(s));
		}
		return set;
	}
	private NucleotideSequence create(String seq){
		return new NucleotideSequenceBuilder(seq).build();
	}
}
