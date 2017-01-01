package org.jcvi.jillion.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestThrowingStream {

    List<String> names = Arrays.asList("larry", "curly", "moe");
    @Test
    public void completesNormally(){
        Set<String> input = new HashSet<>(names);
        ThrowingStream<String> stream = ThrowingStream.asThrowingStream(input.stream());
        
        Set<String> actual = new HashSet<>();
        
        stream.throwingForEach(e -> actual.add(e));
        assertEquals(input, actual);
    }
    
    @Test(expected = IOException.class)
    public void throwCheckedException() throws IOException{
        ThrowingStream.asThrowingStream(names.stream())
                        .throwingForEach(e -> { throw new IOException("test");});
    }
    
    @Test
    public void throwAfter2nd(){
        SingleThreadAdder counter = new SingleThreadAdder();
        
        try {
            ThrowingStream.asThrowingStream(names.stream().sequential())
            .throwingForEach(e -> { 
                if( counter.intValue() < 2){
                    counter.increment();
                }else{
                    throw new IOException("test");
                }
                
            });
            fail("should have thrown");
        } catch (IOException e) {
           assertEquals(2, counter.intValue());
        }
    }
    
    @Test
    public void throwAfter2ndOrdered(){
        List<String> namesSoFar = new ArrayList<>();
        try {
            ThrowingStream.asThrowingStream(names.stream().sequential())
            .throwingForEachOrdered(e -> { 
                if( namesSoFar.size() < 2){
                    namesSoFar.add(e);
                }else{
                    throw new IOException("test");
                }
                
            });
            fail("should have thrown");
        } catch (IOException e) {
           assertEquals(2, namesSoFar.size());
           assertEquals(names.subList(0, 2), namesSoFar);
        }
    }
}
