/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Dec 5, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;

import static org.junit.Assert.*;

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
    protected abstract int getNumberOfFlowgrams() throws Exception;
   protected abstract Flowgram getFlowgram(String id) throws Exception;
   protected abstract void parseSff(File f) throws Exception;
}
