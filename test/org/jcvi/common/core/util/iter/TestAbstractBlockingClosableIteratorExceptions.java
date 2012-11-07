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

package org.jcvi.common.core.util.iter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.impl.AbstractBlockingStreamingIterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author dkatzel
 *
 *
 */
public class TestAbstractBlockingClosableIteratorExceptions {
    private class ExpectedException extends RuntimeException{

        /**
         * 
         */
        private static final long serialVersionUID = -7886752742909496815L;
        
    }
    
    private List<String> names = Arrays.asList("moe","larry","curly", "shemp","curly joe", "joe");
    
    private class TestDouble extends AbstractBlockingStreamingIterator<String>{
        private final int numberOfRecordsUntilThrowException;
        private final List<String> list;
        
        public TestDouble(List<String> list){
            this(list, list.size()+1);
        }
        /**
         * 
         * @param list
         * @param numberOfRecordsUntilThrowException
         */
        public TestDouble(List<String> list,
                int numberOfRecordsUntilThrowException) {
            this.numberOfRecordsUntilThrowException = numberOfRecordsUntilThrowException;
            this.list = list;
        }



        /**
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() {
            for(int i=0; i<numberOfRecordsUntilThrowException && i<list.size(); i++){
                this.blockingPut(list.get(i));
            }
            if(numberOfRecordsUntilThrowException < list.size()){
                throw new ExpectedException();
            }
            
        }
        
    }
    
    @Test
    public void iterateOverAllNames(){
        TestDouble iter = new TestDouble(names);
        iter.start();        
        Iterator<String> expectedIter = names.iterator();
        while(expectedIter.hasNext()){
            assertTrue(iter.hasNext());
            assertEquals(expectedIter.next(), iter.next());
        }
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void backgroundThreadThrowsExceptionShouldCatchOnHasNextOrNext(){
        TestDouble iter = new TestDouble(names, 3);
        iter.start();  
        
        iter.next(); //moe
        iter.next(); //larry
        //should throw exception here
        try{
            iter.hasNext();
        }catch(ExpectedException e){
            //expected
        }
        
        try{
            iter.next();
        }catch(ExpectedException e){
          //expected
        }
    }
    
    @Test
    public void closeBeforeExceptionShouldCloseWithoutProblems(){
        TestDouble iter = new TestDouble(names, 3);
        iter.start();  
        
        iter.next(); //moe
        iter.next(); //larry
        IOUtil.closeAndIgnoreErrors(iter);
        assertFalse(iter.hasNext());
        
    }
}
