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

package org.jcvi.common.core.util;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jcvi.io.IOUtil;

/**
 * {@code AbstractBlockingCloseableIterator}
 * is a {@link CloseableIterator} that is
 * meant be used with a {@link FileVisitor}
 * so that a client may iterate over the records
 * being visited.  This class will block (in a separate
 * Thread) so that the subject being visited
 * only has to be visited once through over the lifetime
 * of this iterator.
 * @author dkatzel
 *
 * @param <T> the records being iterated over.
 */
public abstract class AbstractBlockingCloseableIterator<T> implements CloseableIterator<T>{

	private Object endOfFileToken = new Object();
    private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(1);
    private Object nextRecord=null;
    private volatile boolean isClosed=false;
    /**
     * @throws InterruptedException 
     * 
     */
    public void blockingGetNextRecord(){
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
     * @throws InterruptedException
     */
    public void start(){
      new Thread(){
            @Override
            public void run() {
                backgroundThreadRunMethod();
            }
            
        }.start();
        blockingGetNextRecord();
		
    }
    /**
     * This is the method that is called by the {@link Thread#run()}
     * instance in the background thread created and started 
     * in {@link #start()}.  Please set up and start and visiting
     * and parsing that is required by this iterator.  Make sure
     * all appropriate visit methods call the appropriate
     * {@link #blockingPut(Object)} {@link #blockingGetNextRecord()}
     * and {@link #finishedIterating()} methods.
     */
    protected abstract void backgroundThreadRunMethod();
	/**
	 * This method must be called when the visitor has finished
	 * visiting in order to let the iterator know that there
	 * are no more records left to block for.
	 */
    protected void finishedIterating(){
    	blockingPut(endOfFileToken);
    }
    public void blockingPut(Object obj){
        if(!isClosed){
	        try {
	            queue.put(obj);
	        } catch (InterruptedException e) {
	            throw new IllegalStateException(e);
	        }
	    }
    }


	@Override
	public void remove() {
		throw new UnsupportedOperationException();		
	}

	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    public void close() throws IOException {
	        isClosed=true;
	        nextRecord=endOfFileToken;
	        queue.clear();
	        //remove element from queue
	      //  queue.poll();  
	        
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public T next() {
	        if(!hasNext()){
	            throw new NoSuchElementException("no records");
	        }
	        T next = (T)nextRecord;
            blockingGetNextRecord();
	        
	        return next;
	    }

	     @Override
	     public boolean hasNext() {
	         return !isClosed && nextRecord !=endOfFileToken;
	     }
	     
	     /**
	 	 * @return the isClosed
	 	 */
	     public boolean isClosed() {
	 		return isClosed;
	 	}

}
