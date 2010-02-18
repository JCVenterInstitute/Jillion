/*
 * Created on Aug 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

public class TestMathUtilMedianOf {
    private static final String FAIL_MESSAGE = "empty list should throw illegalArgumentException";
    private static final String MUST_PASS_IN_AT_LEAST_ONE_VALUE =
                                        "must pass in at least one value";
    private static final Integer ONE = Integer.valueOf(1);
    private static final Integer TWO = Integer.valueOf(2);
    @Test
    public void emptyListShouldThrowIllegalArgumentException(){
        try{
            MathUtil.medianOf(Collections.<Integer>emptyList());
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void emptyVarArgsShouldThrowIllegalArgumentException(){
        try{
            MathUtil.medianOf();
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void oneValue(){
        assertEquals("one value",ONE,  MathUtil.medianOf(ONE));
    }
    @Test
    public void threeValues(){
        assertEquals("three value",TWO,  MathUtil.medianOf(1,2,30));
    }
    @Test
    public void sameValues(){
        assertEquals("same value",TWO,  MathUtil.medianOf(1,2,2));
    }
    @Test
    public void twoValues(){
        assertEquals("same value",ONE,  MathUtil.medianOf(1,2));
    }

    @Test
    public void oddValues(){
        assertEquals("same value",Integer.valueOf(5),  MathUtil.medianOf(1, 5, 2, 8, 7));
    }

    @Test
    public void evenValues(){
        assertEquals("same value",Integer.valueOf(6),  MathUtil.medianOf(1, 5, 2, 10, 8, 7));
    }

}
