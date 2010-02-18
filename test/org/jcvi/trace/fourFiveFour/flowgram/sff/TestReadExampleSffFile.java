/*
 * Created on Dec 5, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

import static org.junit.Assert.*;

public abstract class  TestReadExampleSffFile extends AbstractTestExampleSffFile{
    
    @Before
    public void populate() throws Exception{
        parseSff(SFF_FILE);
        
    }
    @Test
    public void readfile() throws Exception{

        
        assertEquals(5, getNumberOfFlowgrams());
        
        assertSameValues(FF585OX02HCMO2, getFlowgram("FF585OX02HCMO2"));
        assertSameValues(FF585OX02HCD8G, getFlowgram("FF585OX02HCD8G"));
        assertSameValues(FF585OX02FNE4N, getFlowgram("FF585OX02FNE4N"));
        assertSameValues(FF585OX02GMGGN, getFlowgram("FF585OX02GMGGN"));
        assertSameValues(FF585OX02FHO5X,getFlowgram("FF585OX02FHO5X"));
    }
    protected abstract int getNumberOfFlowgrams() throws Exception;
   protected abstract Flowgram getFlowgram(String id) throws Exception;
   protected abstract void parseSff(File f) throws Exception;
}
