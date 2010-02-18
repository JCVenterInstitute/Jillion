/*
 * Created on Dec 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.jcvi.util.MultipleWrapper.ReturnPolicy;
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
