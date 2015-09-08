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
package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestDefaultPlacedContig {

	private String id = "id";
	private Range range = Range.of(1,10);

	DefaultPlacedContig sut = new DefaultPlacedContig(id, range, Direction.REVERSE);
	@Test
	public void fullConstructor(){
		assertEquals(id, sut.getContigId());
		assertEquals(range, sut.asRange());
		assertEquals(Direction.REVERSE, sut.getDirection());
		assertEquals(range.getBegin(), sut.getBegin());
		assertEquals(range.getEnd(), sut.getEnd());
		assertEquals(range.getLength(), sut.getLength());
	}
	
	@Test
	public void constructorDefaultsToForwardDir(){
		DefaultPlacedContig sut = new DefaultPlacedContig(id, range);
		assertEquals(id, sut.getContigId());
		assertEquals(range, sut.asRange());
		assertEquals(Direction.FORWARD, sut.getDirection());
		assertEquals(range.getBegin(), sut.getBegin());
		assertEquals(range.getEnd(), sut.getEnd());
		assertEquals(range.getLength(), sut.getLength());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNPE(){
		new DefaultPlacedContig(null, range);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullRangeShouldThrowNPE(){
		new DefaultPlacedContig(id, null);
	}
	@Test(expected = NullPointerException.class)
	public void nullDirShouldThrowNPE(){
		new DefaultPlacedContig(id, range,null);
	}
	
	@Test
	public void notEqualtoNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void notEqualtoDifferentClass(){
		assertFalse(sut.equals("not a placedContig"));
	}
	
	@Test
	public void equalToSameRef(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void equalToSameValues(){
		DefaultPlacedContig sameValues = new DefaultPlacedContig(id, range, Direction.REVERSE);
		TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
	}
	@Test
	public void notEqualToDifferentId(){
		DefaultPlacedContig differentValues = new DefaultPlacedContig("not"+id, range, Direction.REVERSE);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	
	@Test
	public void notEqualToDifferentDir(){
		DefaultPlacedContig differentValues = new DefaultPlacedContig(id, range, Direction.FORWARD);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	@Test
	public void notEqualToDifferentRange(){
		DefaultPlacedContig differentValues = new DefaultPlacedContig(id, new Range.Builder(range).shift(1).build(), Direction.REVERSE);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	
}
