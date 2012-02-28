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
 * Created on Jul 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.io.fileServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.io.fileServer.FileServer;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class  AbstractTestFileServer {
	ResourceFileServer RESOURCES = new ResourceFileServer(AbstractTestFileServer.class);
	
    protected File PATH_TO_ROOT_DIR;
    protected FileServer sut;
    
    protected abstract FileServer createFileServer(File file) throws IOException;
   
    @Before
    public void setup() throws IOException{
    	PATH_TO_ROOT_DIR = RESOURCES.getFile("files");
        sut = createFileServer(PATH_TO_ROOT_DIR);
    }
   
    
    
    @Test
    public void supportsGettingFileObjects(){
        assertTrue(sut.supportsGettingFileObjects());
    }
    @Test
    public void contains() throws IOException{
        assertTrue(sut.contains("README.txt"));
        assertFalse(sut.contains("missingFile"));
    }
    @Test
    public void getFile() throws IOException{
        File expectedFile = new File(PATH_TO_ROOT_DIR + File.separator + "README.txt");
        File actualFile = sut.getFile("README.txt");
        assertEquals(expectedFile, actualFile);
    }
    
    @Test
    public void getFileAfterClosingShouldThrowIllegalStateException() throws IOException{
        sut.close();
        try{
            sut.getFile("README.txt");
            fail("should throw IllegalStateException when trying to get from a closed file server");
        }
        catch(IllegalStateException e){
            assertEquals("DirectoryFileServer is closed", e.getMessage());
        }
    }
    
    @Test
    public void getFileAsStream() throws FileNotFoundException, IOException{
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        File expectedFile = new File(PATH_TO_ROOT_DIR + File.separator + "README.txt");
        IOUtil.copy(new FileInputStream(expectedFile), expected);
        
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        IOUtil.copy(sut.getFileAsStream("README.txt"), actual);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }
}
