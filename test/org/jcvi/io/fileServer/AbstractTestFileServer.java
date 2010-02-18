/*
 * Created on Jul 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.io.IOUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class  AbstractTestFileServer {

    protected final String PATH_TO_ROOT_DIR = AbstractTestFileServer.class.getResource("files").getFile();
    protected FileServer sut;
    
    protected abstract FileServer createFileServer(File file) throws IOException;
    
    @Before
    public void setup() throws IOException{
        sut = createFileServer(new File(this.PATH_TO_ROOT_DIR));
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
        IOUtil.writeToOutputStream(new FileInputStream(expectedFile), expected);
        
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        IOUtil.writeToOutputStream(sut.getFileAsStream("README.txt"), actual);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }
}
