/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Collection;


import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;
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
