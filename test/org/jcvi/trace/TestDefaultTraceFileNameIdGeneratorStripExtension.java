/*
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultTraceFileNameIdGeneratorStripExtension {

    DefaultTraceFileNameIdGenerator sut = new DefaultTraceFileNameIdGenerator(true);
    
    @Test
    public void noExtension(){
        String fileName = "fileName";
        assertEquals(fileName, sut.generateIdFor(fileName));
    }
    @Test
    public void singleExt(){
        String fileName = "fileName.ztr";
        assertEquals("fileName", sut.generateIdFor(fileName));
    }
    @Test
    public void multExtShouldOnlyStripLast(){
        String fileName = "fileName.tar.gz";
        assertEquals("fileName.tar", sut.generateIdFor(fileName));
    }
    @Test(expected = IllegalArgumentException.class)
    public void onlyHasOnePeriodAtStartShouldThrowIllegalArgumentException(){
        String fileName = ".uhoh";
        sut.generateIdFor(fileName);
    }
    @Test
    public void hasPeriodAtBeginningAndElsewhere(){
        String fileName =".this.ok";
        assertEquals(".this", sut.generateIdFor(fileName));
    }
}
