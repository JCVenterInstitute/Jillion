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
 * Created on Aug 9, 2007
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core;


import static org.junit.Assert.*;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestEmptyRange{

    private Range emptyRange = new Range.Builder().build();
    private Range nonEmptyRange = Range.of(5,5);


    @Test
    public void testSize(){
        assertEquals(0,emptyRange.getLength());
    }
    @Test
    public void testIsEmpty(){
        assertTrue(emptyRange.isEmpty());
    }
    @Test
    public void testIsSubRangeOf(){
        assertFalse(emptyRange.isSubRangeOf(nonEmptyRange));
    }
    @Test
    public void testEndsBefore(){
        assertTrue(emptyRange.endsBefore(nonEmptyRange));
        assertFalse(new Range.Builder()
        			.shift(10)
        			.build().endsBefore(nonEmptyRange));
    }
    @Test
    public void testIntersects(){
        assertFalse(emptyRange.intersects(nonEmptyRange));
    }


    @Test
    public void testIntersection(){
        assertSame(emptyRange,emptyRange.intersection(nonEmptyRange));
    }
    @Test
    public void testStartsBefore(){
        assertTrue(emptyRange.startsBefore(nonEmptyRange));
    }
    
}
