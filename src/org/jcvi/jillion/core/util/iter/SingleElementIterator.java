package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * Utility class that wraps a single element inside an iterator.
 * NOTE : This class is not Threadsafe.
 * 
 * @author dkatzel
 *
 * @param <T> the Type of the element
 * 
 */
public final class SingleElementIterator<T> implements Iterator<T>{

	private final T element;
	private boolean hasNext = true;
	
	public SingleElementIterator(T element) {
		this.element = element;
	}
	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public T next() {
		if(hasNext){
			hasNext = false;
			return element;
		}
		throw new NoSuchElementException();
	}

}
