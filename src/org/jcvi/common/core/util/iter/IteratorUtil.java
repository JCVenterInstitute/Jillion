package org.jcvi.common.core.util.iter;

import java.util.Iterator;

public final class IteratorUtil {

	private IteratorUtil(){
		//can not instantiate
	}
	
	/**
     * Creates an {@link Iterator} of Type E
     * that does not have any elements.
     * @param <E> the type of element to be iterated over.
     * @return an instance of {@link Iterator};
     * never null.
     */
    @SuppressWarnings("unchecked")
    public static <E>  Iterator<E> createEmptyIterator(){
        return EmptyIterator.INSTANCE;
    }
    
    /**
     * Creates an {@link CloseableIterator} of Type E
     * that does not have any elements.
     * @param <E> the type of element to be iterated over.
     * @return an instance of {@link CloseableIterator};
     * never null.
     */
    @SuppressWarnings("unchecked")
    public static <E>  CloseableIterator<E> createEmptyCloseableIterator(){
        return CloseableIteratorAdapter.adapt(EmptyIterator.INSTANCE);
    }
}
