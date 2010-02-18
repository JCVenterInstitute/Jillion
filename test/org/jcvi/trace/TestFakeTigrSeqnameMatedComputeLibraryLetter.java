/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestFakeTigrSeqnameMatedComputeLibraryLetter {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        for(int i=0; i<10; i++){
            data.add(new Object[]{i, (""+i).charAt(0)});
        }
        for(int i=0; i<26; i++){
            data.add(new Object[]{i+10, (char)(i+'A')});
        }
        data.add(new Object[]{10, 'A'});
        data.add(new Object[]{20, 'K'});
        return data;
    }
    
    private int position;
    private char expectedLetter;
    
    public TestFakeTigrSeqnameMatedComputeLibraryLetter(int position, char expectedLetter){
        this.position = position;
        this.expectedLetter = expectedLetter;
    }
    @Test
    public void computeLibraryLetter(){
        assertEquals(expectedLetter,
                FakeTigrSeqnameMatedTraceIdGenerator.computeLibraryLetterFrom(position));
    }
    @Test
    public void computeLibraryPosition(){
        assertEquals(position,
                FakeTigrSeqnameMatedTraceIdGenerator.computeLibraryPositionFrom(expectedLetter));
    }
}
