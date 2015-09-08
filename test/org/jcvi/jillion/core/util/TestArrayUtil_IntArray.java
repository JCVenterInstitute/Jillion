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
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.core.util.ArrayUtil;
import org.junit.Test;
public class TestArrayUtil_IntArray {

	private static final Integer FIVE = Integer.valueOf(5);
	private static final Integer TEN = Integer.valueOf(10);
	private static final Integer NINE = Integer.valueOf(9);
	@Test
	public void emptyArray(){
		List<Integer> sut = ArrayUtil.asList(new int[]{});
		assertTrue(sut.isEmpty());
		assertEquals(0, sut.size());
		assertFalse(sut.contains(FIVE));
		assertEquals(-1, sut.indexOf(FIVE));
	}
	
	@Test
	public void oneElementArray(){
		List<Integer> sut = ArrayUtil.asList(new int[]{5});
		assertFalse(sut.isEmpty());
		assertEquals(1, sut.size());
		assertTrue(sut.contains(FIVE));
		assertEquals(0, sut.indexOf(FIVE));
		
		assertFalse(sut.contains(TEN));
		assertEquals(-1, sut.indexOf(TEN));
		
		TestUtil.assertEqualAndHashcodeSame(Arrays.asList(FIVE), sut);
	}
	
	@Test
	public void twoElementsArray(){
		List<Integer> sut = ArrayUtil.asList(new int[]{5,10});
		assertFalse(sut.isEmpty());
		assertEquals(2, sut.size());
		assertTrue(sut.contains(FIVE));
		assertEquals(0, sut.indexOf(FIVE));
		assertTrue(sut.contains(TEN));
		assertEquals(1, sut.indexOf(TEN));
		
		assertFalse(sut.contains(NINE));
		assertEquals(-1, sut.indexOf(NINE));
		
		TestUtil.assertEqualAndHashcodeSame(Arrays.asList(FIVE,TEN), sut);
	}
	
	
}
