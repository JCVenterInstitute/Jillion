/*
 * Created on Sep 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestTraceTypeCode {

    @Test
    public void getTraceTypeCodeFor(){
        for(TraceTypeCode code : TraceTypeCode.values()){
            String toString = code.toString();
            assertEquals(code, TraceTypeCode.getTraceTypeCodeFor(toString));
            assertEquals("lowercase",code, TraceTypeCode.getTraceTypeCodeFor(toString.toLowerCase()));
            assertEquals("uppercase",code, TraceTypeCode.getTraceTypeCodeFor(toString.toUpperCase()));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void unknownCodeShouldThrowIllegalArgumentException(){
        TraceTypeCode.getTraceTypeCodeFor("unknown code");
    }
    
    @Test(expected = NullPointerException.class)
    public void nullCodeShouldThrowNullPointerException(){
        TraceTypeCode.getTraceTypeCodeFor(null);
    }
}
