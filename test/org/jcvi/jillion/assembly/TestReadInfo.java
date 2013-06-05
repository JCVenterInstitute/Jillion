package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestReadInfo {
	Range validRange = Range.of(1,10);
	int fullLength=20;
	
	ReadInfo sut = new ReadInfo(validRange, fullLength);
	@Test
	public void constructor(){
		assertEquals(validRange, sut.getValidRange());
		assertEquals(fullLength, sut.getUngappedFullLength());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeFullLengthShouldThrowException(){
		new ReadInfo(validRange, -1);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullRangeShouldThrowNPE(){
		new ReadInfo(null, fullLength);
	}
	@Test(expected = IllegalArgumentException.class)
	public void fullLengthLessThanValidRangeShouldThrowException(){
		new ReadInfo(validRange, (int)validRange.getEnd() -1);
	}
	
	@Test
	public void readInfoEqualsSelf(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void readInfoEqualsSameValues(){
		ReadInfo sameValues =  new ReadInfo(validRange, fullLength);
		TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
	}
	@Test
	public void readInfoDifferentFullLengthNotEqual(){
		ReadInfo differentValues =  new ReadInfo(validRange, fullLength-1);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	
	@Test
	public void readInfoDifferentValidRangeNotEqual(){
		ReadInfo differentValues =  new ReadInfo(new Range.Builder(validRange)
															.contractBegin(1)
															.build(), fullLength);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	
	@Test
	public void rangeNotEqualToNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void rangeNotEqualToNotReadInfo(){
		assertFalse(sut.equals("not readInfo"));
	}
}
