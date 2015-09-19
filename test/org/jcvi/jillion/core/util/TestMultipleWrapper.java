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
 * Created on Dec 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.jcvi.jillion.core.util.MultipleWrapper;
import org.jcvi.jillion.core.util.MultipleWrapper.ReturnPolicy;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestMultipleWrapper {

    @Test
    public void collectionReturnLast(){
        Collection<Integer> collection1 = new ArrayList<Integer>();
        Collection<Integer> collection2 = new HashSet<Integer>();
        
        Collection<Integer> multiCollection = MultipleWrapper.createMultipleWrapper(
                Collection.class,
                ReturnPolicy.RETURN_LAST, 
                collection1, collection2);
        
        assertTrue( multiCollection.add(42));
       assertFalse( multiCollection.add(42));
       assertEquals(2, collection1.size());
       assertEquals(1, collection2.size());
    }
    
    @Test
    public void collectionReturnFirst(){
        Collection<Integer> collection1 = new ArrayList<Integer>();
        Collection<Integer> collection2 = new HashSet<Integer>();
        
        Collection<Integer> multiCollection = MultipleWrapper.createMultipleWrapper(
                Collection.class,
                ReturnPolicy.RETURN_FIRST, 
                collection1, collection2);
        
        assertTrue( multiCollection.add(42));
       assertTrue( multiCollection.add(42));
       assertEquals(2, collection1.size());
       assertEquals(1, collection2.size());
    }
    
    @Test
    public void defaultToReturnFirst(){
        Collection<Integer> collection1 = new ArrayList<Integer>();
        Collection<Integer> collection2 = new HashSet<Integer>();
        
        Collection<Integer> multiCollection = MultipleWrapper.createMultipleWrapper(
                Collection.class,
                collection1, collection2);
        
        assertTrue( multiCollection.add(42));
       assertTrue( multiCollection.add(42));
       assertEquals(2, collection1.size());
       assertEquals(1, collection2.size());
    }
    @Test
    public void wrapCollection(){
        Collection<Integer> collection1 = new ArrayList<Integer>();
        Collection<Integer> collection2 = new HashSet<Integer>();
        
        Collection<Collection<Integer>> collections = new ArrayList<Collection<Integer>>();
        collections.add(collection1);
        collections.add(collection2);
        
        Collection<Integer> multiCollection = MultipleWrapper.createMultipleWrapper(
                Collection.class,
                collections);
        
        assertTrue( multiCollection.add(42));
       assertTrue( multiCollection.add(42));
       assertEquals(2, collection1.size());
       assertEquals(1, collection2.size());
    }
    
    @Test(expected = NullPointerException.class)
    public void nullReturnPolicyShouldThrowNullPointerException(){
        MultipleWrapper.createMultipleWrapper(
                Collection.class,
                (ReturnPolicy)null, new ArrayList());
    }
    @Test(expected = IllegalArgumentException.class)
    public void noDelegatesShouldThrowIllegalArgumentException(){
        MultipleWrapper.createMultipleWrapper(
                Collection.class,
                ReturnPolicy.RETURN_FIRST);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullDelegateShouldThrowNullPointerException(){
        MultipleWrapper.createMultipleWrapper(
                Collection.class,
                ReturnPolicy.RETURN_FIRST, new ArrayList(),null);
    }
}
