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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestTriplet {

	Triplet sut = Triplet.create('A','T','G');
	
	@Test
	public void constructor(){
		assertEquals(Nucleotide.Adenine,sut.getFirst());
		assertEquals(Nucleotide.Thymine,sut.getSecond());
		assertEquals(Nucleotide.Guanine,sut.getThird());
	}
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	
	@Test
	public void notDifferentObject(){
		assertFalse(sut.equals("not a triplet"));
	}
	
	@Test
	public void nucleotideFactoryMethod(){
		Triplet t = Triplet.create(Nucleotide.Adenine, Nucleotide.Thymine, Nucleotide.Guanine);
		assertEquals(sut, t);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullFirstShouldThrowNPE(){
		Triplet.create(null, Nucleotide.Thymine, Nucleotide.Guanine);
	}
	@Test(expected = NullPointerException.class)
	public void nullSecondShouldThrowNPE(){
		Triplet.create(Nucleotide.Adenine, null, Nucleotide.Guanine);
	}
	@Test(expected = NullPointerException.class)
	public void nullThirdShouldThrowNPE(){
		Triplet.create(Nucleotide.Adenine, Nucleotide.Thymine, null);
	}
	@Test
	public void testToString(){
		assertEquals("ATG",sut.toString());
	}
	@Test
	public void equalsSameRef(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void equalsSameValues(){
		Triplet.clearCache();
		Triplet sameValues = Triplet.create('A','T','G');
		TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
	}
	
	@Test
	public void sameValuesReturnSameReference(){
		//because we've cleared the cache in a different test
		//and can't guarentee (nor should we want to)
		//that the cache was cleared, it is easier
		//to get more references and make sure they match
		//then compare the sut
		
		Triplet ref1 = Triplet.create('A','T','G');
		Triplet ref2 = Triplet.create('A','T','G');
		assertSame(ref1, ref2);
		assertEquals(0, sut.getNumChanges(ref1));
		assertEquals(0, sut.computeChangeScore(ref2));
	}
	@Test
	public void differentFirstValueShouldNotBeEqual(){
		Triplet diffValues = Triplet.create('G', 'T', 'G');
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffValues);
		assertEquals(1, sut.getNumChanges(diffValues));
		assertEquals(1, sut.computeChangeScore(diffValues));
	}
	@Test
	public void differentSecondValueShouldNotBeEqual(){
		Triplet diffValues = Triplet.create('A', 'C', 'G');
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffValues);
		assertEquals(1, sut.getNumChanges(diffValues));
		assertEquals(1, sut.computeChangeScore(diffValues));
	}
	@Test
	public void differentThridValueShouldNotBeEqual(){
		Triplet diffValues = Triplet.create('A', 'T', 'R');
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffValues);
		assertEquals(1, sut.getNumChanges(diffValues));
		assertEquals(4, sut.computeChangeScore(diffValues));
	}
	
}
