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
        assertEquals(expectedFloat, SffUtil.convertFlowgramValue(encodedValue),0);
    }

}
