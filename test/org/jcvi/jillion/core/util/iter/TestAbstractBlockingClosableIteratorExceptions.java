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
package org.jcvi.jillion.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

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
    public void backgroundThreadThrowsExceptionShouldCatchOnHasNextOrNext() throws InterruptedException, BrokenBarrierException{
       try( TestDouble iter = new TestDouble(names, 2)){
	        iter.start();  
	        //depending on thread scheduling
	        //the throw can happen on either of the 
	        //next 2 next() or hasNext() calls
	        //the easiest way to test is to just wrap everything 
	        //in a try.
	        try{
		       iter.next();
		       iter.next();
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
        
	}catch(ExpectedException e){
		e.printStackTrace();
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
