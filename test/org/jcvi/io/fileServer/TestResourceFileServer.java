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
 * Created on Aug 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public class TestResourceFileServer extends AbstractTestFileServer{

    ResourceFileServer resourceFileServer = new ResourceFileServer(TestResourceFileServer.class, "files");

    @Override
    protected FileServer createFileServer(File file)
            throws IOException {
        return resourceFileServer;
    }
    
    @Test
    public void nullRootPath() throws IOException{
        ResourceFileServer sut = new ResourceFileServer(TestResourceFileServer.class);
        String path = "files/README.txt";
        File expectedFile = new File(TestResourceFileServer.class.getResource(path).getFile());
        File actualFile = sut.getFile(path);
        assertEquals(expectedFile, actualFile);

    }
   
}
