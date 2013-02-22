/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
