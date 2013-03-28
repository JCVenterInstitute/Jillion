/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util.iter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


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
     * Creates an {@link StreamingIterator} of Type E
     * that does not have any elements.
     * @param <E> the type of element to be iterated over.
     * @return an instance of {@link StreamingIterator};
     * never null.
     */
    @SuppressWarnings("unchecked")
    public static <E>  StreamingIterator<E> createEmptyStreamingIterator(){
        return IteratorUtil.createStreamingIterator(EmptyIterator.INSTANCE);
    }
    /**
     * Create a new {@link PeekableStreamingIterator} instance
     * which wraps the given iterator.
     * @param iter
     * @return a new {@link PeekableStreamingIterator};
     * never null.
     * @throws NullPointerException if iter is null.
     */
    public static <E> PeekableIterator<E> createPeekableIterator(Iterator<E> iter){
    	return new PeekableIteratorImpl<E>(iter);
    }
    /**
     * Create a new {@link StreamingIterator} instance
     * which wraps the given iterator.
     * @param iter
     * @return a new {@link StreamingIterator};
     * never null.
     * @throws NullPointerException if iter is null.
     */
    public static <E> StreamingIterator<E> createStreamingIterator(Iterator<E> iter){
    	return StreamingIteratorAdapter.adapt(iter);
    }
    /**
     * Create a new {@link PeekableStreamingIterator} instance
     * which wraps the given iterator.
     * @param iter
     * @return a new {@link PeekableStreamingIterator};
     * never null.
     * @throws NullPointerException if iter is null.
     */
    public static <E> PeekableStreamingIterator<E> createPeekableStreamingIterator(Iterator<E> iter){
    	return new PeekableStreamingIteratorImpl<E>(createStreamingIterator(iter));
    }
    public static <E> PeekableStreamingIterator<E> createPeekableStreamingIterator(StreamingIterator<E> iter){
    	return new PeekableStreamingIteratorImpl<E>(iter);
    }
    /**
     * Create a new {@link Iterator} instance
     * that wraps several {@link Iterator}s behind a single
     * {@link Iterator} instance.  Once all the elements in the first
     * iterator have been iterated over, the next iterator in the chain
     * gets used.
     * @return a new {@link Iterator} instance; never null.
     * @throws NullPointerException if the input collection
     * is null or if any element in the collection is null.
     */
    public static <E> Iterator<E> createChainedIterator(Collection<? extends Iterator<E>> iterators){
    	return ChainedIterator.create(iterators);
    }
    /**
     * Create a new {@link StreamingIterator} instance
     * that wraps several {@link StreamingIterator}s behind a single
     * {@link StreamingIterator} instance.  Once all the elements in the first
     * iterator have been iterated over, the next iterator in the chain
     * gets used.  If at any time, {@link StreamingIterator#close()}
     * is called, then all downstream {@link StreamingIterator} are closed as well.
     * @return a new {@link StreamingIterator} instance; never null.
     * @throws NullPointerException if the input collection
     * is null or if any element in the collection is null.
     */
    public static <E> StreamingIterator<E> createChainedStreamingIterator(Collection<? extends StreamingIterator<? extends E>> iterators){
    	return new ChainedStreamingIterator<E>(iterators);
    }
    private static class PeekableIteratorImpl<T> implements PeekableIterator<T>{

    	private final Iterator<T> iter;
    	private T next;
    	private boolean doneIterating=false;
    	
		PeekableIteratorImpl(Iterator<T> iter) {
			if(iter==null){
				throw new NullPointerException();
			}
			this.iter = iter;
			updateNext();
		}
		
		private void updateNext(){
			if(iter.hasNext()){
				next=iter.next();
			}else{
				doneIterating=true;
			}
		}
		@Override
		public boolean hasNext() {
			return !doneIterating;
		}
		@Override
		public T next() {
			T ret = next;
			updateNext();
			return ret;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("peekable iterators can not remove");
			
		}
		@Override
		public T peek() {
			if(hasNext()){
				return next;
			}
			throw new NoSuchElementException();
		}
    	
    	
    }
    
    private static class PeekableStreamingIteratorImpl<T> implements PeekableStreamingIterator<T>{

    	private final StreamingIterator<T> iter;
    	private T next;
    	private boolean doneIterating=false;
    	
    	PeekableStreamingIteratorImpl(StreamingIterator<T> iter) {
    		if(iter==null){
				throw new NullPointerException();
			}
			this.iter = iter;
			updateNext();
		}
		
		private void updateNext(){
			if(iter.hasNext()){
				next=iter.next();
			}else{
				doneIterating=true;
			}
		}
		
		@Override
		public void close() throws IOException {
			doneIterating=true;
			iter.close();
			
		}

		@Override
		public boolean hasNext() {
			return !doneIterating;
		}
		@Override
		public T next() {
			if(hasNext()){
				T ret = next;
				updateNext();
				return ret;
			}
			throw new NoSuchElementException();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("peekable iterators can not remove");
			
		}
		@Override
		public T peek() {
			if(hasNext()){
				return next;
			}
			throw new NoSuchElementException();
		}
    }
    
    /**
     * {@code EmptyIterator} is a NullObject implementation
     * of {@link Iterator}; an EmptyIterator will never 
     * have any elements to iterate over.
     * @author dkatzel
     *
     *
     */
    private static class EmptyIterator<E> implements Iterator<E> {
        /**
         * Singleton instance of Empty iterator that can be shared 
         * by all.
         */
        @SuppressWarnings("rawtypes")
    	static final EmptyIterator INSTANCE  = new EmptyIterator();
        /**
         * Private constructor so no one can subclass.
         */
        private EmptyIterator(){}
        /**
         * Never has a next.
         * @return {@code false}
         */
        @Override
        public boolean hasNext() {
            return false;
        }
        /**
         * Will always throw an NoSuchElementException.
         * @throws NoSuchElementException because there will never be a next.
         */
        @Override
        public E next() {
            throw new NoSuchElementException("no elements in empty iterator");
        }
        /**
         * Does nothing.
         */
        @Override
        public void remove() {
            //no-op
        }

    }
    
    
}
