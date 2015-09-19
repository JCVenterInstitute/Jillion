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
