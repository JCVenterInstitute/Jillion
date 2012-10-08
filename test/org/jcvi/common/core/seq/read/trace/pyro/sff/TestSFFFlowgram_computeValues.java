/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.seq.read.trace.pyro.sff.SffReadData;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestSFFFlowgram_computeValues {

    short[] encodedValues = new short[]{213,0,2, 97, 120};
    byte[] indexes = new byte[]{1,2,1,1};

    short[] expectedValues = new short[]{213,2,97, 120};
    SffReadData mockReadData;

    @Before
    public void setup(){
        mockReadData = createMock(SffReadData.class);
    }

    @Test
    public void valid(){
        expect(mockReadData.getFlowIndexPerBase()).andReturn(indexes);
        expect(mockReadData.getFlowgramValues()).andReturn(encodedValues);
        replay(mockReadData);
        short[] actualValues = SffFlowgram.computeValues(mockReadData);
        assertArrayEquals(expectedValues, actualValues);
        verify(mockReadData);
    }
    @Test
    public void emptyIndexesShouldReturnEmptyList(){
        expect(mockReadData.getFlowIndexPerBase()).andReturn(new byte[]{});
        expect(mockReadData.getFlowgramValues()).andReturn(encodedValues);
        replay(mockReadData);
        assertEquals(0,SffFlowgram.computeValues(mockReadData).length);
        verify(mockReadData);
    }
    @Test
    public void emptyValuesShouldThrowIllegalArguementException(){
        expect(mockReadData.getFlowIndexPerBase()).andReturn(indexes);
        expect(mockReadData.getFlowgramValues()).andReturn(new short[]{});
        replay(mockReadData);
        try{
        	SffFlowgram.computeValues(mockReadData);
            fail("should throw array index out of bounds exception when no flowgram values");
        }catch(IllegalArgumentException expected){
            assertEquals("read data must contain Flowgram values", expected.getMessage());
        }

        verify(mockReadData);
    }

    
}
