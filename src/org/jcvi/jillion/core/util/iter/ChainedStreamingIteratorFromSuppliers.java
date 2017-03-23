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
import java.util.function.Supplier;


/**
 * {@code ChainedStreamingIteratorFromSuppliers} is a wrapper class
 * to hide several {@link StreamingIterator}s behind a single
 * {@link StreamingIterator} instance, that are created ONLY WHEN NEEDED.  Once all the elements in the first
 * iterator have been iterated over, the next iterator in the chain
 * gets created by calling its supplier.
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
final class ChainedStreamingIteratorFromSuppliers<T> implements StreamingIterator<T>{

    private final ChainedIteratorOfSuppliers<? extends T> iterator;
    
    public static <T> ChainedStreamingIteratorFromSuppliers<T> create(Collection<? extends Supplier<? extends StreamingIterator<? extends T>>> suppliers){
        return new ChainedStreamingIteratorFromSuppliers<T>(suppliers);
    }
    public ChainedStreamingIteratorFromSuppliers(Collection<? extends Supplier<? extends StreamingIterator<? extends T>>> suppliers) {
    	if(suppliers.contains(null)){
            throw new NullPointerException("can not contain null suppiler");
        }
        
        this.iterator = ChainedIteratorOfSuppliers.create(suppliers);
    }

    /**
    * Close all the iterators
    * being chained together.
    */
    @Override
    public void close() {
        iterator.close();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public T next() {
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
