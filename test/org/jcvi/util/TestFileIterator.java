/*
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;

public class TestFileIterator {

    ResourceFileServer fileServer = new ResourceFileServer(TestFileIterator.class);
    
    
    @Test
     public void doNotRecurse() throws IOException{
         Iterator<File> sut = FileIterator.createFileIterator(fileServer.getFile("files"),false);
         assertTrue(sut.hasNext());
         assertEquals(fileServer.getFile("files/file1"),sut.next());
         assertTrue(sut.hasNext());
         assertEquals(fileServer.getFile("files/file2"),sut.next());
         
         assertFalse(sut.hasNext());
         
     }
     
     @Test
     public void shouldthrowNoSuchElementExceptionWhenEmpty() throws IOException{
         Iterator<File> sut = FileIterator.createFileIterator(fileServer.getFile("files"),false);
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
     public void removeShouldThrowUnsupportedOperationException() throws IOException{
         Iterator<File> sut = FileIterator.createFileIterator(fileServer.getFile("files"),false);
         try{
             sut.remove();
             fail("should throw UnsupportedOperationException");
         }catch(UnsupportedOperationException e){
             assertEquals("can not remove", e.getMessage());
         }
     }
}
