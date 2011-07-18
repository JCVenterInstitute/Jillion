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
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFReadData;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestSFFUtil_computeValues {

    short[] encodedValues = new short[]{213,0,2, 97, 120};
    byte[] indexes = new byte[]{1,2,1,1};

    List<Short> expectedValues = Arrays.<Short>asList((short)213,
                                                        (short)2,
                                                        (short)97,
                                                        (short)120);
    SFFReadData mockReadData;

    @Before
    public void setup(){
        mockReadData = createMock(SFFReadData.class);
    }

    @Test
    public void valid(){
        expect(mockReadData.getFlowIndexPerBase()).andReturn(indexes);
        expect(mockReadData.getFlowgramValues()).andReturn(encodedValues);
        replay(mockReadData);
        List<Short> actualValues = SFFUtil.computeValues(mockReadData);
        assertEquals(expectedValues, actualValues);
        verify(mockReadData);
    }
    @Test
    public void emptyIndexesShouldReturnEmptyList(){
        expect(mockReadData.getFlowIndexPerBase()).andReturn(new byte[]{});
        expect(mockReadData.getFlowgramValues()).andReturn(encodedValues);
        replay(mockReadData);
        assertTrue(SFFUtil.computeValues(mockReadData).isEmpty());
        verify(mockReadData);
    }
    @Test
    public void emptyValuesShouldThrowIllegalArguementException(){
        expect(mockReadData.getFlowIndexPerBase()).andReturn(indexes);
        expect(mockReadData.getFlowgramValues()).andReturn(new short[]{});
        replay(mockReadData);
        try{
            SFFUtil.computeValues(mockReadData);
            fail("should throw array index out of bounds exception when no flowgram values");
        }catch(IllegalArgumentException expected){
            assertEquals("read data must contain Flowgram values", expected.getMessage());
        }

        verify(mockReadData);
    }

    @Test
    public void returnsUnmodifiableList(){
        expect(mockReadData.getFlowIndexPerBase()).andReturn(indexes);
        expect(mockReadData.getFlowgramValues()).andReturn(encodedValues);
        replay(mockReadData);
        List<Short> actualValues = SFFUtil.computeValues(mockReadData);
        try{
            actualValues.add((short)0);
            fail("returned list should not be modifiable");
        }catch(UnsupportedOperationException expected){

        }
        verify(mockReadData);
    }
}
