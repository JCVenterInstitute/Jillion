/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * Created on Aug 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import static org.junit.Assert.*;

import java.util.Collections;

import org.jcvi.jillion.core.util.MathUtil;
import org.junit.Test;

public class TestMathUtilMaxOf {
    private static final String FAIL_MESSAGE = "empty list should throw illegalArgumentException";
    private static final String MUST_PASS_IN_AT_LEAST_ONE_VALUE =
                                        "must pass in at least one value";
    private static final Integer ONE = Integer.valueOf(1);
    private static final Integer TWO = Integer.valueOf(2);
    @Test
    public void emptyListShouldThrowIllegalArgumentException(){
        try{
            MathUtil.maxOf(Collections.<Integer>emptyList());
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void emptyVarArgsShouldThrowIllegalArgumentException(){
        try{
            MathUtil.maxOf();
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void oneNumber(){

        assertEquals("single value",ONE, MathUtil.maxOf(ONE));
    }
    @Test
    public void twoNumbers(){
        assertEquals("two numbers",TWO, MathUtil.maxOf(1,TWO));
    }
    @Test
    public void multipleWithSameValues(){
        assertEquals("multiple with same value",ONE, MathUtil.maxOf(ONE,ONE,ONE,ONE,ONE));
    }

    @Test
    public void negativeValues(){
        Integer negativeOne =Integer.valueOf(-1);
        assertEquals("negative value",ONE, MathUtil.maxOf(ONE,negativeOne));
    }
    @Test
    public void minValue(){
        assertEquals("min value",ONE, MathUtil.maxOf(ONE,Integer.MIN_VALUE));
    }
    @Test
    public void maxValue(){
        assertEquals("max value",Integer.valueOf(Integer.MAX_VALUE), MathUtil.maxOf(ONE,Integer.MAX_VALUE));
    }
    @Test
    public void lotsOfValues(){
        assertEquals("lots of values",Integer.valueOf(1234), MathUtil.maxOf(
                -1,-4, 1000, 1234,5,7,8,9,2));
    }
}
