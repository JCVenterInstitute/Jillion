package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface PeekableIterator<T> extends Iterator<T> {
	/**
	 * Peek at the next element to be iterated
	 * over without actually iterating over it.
	 * The object returned is guaranteed to be the 
	 * same as the object returned by the next 
	 * call to {@link #next()}.
	 * Calling {@link #peek()} several times
	 * without calling {@link #next()}
	 * will always return the same object.
	 * @return T
	 * @throws NoSuchElementException if there are no more elements
	 */
	T peek();
	/**
	 * Remove is not supported.
	 * Will always throw {@link UnsupportedOperationException}.
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	void remove();
}
