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
package org.jcvi.jillion.internal.core.util;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.PriorityQueue;
/**
 * {@code BoundedPriorityQueue} is a {@link PriorityQueue}
 * that is bounded by a maximum size.  If the queue size goes beyond
 * this max size, then the least element (determined by the provided comparator)
 * will be removed.
 * 
 * This class is NOT THREADSAFE
 * 
 * @author dkatzel
 *
 * @param <E> the type of element in the queue.
 */
public class BoundedPriorityQueue<E> extends AbstractQueue<E>{

	private final int maxSize;
	private final Comparator<? super E> comparator;
	
	private final PriorityQueue<E> delegate;
	/**
	 * Create a new BoundedPriorityQueue with the given maxSize and uses
	 * the natural order of the Elements as a comparator.
	 * 
	 * @param maxSize the max size this Bounded Priority Queue can grow to
	 * before we start kicking out elements, must be >= 1.
	 * 
	 * 
	 * 
	 * @throws IllegalArgumentException if maxSize < 1.
	 */
	public static <E extends Comparable<? super E>> BoundedPriorityQueue<E> create(int maxSize){
		return new BoundedPriorityQueue<E>(maxSize, Comparator.naturalOrder());
	}

	/**
	 * Create a new BoundedPriorityQueue with the given maxSize and comparator.
	 * 
	 * @param maxSize the max size this Bounded Priority Queue can grow to
	 * before we start kicking out elements, must be >= 1.
	 * 
	 * @param comparator the {@link Comparator} used for comparisons.
	 * 		The comparator should sort the elements worst (smallest) to best (largest)
	 * 
	 * @throws NullPointerException if comparator is null.
	 * @throws IllegalArgumentException if maxSize < 1.
	 */
	public static <E> BoundedPriorityQueue<E> create(int maxSize, Comparator<? super E> comparator){
		return new BoundedPriorityQueue<E>(maxSize, comparator);
	}
	
	
	private BoundedPriorityQueue(int maxSize, Comparator<? super E> comparator){
		if(maxSize <1){
			throw new IllegalArgumentException("max size must be >= 1");
		}
		Objects.requireNonNull(comparator);
		//priorityQueue can only see the top
		//so peeking
		//at the top will get us the "smallest" element in the queue
		//which we can use to determine if we can bump it in #offer(e)
		this.comparator = comparator;
		delegate = new PriorityQueue<E>(this.comparator);
		
		this.maxSize = maxSize;
	}
	
	@Override
	public boolean offer(E e) {
		if(size() >= maxSize){
			E peek = peek();
			
			if(comparator.compare(e, peek) >0){
				//new element offered is greater than
				//to our current worst element
				//so we can bump the current worst
				//and add the new one
				poll();
			}else{
				//don't add the new element
				//but return true so we don't get 
				//a Queue Full exception
				return true;
			}
		}
		return delegate.offer(e);
	}

	@Override
	public E poll() {
		return delegate.poll();
	}

	@Override
	public E peek() {
		return delegate.peek();
	}

	@Override
	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	
}
