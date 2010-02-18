/*
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.Arrays;
import java.util.List;

import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadData;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;
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
