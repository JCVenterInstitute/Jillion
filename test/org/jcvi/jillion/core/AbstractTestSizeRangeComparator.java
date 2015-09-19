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

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Before;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSizeRangeComparator {

    Range small = new Range.Builder(10).build();
    Range medium = new Range.Builder(-10, 30).build();
    Range large = new Range.Builder(-50, 100).build();
    
    List<Range> ranges;
    @Before
    public void setup(){
        ranges = new ArrayList<Range>(3);
        ranges.add(small);
        ranges.add(medium);
        ranges.add(large);
    }
}
