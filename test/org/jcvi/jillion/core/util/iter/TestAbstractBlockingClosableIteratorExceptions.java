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
package org.jcvi.jillion.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.junit.Test;

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
        
        public ExpectedException(String message){
        	super(message);
        }
        
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
        	int i=0;
            for(; i<numberOfRecordsUntilThrowException && i<list.size(); i++){
                String obj = list.get(i);
				this.blockingPut(obj);
            }
            if(numberOfRecordsUntilThrowException < list.size()){
                throw new ExpectedException(list.get(i));
            }
            
        }
        
    }
    
    @Test
    public void iterateOverAllNames(){
        try(TestDouble iter = new TestDouble(names)){
	        iter.start();        
	        Iterator<String> expectedIter = names.iterator();
	        while(expectedIter.hasNext()){
	            assertTrue(iter.hasNext());
	            assertEquals(expectedIter.next(), iter.next());
	        }
	        assertFalse(iter.hasNext());
        }
    }
    
    @Test
    public void backgroundThreadThrowsExceptionShouldCatchOnHasNextOrNext() throws InterruptedException{
       try( TestDouble iter = new TestDouble(names, 2)){
	        iter.start();  
	        
	        iter.next(); //moe
	        //depending on thread scheduling
	        //the throw can happen on either of the 
	        //next 2 next() or hasNext() calls
	        //the easiest way to test is to just wrap both 
	        //in a try.
	        try{
	        	Thread.sleep(1000);
	        	 iter.next(); 
	        	 Thread.sleep(1000);
	             iter.next();
	            fail("should throw exception");
	        }catch(ExpectedException e){
	            //expected
	        }
	        
	        try{
	            iter.next();
	            fail("should throw exception");
	        }catch(ExpectedException e){
	          //expected
	        }
        
	}
    }
    
    @Test
    public void closeBeforeExceptionShouldCloseWithoutProblems(){
        try(TestDouble iter = new TestDouble(names, 3)){
	        iter.start();  
	        
	        iter.next(); //moe
	       // iter.next(); //larry
	        IOUtil.closeAndIgnoreErrors(iter);
	        assertFalse(iter.hasNext());
        }
    }
}
