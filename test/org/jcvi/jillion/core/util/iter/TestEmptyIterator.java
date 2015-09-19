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
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestEmptyIterator {

    Iterator<String> sut = IteratorUtil.createEmptyIterator();
    @Test
    public void removeDoesNothing(){
        sut.remove();
    }
    
    @Test
    public void hasNextReturnsFalse(){
        assertFalse(sut.hasNext());
    }
    
    @Test
    public void nextThrowsNoSuchElementException(){
        try{
            sut.next();
            fail("should throw no such element exception");
        }catch(NoSuchElementException e){
            assertEquals("no elements in empty iterator", e.getMessage());
        }
    }
}
