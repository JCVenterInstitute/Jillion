/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
