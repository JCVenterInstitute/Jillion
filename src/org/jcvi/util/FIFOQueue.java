/*
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;
import java.util.Deque;
import java.util.Queue;


/**
 * {@code FIFOQueue} is a First In First Out {@link Queue} that
 * adds elements to the tail and removes elements from
 * the head.
 * @author dkatzel
 *
 *
 */
public class FIFOQueue<E> extends AbstractFOQueue<E>{

    /**
     * Construct an empty FIFOQueue.
     */
    public FIFOQueue() {
        super();
    }

    @Override
    protected boolean add(E e, Deque<E> deque) {
        return deque.add(e);
    }
    @Override
    protected boolean offer(E e, Deque<E> deque) {
        return deque.offer(e);
    }
    
    
}
