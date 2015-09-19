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
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class TestSFFFlowgram_computeValues {

    short[] encodedValues = new short[]{213,0,2, 97, 120};
    byte[] indexes = new byte[]{1,2,1,1};

    short[] expectedValues = new short[]{213,2,97, 120};

    @Test
    public void valid(){
        short[] actualValues = SffFlowgramImpl.computeValues(indexes, encodedValues);
        assertArrayEquals(expectedValues, actualValues);
    }
    @Test
    public void emptyIndexesShouldReturnEmptyList(){
        assertEquals(0,SffFlowgramImpl.computeValues(new byte[]{}, encodedValues).length);

    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyValuesShouldThrowIllegalArguementException(){
        SffFlowgramImpl.computeValues(indexes, new short[]{});        

    }

    
}
