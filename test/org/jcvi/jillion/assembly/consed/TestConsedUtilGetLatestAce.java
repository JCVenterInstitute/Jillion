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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed;

import java.io.File;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedUtilGetLatestAce {

    File mockEditDir;
    
    @Before
    public void setupMockEditDir() throws SecurityException, NoSuchMethodException{
        //creates a mock File (directory) object which thinks its located at root
        //and allows us to mock the list() method so we can
        //change which files are under it.
        mockEditDir = createMockBuilder(File.class)
        .withConstructor(String.class)
        .withArgs("/")
        .addMockedMethod(File.class.getMethod("list"))
                .createMock();
    }
    
    @Test
    public void noAceFilesShouldReturnNull(){
        expect(mockEditDir.list())
        .andReturn(new String[]{});
        replay(mockEditDir);
        assertNull(ConsedUtil.getLatestAceFile(mockEditDir, "prefix"));
        verify(mockEditDir);
    }
    
    @Test
    public void oneAceShouldReturnIt(){
        expect(mockEditDir.list())
        .andReturn(new String[]{
            "prefix.ace.1",});
        
        replay(mockEditDir);
        assertEquals("prefix.ace.1",
                ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
    @Test
    public void multipleAcesShouldReturnHighestVersion(){

        expect(mockEditDir.list())
        .andReturn(new String[]{
            "prefix.ace.1",
            "prefix.ace.2",
            "prefix.ace.3"});
        
        replay(mockEditDir);
        assertEquals("prefix.ace.3",ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
   
    @Test
    public void otherPrefixedAceFilesShouldBeIgnored(){
        expect(mockEditDir.list())
                        .andReturn(new String[]{
                            "prefix.ace.1",
                            "prefix.ace.2",
                            "otherPrefix.ace.3"});
        
        replay(mockEditDir);
        assertEquals("prefix.ace.2",ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
    
    @Test
    public void otherSuffixedFilesButWhichContainAceVersionPrefixInNameShouldBeIgnored(){
        expect(mockEditDir.list())
        .andReturn(new String[]{
            "prefix.ace.1",
            "prefix.ace.2",
            "prefix.ace.2.consensus.fasta"});
        
        replay(mockEditDir);
        assertEquals("prefix.ace.2",ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
}
