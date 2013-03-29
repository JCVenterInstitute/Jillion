/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.core.util.iter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * {@code AbstractBlockingStreamingIterator}
 * is a {@link StreamingIterator} that is
 * meant be used to iterate over a large computationally intensive
 * or memory intensive process.  Only 1 record (the next record
 * to be returned by {@link Iterator#next()}) will be referenced by this class.
 * This class will perform the intensive computation in a background Thread
 * and will block that Thread until the next item to be iterated 
 * is required.  Elements to be iterated over are placed onto the blocking
 * iterator by {@link #blockingPut(Object)}.
 * <strong>WARNING:</strong> Client code must be very careful
 * to always make sure that this iterator is closed when finished.
 * If the iterator does not reach the end or
 * if the iterator is not explicitly closed via the {@link #close()}
 * method, then the background thread will block forever. This is 
 * especially true in situations when Exceptions are thrown by other objects.
 * Please make sure {@link StreamingIterator}s are closed in finally blocks.
 * <p/>
 * The background thread is not started until the {@link #start()}
 * method is called.  This allows for subclasses to set up
 * and initialize themselves in either the constructor
 * or other pre-process steps.
 * <p/>
 * Example:
 * <pre> 

     //Example implementation 
     //will compute the approximate value of &pi;.
     //Each {@link BigDecimal} returned by this iterator
     //will be a more accurate approximation.
    //This class computes the value of &pi; using the Madhava-Leibniz series:
    //&pi; = 4 &sum; ( (-1)<sup>k</sup> / (2k + 1) )
     
    class ApproximatePiIterator extends AbstractBlockingStreamingIterator&lt;BigDecimal&gt;{
        private final int numOfIterations;
        
        public ApproximatePiIterator(int numOfIterations) {
            this.numOfIterations = numOfIterations;
        }
        
         
        protected void backgroundThreadRunMethod() throws RuntimeException {
           
            this.blockingPut(FOUR);
            
            BigDecimal currentValue = BigDecimal.valueOf(1);
            for(int i=1; i&lt;numOfIterations; i++){
                BigDecimal x = BigDecimal.valueOf(1D/(2*i+1));
                if(i%2==0){
                    currentValue = currentValue.add(x);
                }else{
                    currentValue = currentValue.subtract(x);
                }
                this.blockingPut(currentValue.multiply(FOUR));
            }
        }
        
    }
    
    public static void main(String[] args){
        ApproximatePiIterator approxPi = new ApproximatePiIterator(1_000_000);
        approxPi.start();
        try{
           while(approxPi.hasNext()){
              System.out.println(approxPi.next());
           }
        }finally{
           IOUtil.closeAndIgnoreErrors(approxPi.close());
        }
    }
 * </pre>
 * 
 * @author dkatzel
 *
 * @param <T> the type of elements being iterated over.
 */
public abstract class AbstractBlockingStreamingIterator<T> implements StreamingIterator<T>{

	private final Object endOfFileToken = new Object();
    private final BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(1);
    private Object nextRecord=null;
    private volatile boolean isClosed=false;
    
    private volatile RuntimeException uncaughtException;

    /**
     * @throws InterruptedException 
     * 
     */
    private void blockingGetNextRecord(){
        if(!isClosed){
            try {
				nextRecord = queue.take();
			} catch (InterruptedException e) {
				//assume interrupted is closed?
				IOUtil.closeAndIgnoreErrors(this);
			}     
        }
    }
    /**
     * This starts the visiting in a separate thread.
     * This method must be called before a client may call
     * {@link #hasNext()} or {@link #next()}.
     */
    public void start(){
      final IteratorThread iteratorThread =new IteratorThread();
        iteratorThread.start();
        //if the vm exits while we are still blocking
        //we will run forever...
        //add shutdown hook to try to kill ourselves
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                iteratorThread.kill();
            }
        });
        blockingGetNextRecord();
		
    }
    /**
     * This is the method that is called by the {@link Thread#run()}
     * instance in the background thread created and started 
     * in {@link #start()}.  Please set up and start the items
     * being iterated over.  Make sure
     * to call
     * {@link #blockingPut(Object)} when appropriate. 
     * Any uncaught exceptions will be thrown by the 
     * thread using the iterator the next time
     * {@link #next()} or {@link #hasNext()}
     * is used.
     * @throws RuntimeException - any exception not caught or handled by this background
     * thread should throw a RunTimeException which will be thrown
     * on the next call to {@link #next()} or {@link #hasNext()}.
     * 
     */
    protected abstract void backgroundThreadRunMethod() throws RuntimeException;
	/**
	 * This method must be called when the visitor has finished
	 * visiting in order to let the iterator know that there
	 * are no more records left to block for.
	 */
    private void finishedIterating(){
    	blockingPut(endOfFileToken);
    }
    /**
     * Put the given object onto the queue to be iterated over
     * and block until there is room for it in the queue.
     * @param obj the object to put.
     */
    public final void blockingPut(Object obj){
        if(!isClosed){
	        try {
	            queue.put(obj);
	        } catch (InterruptedException e) {
	            throw new IllegalStateException(e);
	        }
	    }
    }
    /**
     * 
    * {@inheritDoc}
    * <p/>
    * Not supported.
    * @throws UnsupportedOperationException always.
     */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();		
	}

	/**
    * {@inheritDoc}
    */
    @Override
    public final void close() throws IOException {
        isClosed=true;
        nextRecord=endOfFileToken;
        queue.clear();	        
    }
	    
	/**
	 * Safety-net to close the iterator
	 * in case it hasn't been closed already.
	 * Client code should always explicitly
	 * close a {@link StreamingIterator}
	 * but this finalizer is used just in case.
	 * This method can not be relied upon 
	 * since an object is not guaranteed to 
	 * get finalized by the garbage collector.
	 */
    @Override
	protected void finalize() throws IOException {
    	if(!isClosed){
    		close();
    	}
	}
		/**
	    * {@inheritDoc}
	    */
	    @Override
	    public final T next() {
	        if(!isClosed && uncaughtException !=null){
	            throw uncaughtException;
	        }
	        if(!hasNext()){
	            throw new NoSuchElementException("no records");
	        }
	        //if we are here then nextRecord must
	        //be type T so we can safely cast
	        @SuppressWarnings("unchecked")
			T next = (T)nextRecord;
            blockingGetNextRecord();
	        
	        return next;
	    }

	     @Override
	     public final boolean hasNext() {
	         if(!isClosed && uncaughtException !=null){
                throw uncaughtException;
             }
	         
	        return !isClosed && nextRecord !=endOfFileToken;
	     }
	    
	     
	     /**
	 	 * @return the isClosed
	 	 */
	     public final boolean isClosed() {
	 		return isClosed;
	 	}

	     /**
	      * Background thread that runs this iterator
	      * and can be killed
	      * @author dkatzel
	      *
	      *
	      */
	     private class IteratorThread extends Thread{
	         /**
	          * closes the blocked iterator.
	          */
	         public void kill(){
	             IOUtil.closeAndIgnoreErrors(AbstractBlockingStreamingIterator.this);                
	         }
	         @Override
	         public void run() {
	             try{
	                 backgroundThreadRunMethod();
	             }catch(RuntimeException e){
	                 AbstractBlockingStreamingIterator.this.uncaughtException = e;	                 
	             }finally{
	                 finishedIterating();
	             }
	         }
	     }
}
