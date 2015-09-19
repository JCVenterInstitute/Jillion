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
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.FileIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public class TestFileIterator {

    ResourceHelper fileServer = new ResourceHelper(TestFileIterator.class);
    
    
    @Test
     public void doNotRecurse() throws IOException{
         Iterator<File> sut = FileIterator.createNonRecursiveFileIteratorBuilder(fileServer.getFile("files")).build();
         assertTrue(sut.hasNext());
         assertEquals(fileServer.getFile("files/file1"),sut.next());
         assertTrue(sut.hasNext());
         assertEquals(fileServer.getFile("files/file2"),sut.next());
         
         assertFalse(sut.hasNext());
         
     }
    @Test
    public void additionalFileFilter() throws IOException{
        
        Iterator<File> sut = FileIterator.createNonRecursiveFileIteratorBuilder(fileServer.getFile("files"))
                    .fileFilter(FileIteratorTestUtil.FILE_FILTER_ANYTHING_THAT_DOESNT_END_WITH_2)
                    .build();
        assertTrue(sut.hasNext());
        assertEquals(fileServer.getFile("files/file2"),sut.next());
        
        assertFalse(sut.hasNext());
        
    }
     @Test
     public void shouldthrowNoSuchElementExceptionWhenEmpty() throws IOException{
         Iterator<File> sut = FileIterator.createNonRecursiveFileIteratorBuilder(fileServer.getFile("files")).build();
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
         Iterator<File> sut = FileIterator.createNonRecursiveFileIteratorBuilder(fileServer.getFile("files")).build();
         try{
             sut.remove();
             fail("should throw UnsupportedOperationException");
         }catch(UnsupportedOperationException e){
             assertEquals("can not remove", e.getMessage());
         }
     }
}
