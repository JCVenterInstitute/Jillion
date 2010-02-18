/*
 * Created on Aug 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestBreadthFirstFileIterator {

   ResourceFileServer fileServer = new ResourceFileServer(TestBreadthFirstFileIterator.class);
    
  
    
    @Test
    public void shouldthrowNoSuchElementExceptionWhenEmpty() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIterator(fileServer.getFile("files"),false);
        while(sut.hasNext()){
            sut.next();
        }
        try{
            sut.next();
            fail("should throw no such element exception");
        }catch(NoSuchElementException e){
            assertEquals("no more files", e.getMessage());
        }
    }
    
    @Test
    public void iterateFilesOnly() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIterator(fileServer.getFile("files"),false);
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file1"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file2"),sut.next()); 
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/file3"),sut.next());
        assertTrue(sut.hasNext());
        
        assertEquals(fileServer.getFile("files/siblingSubDir/file6"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/subSubDir2/file5"),sut.next());
       
        assertTrue(sut.hasNext());                
        assertEquals(fileServer.getFile("files/subDir/subSubDir/file4"),sut.next());
      
        assertFalse(sut.hasNext());
        
    }
    @Test
    public void iterateIncludeDirs() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIterator(fileServer.getFile("files"),true);
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file1"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file2"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/siblingSubDir/"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/"),sut.next());
        
        
        assertTrue(sut.hasNext());  
        assertEquals(fileServer.getFile("files/subDir/file3"),sut.next());
        assertTrue(sut.hasNext());        
        assertEquals(fileServer.getFile("files/subDir/subSubDir/"),sut.next());
        assertTrue(sut.hasNext()); 
        assertEquals(fileServer.getFile("files/subDir/subSubDir2/"),sut.next());
        
        assertTrue(sut.hasNext());        
              
               
        assertEquals(fileServer.getFile("files/siblingSubDir/file6"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/subSubDir2/file5"),sut.next());
        
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/subSubDir/file4"),sut.next());
        assertFalse(sut.hasNext());
    }
    
    @Test
    public void removeShouldThrowUnsupportedOperationException() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIterator(fileServer.getFile("files"),false);
        try{
            sut.remove();
            fail("should throw UnsupportedOperationException");
        }catch(UnsupportedOperationException e){
            assertEquals("can not remove", e.getMessage());
        }
    }
}
