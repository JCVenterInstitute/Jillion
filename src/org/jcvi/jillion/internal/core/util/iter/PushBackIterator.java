/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.internal.core.util.iter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

public class PushBackIterator<T> implements Iterator<T> {

    private final Iterator<T> delegate;
    
    private final LinkedList<T> pushedBackList = new LinkedList<>();
    
    public PushBackIterator(Iterator<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public boolean hasNext() {
        return !pushedBackList.isEmpty() || delegate.hasNext();
    }

    @Override
    public T next() {
        if(!hasNext()){
            throw new NoSuchElementException();
        }
        if(!pushedBackList.isEmpty()){
            return pushedBackList.pop();
        }
        return delegate.next();
    }

    public void pushBack(T t){
        pushedBackList.add(t);
    }
}
