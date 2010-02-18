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
public class TestFakeTigrSeqnameWellPosition {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        int counter = 0;
        for(int i=0; i<26; i++){
            char row =(char)(i+'A');
            for(int column= 0; column<100; column++){
                data.add(new Object[]{counter++, String.format("%s%02d",row, column)});
            }
        }
        return data;
    }
    
    private int position;
    private String expectedWell;
    
    public TestFakeTigrSeqnameWellPosition(int position, String well){
        this.position = position;
        this.expectedWell = well;
    }
    
    @Test
    public void computeWellPosition(){
        assertEquals(expectedWell, FakeTigrSeqnameMatedTraceIdGenerator.computeWellPositionFrom(position));
    }
}
