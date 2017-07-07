/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.core;


import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestIntArrayAsRanges extends AbstractTestAsRangeOpperations<int[]>{

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    public TestIntArrayAsRanges() {
        super(Ranges::asRanges, Ranges::asRanges);
    }
    protected int[] createInput(GrowableIntArray array){
        return array.toArray();
    }
    protected int[] createInput(int...ints){
        return ints;
    }

    @Test
    public void unsortedArrayShouldthrowException(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must be sorted");
        Ranges.asRanges(new int[]{8,6,7,5,3,0,9});
    }
    
    @Test
    public void reverseSortedArrayShouldthrowException(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must be sorted");
        Ranges.asRanges(new int[]{9,8,7,6,5,4,3,2,1});
    }
}
