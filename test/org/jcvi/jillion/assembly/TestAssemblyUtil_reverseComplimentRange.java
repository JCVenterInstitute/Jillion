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
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAssemblyUtil_reverseComplimentRange {

    private final Range range = Range.of(0, 9);
    @Test
    public void reverseFullRange(){
        assertEquals(range, AssemblyUtil.reverseComplementValidRange(range, range.getLength()));
    }
    
    @Test
    public void reverse(){
        Range expectedRange = Range.of(5,14);
        assertEquals(expectedRange, AssemblyUtil.reverseComplementValidRange(range, 15));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void fullLengthSmallerThanValidRangeShouldThrowIllegalArgumentException(){
        AssemblyUtil.reverseComplementValidRange(range, range.getLength()-1);
    }
    
    @Test
    public void validRangeInMiddleOfFullRange(){
        Range validRange = Range.of(5,9);
       assertEquals(Range.of(10,14), AssemblyUtil.reverseComplementValidRange(validRange, 20));
    }
}
