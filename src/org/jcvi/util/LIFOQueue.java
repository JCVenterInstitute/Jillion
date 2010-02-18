/*
 * Created on Aug 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.Deque;
import java.util.Queue;

/**
 * {@code LIFOQueue} is a Last In First Out {@link Queue} that
 * adds and removes elements from the head.
 * @author dkatzel
 *
 *
 */
public class LIFOQueue<E> extends AbstractFOQueue<E>{

    /**
     * Constructs an empty LIFOQueue.
     */
    public LIFOQueue() {
        super();
    }

  

    @Override
    protected boolean add(E e, Deque<E> deque) {
        deque.addFirst(e);
        return true;
    }

    @Override
    protected boolean offer(E e, Deque<E> deque) {
        return deque.offerFirst(e);
    }

}
