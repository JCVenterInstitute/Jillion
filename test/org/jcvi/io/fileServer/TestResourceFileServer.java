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
