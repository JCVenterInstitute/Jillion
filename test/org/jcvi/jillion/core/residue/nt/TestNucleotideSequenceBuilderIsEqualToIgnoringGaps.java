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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
public class TestNucleotideSequenceBuilderIsEqualToIgnoringGaps {

	@Test
	public void shouldNeverEqualNull(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		assertFalse(seq1.isEqualToIgnoringGaps(null));
	}
	
	@Test
	public void sameSequenceShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		assertTrue(seq1.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void differentSequenceShouldNotBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("NNNN");
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void subSequenceShouldNotBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("ACGTNN");
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	@Test
	public void gappySubSequenceShouldNotBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("A-CGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("AC-GTN-N");
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	@Test
	public void sameSequenceWithGapsShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("A--CG-T");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void sameSequenceWithTrailingGapsShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("ACGT---");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void allGapsShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("--");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("--------");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void allGapsEqualEmpty(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("--------");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
}
