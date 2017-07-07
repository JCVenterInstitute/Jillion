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
package org.jcvi.jillion.core.util;

import java.io.Closeable;
import java.util.Objects;
import java.util.function.Supplier;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * A tuple pair used to return a 2-tuple return value.
 * @author dkatzel
 *
 */
public class Pair<T,U> implements AutoCloseable{

    private final Supplier<T> first;
    private final Supplier<U> second;
    
    public Pair(T first, U second) {
        this.first = () -> first;
        this.second = () ->second;
    }
    
    

    public Pair(Supplier<T> first, Supplier<U> second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }



    public T getFirst() {
        return first.get();
    }

    public U getSecond() {
        return second.get();
    }

    @Override
    public void close() {
        if(first instanceof Closeable){
            IOUtil.closeAndIgnoreErrors((Closeable) first);
        }
        if(second instanceof Closeable){
            IOUtil.closeAndIgnoreErrors((Closeable) second);
        }
        
    }
    
    
}
