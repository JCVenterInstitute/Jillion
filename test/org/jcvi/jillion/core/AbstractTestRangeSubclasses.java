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
package org.jcvi.jillion.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public abstract class AbstractTestRangeSubclasses {

	
	private Range getRange(){
		return Range.of(getBegin(),getEnd());
	}
	
	protected abstract Range getDifferentRange();
	
	protected abstract long getBegin();
	protected abstract long getEnd();
	
	protected abstract long getLength();
	
	@Test
	public void getters(){
		Range range = getRange();
		assertEquals(getBegin(), range.getBegin());
		assertEquals(getEnd(), range.getEnd());
		assertEquals(getLength(), range.getLength());
	}

	@Test
	public void equalsSameRef(){
		TestUtil.assertEqualAndHashcodeSame(getRange(), getRange());
	}
	@Test
	public void notEqualToNoRange(){
		
		assertFalse(getRange().equals("not a range"));
	}
	@Test
	public void notEqualsDifferentValues(){
		TestUtil.assertNotEqualAndHashcodeDifferent(getRange(), getDifferentRange());
	}
	
	@Test
	public void serialze() throws IOException, ClassNotFoundException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		Range range = getRange();
		oos.writeObject(range);
		oos.close();
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
	
		Range deserializedRange = (Range)in.readObject();
		
		assertEquals(range, deserializedRange);
		assertEquals(range.getClass(), deserializedRange.getClass());
	}
}
