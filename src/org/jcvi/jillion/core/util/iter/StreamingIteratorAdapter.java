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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@code StreamingIteratorAdapter} is an adapter
 * that will adapt an {@link Iterator} instance
 * into a {@link StreamingIterator}.
 * @author dkatzel
 *
 *
 */
final class StreamingIteratorAdapter<T> implements StreamingIterator<T>{

	private final Iterator<T> iterator;
	private volatile boolean isClosed = false;
    /**
     * Adapt the given (non-null) {@link Iterator} instance
     * into a {@link StreamingIterator}.
     * @param <T> the type of elements the iterator iterates over.
     * @param iterator the iterator to adapt into a CloseableIterator.
     * @return a new {@link StreamingIterator}
     * @throws NullPointerException if iterator is {@code null}.
     */
    public static <T> StreamingIteratorAdapter<T> adapt(Iterator<T> iterator){
        return new StreamingIteratorAdapter<T>(iterator);
    }
   
    /**
     * @param iterator
     */
    private StreamingIteratorAdapter(Iterator<T> iterator) {
    	if(iterator ==null){
    		throw new NullPointerException("iterator can not be null");
    	}
        this.iterator = iterator;
    }

    /**
    * Close this iterator.  If the adapted
    * iterator happens to be a {@link StreamingIterator}
    * before adaption, then its {@link StreamingIterator#close()}
    * method will be called too.  This will
    * force this iterator's {@link StreamingIterator#hasNext()}
    * to return {@code false}
    * and {@link StreamingIterator#next()} to throw
    * a {@link NoSuchElementException}
    * as if there were no more elements
    * to iterate over.
    */
    @Override
    public void close() {
        if(isClosed){
        	return ; //no-op
        }
        if(iterator instanceof StreamingIterator){
            ((StreamingIterator<T>)iterator).close();
        }
        isClosed=true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean hasNext() {
    	if(isClosed){
    		return false;
    	}
        return iterator.hasNext();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public T next() {
    	if(isClosed){
    		throw new NoSuchElementException("iterator has been closed");
    	}
        return iterator.next();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void remove() {
    	throw new UnsupportedOperationException();	
        
    }
    
    
    
}
