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
public class TestRangeComparatorLongestToShortest extends AbstractTestSizeRangeComparator{

    @Test
    public void sort(){
        Collections.sort(ranges, Range.Comparators.LONGEST_TO_SHORTEST);
        List<Range> expectedOrder = Arrays.asList(large,medium,small);
        
        assertEquals(expectedOrder, ranges);
    }
}
