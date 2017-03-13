package org.jcvi.jillion.core.io;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestBufferSize {

    @Test
    public void cachedSizesK(){
        for(int i=0; i<65; i++){
            int expected = 1024 *i;
            assertEquals(""+i, expected, BufferSize.kb(i));
        }
    }
    
    @Test
    public void largerThanCachedSizesK(){
        for(int i=100; i<200; i++){
            int expected = 1024 *i;
            assertEquals(""+i, expected, BufferSize.kb(i));
        }
    }
    @Test
    public void largerThanCachedSizesMB(){
        int mb = 1024*1024;
        for(int i=100; i<200; i++){
            int expected = mb *i;
            assertEquals(""+i, expected, BufferSize.mb(i));
        }
    }
    @Test(expected = IllegalArgumentException.class)    
    public void negativeKShouldThrowIllegalArgumentException(){
        BufferSize.kb(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)    
    public void negativeMBShouldThrowIllegalArgumentException(){
        BufferSize.mb(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)    
    public void overMaxKShouldThrowIllegalArgumentException(){
        BufferSize.kb(Integer.MAX_VALUE);
    }
    
    @Test(expected = IllegalArgumentException.class)    
    public void smallestOverMaxKBShouldThrowIllegalArgumentException(){
        //1<<10 <<21  > int_max
        BufferSize.kb(1<<21);
    }
    @Test    
    public void largestUnderMaxKB(){
        assertEquals(1073741824, BufferSize.kb(1<<20));
    }
    
    @Test(expected = IllegalArgumentException.class)    
    public void smallestOverMaxMBShouldThrowIllegalArgumentException(){
        //1<<11 <<20  > int_max
        BufferSize.mb(1<<11);
    }
    @Test    
    public void largestUnderMaxMB(){
        assertEquals(1073741824, BufferSize.mb(1<<10));
    }
    
    @Test(expected = IllegalArgumentException.class)    
    public void overMaxMBShouldThrowIllegalArgumentException(){
        BufferSize.mb(Integer.MAX_VALUE);
    }
    
    @Test
    public void cachedSizesMB(){
        int mb = 1024*1024;
        for(int i=0; i<65; i++){
            int expected = mb *i;
            assertEquals(""+i, expected, BufferSize.mb(i));
        }
    }
}
