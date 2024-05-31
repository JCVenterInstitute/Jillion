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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

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
	T peek() throws NoSuchElementException;
	/**
	 * Remove is not supported.
	 * Will always throw {@link UnsupportedOperationException}.
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	default void remove(){
		throw new UnsupportedOperationException();
	}

	/**
	 * If the given condition is true, advance the iterator
	 * by one element.
	 * @param condition the condition to evaluate passing in the peeked value.
	 * @return {@code true} if the iterator was advanced; {@code false} otherwise.
	 * Will return {@code false} if there are no elements left to iterate.
	 *
	 * @throws NullPointerException if condition is null.
	 *
	 * @since 6.0.2
	 */
	default boolean advanceIf(Predicate<T> condition){
		if(!hasNext()){
			return false;
		}
		if(condition.test(peek())){
			next();
			return true;
		}
		return false;
	}

	/**
	 * Keep advancing the iterator, as long as the given condition is true.
	 * @param condition the condition to evaluate passing in the peeked value.
	 * @return {@code true} if the iterator was advanced; {@code false} otherwise.
	 * Will return {@code false} if there are no elements left to iterate.
	 *
	 * @throws NullPointerException if condition is null.
	 *
	 * @since 6.0.2
	 */
	default boolean advanceWhile(Predicate<T> condition){
		Objects.requireNonNull(condition);
		boolean advanced=false;
		while(advanceIf(condition)){
			advanced=true;
		}
		return advanced;
	}
}
