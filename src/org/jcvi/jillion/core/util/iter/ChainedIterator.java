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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@code ChainedIterator} is a wrapper class
 * to hide several {@link Iterator}s behind a single
 * {@link Iterator} instance.  Once all the elements in the first
 * iterator have been iterated over, the next iterator in the chain
 * gets used.
 * @author dkatzel
 *
 *
 */
final class ChainedIterator<T> implements Iterator<T>{
  
    private final Iterator<? extends Iterator<? extends T>> chain;
    private Iterator<? extends T> currentIterator;
    private final Object endOfIterating= new Object();
   
    private final Object needToGetNext = new Object();
    private Object next=needToGetNext;
    
    /**
     * Create a new ChainedIterator instance.
     * @param <T> the type of objects being iterated over.
     * @param iterators the iterators to iterate over.
     * @return a new ChainedIterator instance; never null.
     * @throws NullPointerException if iterators is null or contains a null.
     */
    public static <T> ChainedIterator<T> create(Collection<? extends Iterator<? extends T>> iterators){
        return new ChainedIterator<T>(iterators);
    }
    
    private ChainedIterator(Collection<? extends Iterator<? extends T>> iterators){
        if(iterators.contains(null)){
            throw new NullPointerException("can not contain null iterator");
        }
        chain = iterators.iterator();
        currentIterator=chain.next();
        
    }

    /**
     * get the next object to return by {@link #next()}.
     */
    private void updateNext() {
        if(currentIterator.hasNext()){
            next=currentIterator.next();
        }else{
            if(chain.hasNext()){
                currentIterator=chain.next();
                updateNext();
            }else{
                next=endOfIterating;
            }
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public boolean hasNext() {
        if(next==endOfIterating){
            return false;
        }
        if(next==needToGetNext){
            updateNext();
            return hasNext();
        }
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public T next() {
        if(!hasNext()){
            throw new NoSuchElementException("no more elements in chain");
        }
        @SuppressWarnings("unchecked")
		T ret= (T)next;
        next= needToGetNext;
        return ret;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
        
    }
}
