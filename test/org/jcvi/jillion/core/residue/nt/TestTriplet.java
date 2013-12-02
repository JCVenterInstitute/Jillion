/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
		assertEquals(Nucleotide.Guanine,sut.getThrid());
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
	}
	@Test
	public void differentFirstValueShouldNotBeEqual(){
		Triplet diffValues = Triplet.create('G', 'T', 'G');
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffValues);
	}
	@Test
	public void differentSecondValueShouldNotBeEqual(){
		Triplet diffValues = Triplet.create('A', 'C', 'G');
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffValues);
	}
	@Test
	public void differentThridValueShouldNotBeEqual(){
		Triplet diffValues = Triplet.create('A', 'T', 'R');
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffValues);
	}
}
