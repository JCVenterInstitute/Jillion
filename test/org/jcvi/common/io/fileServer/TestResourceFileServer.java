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
package org.jcvi.common.io.fileServer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import org.jcvi.common.io.fileServer.FileServer;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;


public class TestResourceFileServer extends AbstractTestFileServer{
    @Override
    protected FileServer createFileServer(File file)
            throws IOException {
        return new ResourceFileServer(TestResourceFileServer.class,file);

    }
    
    @Test
    public void nullRootPath() throws IOException{
        ResourceFileServer sut = new ResourceFileServer(TestResourceFileServer.class);
        String path = "files/README.txt";
        File expectedFile = new File(URLDecoder.decode(TestResourceFileServer.class.getResource(path).getFile(), "UTF-8"));
        File actualFile = sut.getFile(path);
        assertEquals(expectedFile.getAbsolutePath(), 
        		URLDecoder.decode(actualFile.getAbsolutePath(), "UTF-8"));

    }
   
}
