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
 * Created on Aug 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import java.util.Collections;

import org.jcvi.common.core.util.MathUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestMathUtilMinOf {

    private static final String FAIL_MESSAGE = "empty list should throw illegalArgumentException";
    private static final String MUST_PASS_IN_AT_LEAST_ONE_VALUE =
                                        "must pass in at least one value";
    private static final Integer ONE = Integer.valueOf(1);
    @Test
    public void emptyListShouldThrowIllegalArgumentException(){
        try{
            MathUtil.minOf(Collections.<Integer>emptyList());
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void emptyVarArgsShouldThrowIllegalArgumentException(){
        try{
            MathUtil.minOf();
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void oneNumber(){

        assertEquals("single value",ONE, MathUtil.minOf(ONE));
    }
    @Test
    public void twoNumbers(){
        assertEquals("two numbers",ONE, MathUtil.minOf(ONE,2));
    }
    @Test
    public void multipleWithSameValues(){
        assertEquals("multiple with same value",ONE, MathUtil.minOf(ONE,ONE,ONE,ONE,ONE));
    }

    @Test
    public void negativeValues(){
        Integer negativeOne =Integer.valueOf(-1);
        assertEquals("negative value",negativeOne, MathUtil.minOf(ONE,negativeOne));
    }
    @Test
    public void minValue(){
        assertEquals("min value",Integer.valueOf(Integer.MIN_VALUE), MathUtil.minOf(ONE,Integer.MIN_VALUE));
    }
    @Test
    public void maxValue(){
        assertEquals("max value",ONE, MathUtil.minOf(ONE,Integer.MAX_VALUE));
    }
    @Test
    public void lotsOfValues(){
        assertEquals("lots of values",Integer.valueOf(-4), MathUtil.minOf(
                -1,-4, 1000, 1234,5,7,8,9,2));
    }

}
