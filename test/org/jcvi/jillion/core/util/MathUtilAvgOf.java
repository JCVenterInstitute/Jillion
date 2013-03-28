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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.util.Collections;

import org.jcvi.jillion.core.util.MathUtil;
import org.junit.Test;

public class MathUtilAvgOf {
    private static final String FAIL_MESSAGE = "empty list should throw illegalArgumentException";
    private static final String MUST_PASS_IN_AT_LEAST_ONE_VALUE =
                                        "must pass in at least one value";
    private static final Integer ONE = Integer.valueOf(1);
    private static final Integer TWO = Integer.valueOf(2);
    @Test
    public void emptyListShouldThrowIllegalArgumentException(){
        try{
            MathUtil.averageOf(Collections.<Integer>emptyList());
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void emptyVarArgsShouldThrowIllegalArgumentException(){
        try{
            MathUtil.averageOf();
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void oneValue(){
       assertEquals("one value",ONE.doubleValue(), MathUtil.averageOf(ONE));
    }
    @Test
    public void sameValueManyTimes(){
       assertEquals("same value many times",ONE.doubleValue(),
               MathUtil.averageOf(ONE,ONE,ONE,ONE));
    }

    @Test
    public void actualAverage(){
        assertEquals("avg",1.5D,
                MathUtil.averageOf(ONE,TWO));
    }

    @Test
    public void minValue(){
        assertEquals("avg",(Integer.MIN_VALUE+1)/2D,
                MathUtil.averageOf(ONE,Integer.MIN_VALUE));
    }
    /**
     * Checks to make sure the summation calculation can
     * handle values over max int.
     */
    @Test
    public void intOverflow(){
        assertEquals("int overflow",1073741824D,
                MathUtil.averageOf(ONE,Integer.MAX_VALUE));
    }

}
