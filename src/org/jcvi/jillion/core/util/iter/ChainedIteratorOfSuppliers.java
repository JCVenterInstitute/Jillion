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

import java.io.Closeable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * {@code ChainedIteratorOfSuppliers} is a wrapper class
 * to hide several {@link Iterator}s behind a single
 * {@link Iterator} instance, that are created ONLY WHEN NEEDED.  Once all the elements in the first
 * iterator have been iterated over, the next iterator in the chain
 * gets created by calling its supplier.
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
final class ChainedIteratorOfSuppliers<T> implements Iterator<T>, Closeable{
  
    private final Iterator<? extends Supplier<? extends Iterator<? extends T>>> chain;
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
    public static <T> ChainedIteratorOfSuppliers<T> create(Collection<? extends Supplier<? extends Iterator<? extends T>>> suppliers){
        return new ChainedIteratorOfSuppliers<T>(suppliers);
    }
    
    @Override
    public void close(){
        next = endOfIterating;
        if(currentIterator instanceof Closeable){
            IOUtil.closeAndIgnoreErrors((Closeable)currentIterator);
        }
        
    }

    private ChainedIteratorOfSuppliers(Collection<? extends Supplier<? extends Iterator<? extends T>>> suppliers){
        if(suppliers.contains(null)){
            throw new NullPointerException("can not contain null iterator");
        }
        chain = suppliers.iterator();
        updateCurrentIterator();
        
    }

    private void updateCurrentIterator(){
        Iterator<? extends T> newIter= null;
        while(chain.hasNext()){
           
            newIter = chain.next().get();
            if(newIter !=null){
               break;
            }
        }
        if(newIter ==null){
            next = endOfIterating;
        }else{
            next=needToGetNext;
            currentIterator = newIter; 
        }
    }
    
    /**
     * get the next object to return by {@link #next()}.
     */
    private void updateNext() {
        if(currentIterator.hasNext()){
            next=currentIterator.next();
        }else{
            if(chain.hasNext()){
                updateCurrentIterator();
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
