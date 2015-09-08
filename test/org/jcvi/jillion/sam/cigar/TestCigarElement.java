/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.cigar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestCigarElement {

	int length=10;
	CigarOperation op = CigarOperation.ALIGNMENT_MATCH;
	CigarOperation diffOp = CigarOperation.DELETION;
	
	
	CigarElement sut = new CigarElement(op, length);
	
	@Test
	public void getters(){
		assertEquals(length, sut.getLength());
		assertEquals(op, sut.getOp());
	}
	
	@Test
	public void assertNotEqualtoNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void assertNotEqualtoDifferentClass(){
		assertFalse(sut.equals("not a cigar element"));
	}
	@Test
	public void sameRefIsEqual(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	
	@Test
	public void sameValuesAreEqual(){
		CigarElement sameValues = new CigarElement(op, length);		
		TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
	}
	@Test
	public void differentLengthIsNotEqual(){
		CigarElement diffLength = new CigarElement(op, length+1);		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffLength);
	}
	@Test
	public void differentOpIsNotEqual(){
		CigarElement withDiffOp = new CigarElement(diffOp, length);		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, withDiffOp);
	}
}
