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
 * Created on Dec 5, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public abstract class  TestReadExampleSffFile extends AbstractTestExampleSffFile{
    
	protected File sffFileToUse(){
		return SFF_FILE;
	}
    @Before
    public void populate() throws Exception{
        parseSff(sffFileToUse());
        
    }
    @Test
    public void readfile() throws Exception{

        
        assertEquals(5, getNumberOfFlowgrams());
        
        assertEquals(FF585OX02HCMO2, getFlowgram("FF585OX02HCMO2"));
        assertEquals(FF585OX02HCD8G, getFlowgram("FF585OX02HCD8G"));
        assertEquals(FF585OX02FNE4N, getFlowgram("FF585OX02FNE4N"));
        assertEquals(FF585OX02GMGGN, getFlowgram("FF585OX02GMGGN"));
        assertEquals(FF585OX02FHO5X,getFlowgram("FF585OX02FHO5X"));
    }
    
    protected abstract long getNumberOfFlowgrams() throws Exception;
   protected abstract SffFlowgram getFlowgram(String id) throws Exception;
   protected abstract void parseSff(File f) throws Exception;
}
