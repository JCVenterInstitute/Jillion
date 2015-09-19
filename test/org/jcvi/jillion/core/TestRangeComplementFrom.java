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
package org.jcvi.jillion.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestRangeComplementFrom {

    Range range = Range.of(0,10);
    
    @Test
    public void complementEmptyShouldReturnEmpty(){
        assertTrue(range.complementFrom(Collections.<Range>emptyList()).isEmpty());
    }
    @Test
    public void complementWithItselfShouldReturnEmpty(){
        assertTrue(range.complementFrom(Collections.singleton(range)).isEmpty());
    }
    
    @Test
    public void oneLargeRangeShouldGetSplit(){
        List<Range> expected = Arrays.asList(
                Range.of(-10, -1),
                Range.of(11, 20)
                );
        assertEquals(expected, range.complementFrom(Collections.singleton(
                Range.of(-10,20))));
    }
    
    @Test
    public void oneSideOverhangsShouldReturnThatSide(){
        Range largeRange = Range.of(0,20);
        List<Range> expected = Arrays.asList(Range.of(11,20));
        assertEquals(expected, range.complementFrom(Collections.singleton(largeRange)));
    }
    
    @Test
    public void twoOverlappingRanges(){
        Collection<Range> ranges = Arrays.asList(
                    Range.of(0,20),
                    Range.of(-5,10))
                    ;
        List<Range> expected = Arrays.asList(
                Range.of(-5,-1),
                Range.of(11,20)
                );
        assertEquals(expected, range.complementFrom(ranges));
  
    }
}
