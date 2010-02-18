/*
 * Created on Jul 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TestReadOnlyDirectoryFileServer extends
        AbstractTestDirectoryFileServer {

    @Override
    protected DirectoryFileServer createFileServer(File dir) throws IOException {
        return DirectoryFileServer.createReadOnlyDirectoryFileServer(dir);
    }
    
    @Test(expected= IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfRootDirDoesNotExist() throws IOException{
        createFileServer(new File("does not exist"));
    }

}
