/*
 * Created on Aug 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public abstract class AbstractTestDirectoryFileServer extends AbstractTestFileServer{

    @Test(expected= NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfRootDirisNull() throws IOException{
        createFileServer(null);
    }
    
    @Test
    public void getFileThatDoesNotExistShouldThrowIOException(){
        try{
            sut.getFile("missingFile");
            fail("should throw IOException if file does not exist");
        }catch(IOException e){
            assertEquals("file missingFile does not exist", e.getMessage());
        }
    }
}
