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

import static org.junit.Assert.*;

import java.util.Collections;

import org.jcvi.util.MathUtil;
import org.junit.Test;

public class TestMathUtilMedianOf {
    private static final String FAIL_MESSAGE = "empty list should throw illegalArgumentException";
    private static final String MUST_PASS_IN_AT_LEAST_ONE_VALUE =
                                        "must pass in at least one value";
    private static final Long ONE = Long.valueOf(1);
    private static final Long TWO = Long.valueOf(2);
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
            MathUtil.<Integer>medianOf();
            fail(FAIL_MESSAGE);
        }
        catch(IllegalArgumentException e){
            assertEquals(MUST_PASS_IN_AT_LEAST_ONE_VALUE,e.getMessage());
        }
    }

    @Test
    public void oneValue(){
        assertEquals("one value",ONE,  MathUtil.<Long>medianOf(ONE));
    }
    @Test
    public void threeValues(){
        assertEquals("three value",TWO,  MathUtil.<Integer>medianOf(1,2,30));
    }
    @Test
    public void sameValues(){
        assertEquals("same value",TWO,  MathUtil.<Integer>medianOf(1,2,2));
    }
    @Test
    public void twoValues(){
        assertEquals("same value",ONE,  MathUtil.<Integer>medianOf(1,2));
    }

    @Test
    public void oddValues(){
        assertEquals("same value",Long.valueOf(5),  MathUtil.<Integer>medianOf(1, 5, 2, 8, 7));
    }

    @Test
    public void evenValues(){
        assertEquals("same value",Long.valueOf(6),  MathUtil.<Integer>medianOf(1, 5, 2, 10, 8, 7));
    }

}
