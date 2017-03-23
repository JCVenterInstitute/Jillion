package org.jcvi.jillion.core.util.iter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestChainedIteratorSupplier {

    List<Integer> _1_to_5 = Arrays.asList(1,2,3,4,5);
    
    List<Integer> _6_to_10 = Arrays.asList(6,7,8,9,10);
    
    @Test
    public void singleIterator(){
       Iterator<Integer> sut = IteratorUtil.chainSuppliers(() -> _1_to_5.iterator());
       
       
       assertEquals(_1_to_5, toList(sut));
    }
    
    @Test
    public void multipleIterators(){
       Iterator<Integer> sut = IteratorUtil.chainSuppliers(
               () -> _1_to_5.iterator(),
               () -> _6_to_10.iterator()
               
               
               );
       
       List<Integer> expected = new ArrayList<>();
       expected.addAll(_1_to_5);
       expected.addAll(_6_to_10);
       assertEquals(expected, toList(sut));
    }
    
    
    @Test
    public void closeSingleStreamingIterator(){
        StreamingIterator<Integer> sut = IteratorUtil.chainStreamingSuppliers(() -> IteratorUtil.createStreamingIterator(_1_to_5.iterator()));
        List<Integer> expected = new ArrayList<>();
        
        expected.add(1);
        expected.add(2);
        
        List<Integer> actual = new ArrayList<>();
        
        assertTrue(sut.hasNext());
        actual.add(sut.next());
        actual.add(sut.next());
        assertTrue(sut.hasNext());
        
        sut.close();
        
        assertFalse(sut.hasNext());
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void downstreamSupplierIsNotCalledUntilNeeded(){
        StreamingIterator<Integer> sut = IteratorUtil.chainStreamingSuppliers(
                () -> IteratorUtil.createStreamingIterator(_1_to_5.iterator()),
                () -> {throw new RuntimeException();}
                );
        List<Integer> expected = new ArrayList<>();
        
        expected.add(1);
        expected.add(2);
        
        List<Integer> actual = new ArrayList<>();
        
        assertTrue(sut.hasNext());
        actual.add(sut.next());
        actual.add(sut.next());
        assertTrue(sut.hasNext());
        
        sut.close();
        
        assertFalse(sut.hasNext());
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void singleStreamingIterator(){
        StreamingIterator<Integer> sut = IteratorUtil.chainStreamingSuppliers(() -> IteratorUtil.createStreamingIterator(_1_to_5.iterator()));
        
        
        assertEquals(_1_to_5, toList(sut));
    }

    @Test
    public void multipleStreamingIterators(){
       Iterator<Integer> sut = IteratorUtil.chainStreamingSuppliers(
               () -> IteratorUtil.createStreamingIterator(_1_to_5.iterator()),
               () -> IteratorUtil.createStreamingIterator(_6_to_10.iterator())
               
               
               );
       
       List<Integer> expected = new ArrayList<>();
       expected.addAll(_1_to_5);
       expected.addAll(_6_to_10);
       assertEquals(expected, toList(sut));
    }
    
    private List<Integer> toList(Iterator<Integer> sut) {
        List<Integer> actual = new ArrayList<>();
           while(sut.hasNext()){
               actual.add(sut.next());
           }
        return actual;
    }
}
