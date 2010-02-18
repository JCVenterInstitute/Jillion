/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestSFFUtil_convertFlowgramValues {

    short encodedValue;
    float expectedFloat;


    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                {(short)0, 0F},
                {(short)12, 0.12F},
                {(short)112, 1.12F},
                {(short)5526, 55.26F},
                {(short)836, 8.36F},
                {(short)8, 0.08F},
        });
    }

    public TestSFFUtil_convertFlowgramValues(short encodedValue,
                                    float expectedFloat){
        this.encodedValue =encodedValue;
        this.expectedFloat =expectedFloat;
    }

    @Test
    public void convert(){
        assertEquals(expectedFloat, SFFUtil.convertFlowgramValue(encodedValue),0);
    }

}
