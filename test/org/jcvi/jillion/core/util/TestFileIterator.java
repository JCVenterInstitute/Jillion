/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
