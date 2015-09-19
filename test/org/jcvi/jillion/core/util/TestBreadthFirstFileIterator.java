/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Aug 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.FileIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestBreadthFirstFileIterator {

   ResourceHelper fileServer = new ResourceHelper(TestBreadthFirstFileIterator.class);
    
  
    
    @Test
    public void shouldthrowNoSuchElementExceptionWhenEmpty() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIteratorBuilder(fileServer.getFile("files")).build();
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
        Iterator<File> sut = FileIterator.createBreadthFirstFileIteratorBuilder(fileServer.getFile("files")).build();
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file1"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file2"),sut.next()); 
        assertTrue(sut.hasNext());
        
        assertEquals(fileServer.getFile("files/siblingSubDir/file6"),sut.next());
        assertTrue(sut.hasNext());
        
        assertEquals(fileServer.getFile("files/subDir/file3"),sut.next());
        assertTrue(sut.hasNext());
        
        assertTrue(sut.hasNext());                
        assertEquals(fileServer.getFile("files/subDir/subSubDir/file4"),sut.next());
      
        assertEquals(fileServer.getFile("files/subDir/subSubDir2/file5"),sut.next());
       
       
        assertFalse(sut.hasNext());
        
    }
    @Test
    public void iterateIncludeDirs() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIteratorBuilder(fileServer.getFile("files"))
                            .includeDirectories(true)
                            .build();
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file1"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file2"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/siblingSubDir/"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/"),sut.next());
        assertTrue(sut.hasNext());  
        
        assertTrue(sut.hasNext()); 
        assertEquals(fileServer.getFile("files/siblingSubDir/file6"),sut.next());
        
        
        assertEquals(fileServer.getFile("files/subDir/file3"),sut.next());
        assertTrue(sut.hasNext());        
        assertEquals(fileServer.getFile("files/subDir/subSubDir/"),sut.next());
        assertTrue(sut.hasNext()); 
        assertEquals(fileServer.getFile("files/subDir/subSubDir2/"),sut.next());
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/subSubDir/file4"),sut.next());
        
       assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/subDir/subSubDir2/file5"),sut.next());
        
       assertFalse(sut.hasNext());
    }
    
    @Test
    public void additionalFilter() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIteratorBuilder(fileServer.getFile("files"))
                            .includeDirectories(true)
                            .fileFilter(FileIteratorTestUtil.FILE_FILTER_ANYTHING_THAT_DOESNT_END_WITH_2)
                            .build();
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file2"),sut.next());
        assertEquals(fileServer.getFile("files/subDir/subSubDir2/"),sut.next());
        assertFalse(sut.hasNext());
    }
    
    @Test
    public void removeShouldThrowUnsupportedOperationException() throws IOException{
        Iterator<File> sut = FileIterator.createBreadthFirstFileIteratorBuilder(fileServer.getFile("files")).build();
        try{
            sut.remove();
            fail("should throw UnsupportedOperationException");
        }catch(UnsupportedOperationException e){
            assertEquals("can not remove", e.getMessage());
        }
    }
}
