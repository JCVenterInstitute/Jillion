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

package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.jcvi.io.fileServer.ResourceFileServer;
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
    public void setupMockEditDir(){
        mockEditDir = createMock(File.class);
    }
    
    @Test
    public void noAceFilesShouldReturnNull(){
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
            .andReturn(new File[0]);
        replay(mockEditDir);
        assertNull(ConsedUtil.getLatestAceFile(mockEditDir, "prefix"));
        verify(mockEditDir);
    }
    
    @Test
    public void oneAceShouldReturnIt(){
        File aceFile = createFakeFile("prefix.ace.1");
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
                        .andReturn(new File[]{aceFile});
        
        replay(mockEditDir);
        assertEquals(aceFile,ConsedUtil.getLatestAceFile(mockEditDir, "prefix"));
        verify(mockEditDir);
    }
    @Test
    public void multipleAcesShouldReturnHighestVersion(){
        File ace1 = createFakeFile("prefix.ace.1");
        File ace2 = createFakeFile("prefix.ace.2");
        File ace3 = createFakeFile("prefix.ace.3");
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
                        .andReturn(new File[]{ace2,ace3,ace1});
        
        replay(mockEditDir);
        assertEquals(ace3,ConsedUtil.getLatestAceFile(mockEditDir, "prefix"));
        verify(mockEditDir);
    }
   
    @Test
    public void ignoreOtherPrefixedAceFiles() throws IOException{
        ResourceFileServer resources = new ResourceFileServer(TestConsedUtilGetLatestAce.class);
        File editDir = resources.getFile("edit_dir");
        File expectedHighestVersion = resources.getFile("edit_dir/prefix.ace.3");
        assertEquals(expectedHighestVersion,ConsedUtil.getLatestAceFile(editDir, "prefix"));
    }
    
    private File createFakeFile(String filename){
        File fakeFile = createMock(File.class);
        expect(fakeFile.getName()).andStubReturn(filename);
        replay(fakeFile);
        return fakeFile;
    }
}
