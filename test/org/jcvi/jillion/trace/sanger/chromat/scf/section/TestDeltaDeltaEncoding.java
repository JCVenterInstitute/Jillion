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
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat.scf.section;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Collection;


import org.jcvi.jillion.internal.trace.sanger.chromat.scf.SCFUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestDeltaDeltaEncoding {

   
    short[] original,expectedDeltaDelta;
    @Parameters
    public static Collection<?> data(){
        //first value is decoded position,
        //second value is delta delta encoded
        //basic algorithm is deltaDelta[i] = orig[i]-2[orig[i-1] + orig[i-2]
        return Arrays.asList(new Object[][]{
                {   new short[]{10,20},
                    new short[]{10,0}
                },
                {   new short[]{10,20,30},
                    new short[]{10,0,0}
                },
                {   new short[]{10,20,25},
                    new short[]{10,0,-5}
                },
                {   new short[]{10,20,30,70},
                    new short[]{10,0,0,30}
                },
                {   new short[]{10,20,30,70,75},
                    new short[]{10,0,0,30,-35}
                },
                {   new short[]{10,20,30,70,75,75},
                    new short[]{10,0,0,30,-35,-5}
                },
                {   new short[]{10,20,30,70,75,75,100},
                    new short[]{10,0,0,30,-35,-5,25}
                },
                {   new short[]{10,20,30,70,75,75,100,95},
                    new short[]{10,0,0,30,-35,-5,25,-30}
                },
                {   new short[]{10,20,30,70,75,75,100,200},
                    new short[]{10,0,0,30,-35,-5,25,75}
                },
                {   new short[]{10,20,30,70,75,75,100,500},
                    new short[]{10,0,0,30,-35,-5,25,375}
                },
                {   new short[]{13,14,15,16},
                    new short[]{13,-12,0,0}
                },
                {   new short[]{200,201,202,203,204},
                    new short[]{200,-199,0,0,0}
                },

        });

    }

    public TestDeltaDeltaEncoding(short[] original, short[]expectedDeltaDelta){
        this.original = original;
        this.expectedDeltaDelta = expectedDeltaDelta;
    }
    @Test
    public void testDeltaDeltaEncode(){
        ShortBuffer actualDeltaDelta = SCFUtils.deltaDeltaEncode(ShortBuffer.wrap(original));

        assertTrue("expected " +
                Arrays.toString(expectedDeltaDelta) +"  but was " +
                Arrays.toString(actualDeltaDelta.array()),
                Arrays.equals(actualDeltaDelta.array(), expectedDeltaDelta));
    }
    @Test
    public void testDeltaDeltaDecode(){
        short[] decoded = SCFUtils.copy(ShortBuffer.wrap(expectedDeltaDelta)).array();
        SCFUtils.deltaDeltaDecode(decoded);
        assertTrue("expected " +
                Arrays.toString(original) +"  but was " +
                Arrays.toString(decoded),
                Arrays.equals(decoded, original));
    }
}
