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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * {@code ChainedStreamingIterator}
 * is a {@link StreamingIterator} that chains
 * multiple {@link StreamingIterator}s
 * together. Once all the elements in the first
 * iterator have been iterated over, the next iterator in the chain
 * gets used.
 * 
 * @author dkatzel
 *
 *
 */
final class ChainedStreamingIterator<T> implements StreamingIterator<T>{

    private final List<StreamingIterator<? extends T>> delegates;
    private final Iterator<? extends T> iterator;
    
    
    public ChainedStreamingIterator(Collection<? extends StreamingIterator<? extends T>> delegates) {
    	if(delegates.contains(null)){
            throw new NullPointerException("can not contain null iterator");
        }
        this.delegates = new ArrayList<StreamingIterator<? extends T>>(delegates);
        
        this.iterator = ChainedIterator.create(delegates);
    }

    /**
    * Close all the iterators
    * being chained together.
    */
    @Override
    public void close() {
      for(StreamingIterator<? extends T> delegate : delegates){
            IOUtil.closeAndIgnoreErrors(delegate);
        }
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
