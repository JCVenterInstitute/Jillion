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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
public class TestRangeIterator {

    Range range = Range.of(1, 10);
    Iterator<Long> sut;
    @Before
    public void setup(){
        sut = range.iterator();
    }
    @Test
    public void iterate(){
        for(long i= range.getBegin(); i<=range.getEnd(); i++){
            assertTrue(sut.hasNext());
            assertEquals(Long.valueOf(i), sut.next());
        }
        assertFalse(sut.hasNext());
        try{
        	sut.next();
        	fail("should throw no such element exception");
        }catch(NoSuchElementException expected){
        	//pass
        }
    }
    
    @Test
    public void removeShouldthrowUnsupportedOperationException(){
        
        try{
            sut.remove();
            fail("should throw Unsupported operation exception");
        }catch(UnsupportedOperationException e){
            assertEquals("can not remove from Range", e.getMessage());
        }
    }
    
    @Test
    public void iteratorOverRange(){
        for(Long i : range){
            assertEquals(i, sut.next());
        }
        assertFalse(sut.hasNext());
    }
    /**
     * All longs are <= Long.MAX_VALUE
     * if the iteration code is naively 
     * written it will infinite loop if end is max_value.
     * See Java Puzzlers Puzzle# 26 for more details.
     */
    @Test
    public void testEndIsInMax(){
    	Range r = Range.of(Long.MAX_VALUE-10, Long.MAX_VALUE);
    	Iterator<Long> sut = r.iterator();
    	for(int i =0; i< 10; i++){   
    		assertTrue(sut.hasNext());
            assertEquals(Long.MAX_VALUE-10+i, sut.next().longValue());
        }
        assertFalse(sut.hasNext());
    }
    @Test
    public void testEndIsMaxEmptyRange(){
    	Range r = new Range.Builder()
    				.shift(Long.MAX_VALUE)
    				.build();
    	Iterator<Long> sut = r.iterator();
    	assertFalse(sut.hasNext());
    }
}
