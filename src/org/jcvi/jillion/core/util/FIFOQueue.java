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
/*
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;
import java.util.Deque;
import java.util.Queue;

import org.jcvi.jillion.internal.core.util.AbstractFOQueue;


/**
 * {@code FIFOQueue} is a First In First Out {@link Queue} that
 * adds elements to the tail and removes elements from
 * the head.
 * @author dkatzel
 *
 *
 */
public final class FIFOQueue<E> extends AbstractFOQueue<E>{

    @Override
    protected boolean add(E e, Deque<E> deque) {
        return deque.add(e);
    }
    @Override
    protected boolean offer(E e, Deque<E> deque) {
        return deque.offer(e);
    }
    
    
}
