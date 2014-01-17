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
