/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
/*
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class TestSFFFlowgram_computeValues {

    short[] encodedValues = new short[]{213,0,2, 97, 120};
    byte[] indexes = new byte[]{1,2,1,1};

    short[] expectedValues = new short[]{213,2,97, 120};

    @Test
    public void valid(){
        short[] actualValues = SffFlowgramImpl.computeValues(indexes, encodedValues);
        assertArrayEquals(expectedValues, actualValues);
    }
    @Test
    public void emptyIndexesShouldReturnEmptyList(){
        assertEquals(0,SffFlowgramImpl.computeValues(new byte[]{}, encodedValues).length);

    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyValuesShouldThrowIllegalArguementException(){
        SffFlowgramImpl.computeValues(indexes, new short[]{});        

    }

    
}
