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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestRangeComparatorShortestToLongest extends AbstractTestSizeRangeComparator{

    @Test
    public void sort(){
        Collections.sort(ranges, Range.Comparators.SHORTEST_TO_LONGEST);
        List<Range> expectedOrder = Arrays.asList(small,medium,large);
        
        assertEquals(expectedOrder, ranges);
    }

}
