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
package org.jcvi.jillion.sam.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestSamHeaderTagKey {

	SamHeaderTagKey sut = new SamHeaderTagKey('a','b');
	
	@Test
	public void getters(){
		assertEquals('a', sut.getFirstChar());
		assertEquals('b', sut.getSecondChar());
	}
	
	@Test
	public void testToString(){
		assertEquals("ab", sut.toString());
	}
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void sameRefEquals(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	
	@Test
	public void sameValuesAreEqual(){
		SamHeaderTagKey same = new SamHeaderTagKey('a','b');
		
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
	
	@Test
	public void differentFirstCharValuesAreNotEqual(){
		SamHeaderTagKey different = new SamHeaderTagKey('x','b');		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);		
	}
	@Test
	public void differentSecondCharValuesAreNotEqual(){
		SamHeaderTagKey different = new SamHeaderTagKey('a','x');		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);		
	}
	
	@Test
	public void stringCreationMethod(){
		SamHeaderTagKey same = SamHeaderTagKey.getKey('a','b');
		
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
}
