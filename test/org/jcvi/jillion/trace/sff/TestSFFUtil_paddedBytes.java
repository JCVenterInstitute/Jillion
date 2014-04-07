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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestSFFUtil_paddedBytes {

    int readIn, expectedPadding;
    public TestSFFUtil_paddedBytes(int readIn, int expectedPadding){
        this.readIn = readIn;
        this.expectedPadding = expectedPadding;
    }
    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                {8, 0},
                {7, 1},
                {6, 2},
                {5, 3},
                {4, 4},
                {3, 5},
                {2, 6},
                {1, 7},

                {8+8, 0},
                {8+4, 4},
        });
    }

    @Test
    public void paddedBytes(){
        assertEquals(expectedPadding, SffUtil.caclulatePaddedBytes(readIn));
    }

}
