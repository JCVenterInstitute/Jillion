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

package org.jcvi.command;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestCommandUtils {

    @Test
    public void escapeNoUnsafeCharactersShouldReturnSameString(){
        String safe = "safe";
        assertEquals(safe, CommandUtils.escape(safe));
    }
    @Test
    public void escapeSpacesInStringShouldQuoteEntireStringWithSingleQuotes(){
        String sentence = "To be or not to be";
        
        assertEquals("'"+ sentence + "'", CommandUtils.escape(sentence));
    }
    
    @Test
    public void escapeSlash(){
        assertEquals("either\\\\or", CommandUtils.escape("either\\or"));
    }
    @Test
    public void escapeSingleQuote(){
        assertEquals("don\\'t", CommandUtils.escape("don't"));
    }
    @Test
    public void escape(){
        assertEquals("'I don\\'t want either\\\\or'", CommandUtils.escape("I don't want either\\or"));
    }
    
    @Test
    public void waitFor() throws InterruptedException{
        int ret=2;
        Process mockProcess = createMock(Process.class);
        expect(mockProcess.waitFor()).andReturn(ret);
        replay(mockProcess);
        assertEquals(ret,CommandUtils.waitFor(mockProcess));
        verify(mockProcess);
    }
    @Test
    public void waitForInterruptedShouldDestroyProcessAndReturnNeg1() throws InterruptedException{
        Process mockProcess = createMock(Process.class);
        expect(mockProcess.waitFor()).andThrow(new InterruptedException("expected"));           
        mockProcess.destroy();
        
        replay(mockProcess);
        assertEquals(-1,CommandUtils.waitFor(mockProcess));
        verify(mockProcess);
    }
}
