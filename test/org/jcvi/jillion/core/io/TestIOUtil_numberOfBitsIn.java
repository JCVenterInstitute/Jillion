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
package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestIOUtil_numberOfBitsIn {

	private final int value, expectedNumberOfBits;
	
	 @Parameters
    public static Collection<?> data(){
        List<Integer[]> data = new ArrayList<Integer[]>();
        data.add(new Integer[]{0,1}); //special case
        data.add(new Integer[]{1,1});
        data.add(new Integer[]{2,2});
        data.add(new Integer[]{3,2});
        data.add(new Integer[]{4,3});
        data.add(new Integer[]{5,3});
        data.add(new Integer[]{6,3});
        data.add(new Integer[]{7,3});
        data.add(new Integer[]{8,4});
        
        data.add(new Integer[]{100,7});
        data.add(new Integer[]{1024,11});
        return data;
 	}

	public TestIOUtil_numberOfBitsIn(int value, int expectedNumberOfBits) {
		this.value = value;
		this.expectedNumberOfBits = expectedNumberOfBits;
	}
	 
	@Test
	public void computeNumberOfBits(){
		assertEquals("value " + value, expectedNumberOfBits, IOUtil.computeNumberOfBitsIn(value));
	}
	 
	 

}
