package org.jcvi.jillion.internal.core.util.iter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableStreamingIterator;

/**
 * Combine a list of pre-sorted Iterators into a single sorted iterator.
 * Each call to {@link #next()} will peek at the next elements in the wrapped
 * iterators and return the value that has the lowest sort value as determined
 * by the comparator (and advance that iterator).
 * @author dkatzel
 *
 */
public class MergedSortedRecordIterator<T> implements Iterator<T> {
		private final List<PeekableStreamingIterator<T>> iterators;
		
		private T next;
		private final Comparator<T> comparator;
		
		
		public MergedSortedRecordIterator(List<? extends Iterator<T>> iterators, Comparator<T> comparator) {
			this.iterators = new ArrayList<>();
			for(Iterator<T> iter : iterators){
			    if(iterators.isEmpty()){
			        //skip empties
			        continue;
			    }
			    this.iterators.add(IteratorUtil.createPeekableStreamingIterator(iter));
			}
			this.comparator =comparator;
			
			next= getNext();
		}
		
		private T getNext(){
			T bestElement = null;
			PeekableStreamingIterator<T> bestIter =null;
			
			Iterator<PeekableStreamingIterator<T>> iter = iterators.iterator();
			if(!iter.hasNext()){
				return null;
			}
			do{
				PeekableStreamingIterator<T> currentIter = iter.next();
				if(currentIter.hasNext()){
					bestElement = currentIter.peek();
					bestIter = currentIter;
				}
			}while(iter.hasNext() && bestElement ==null);
			
			while(iter.hasNext()){
				PeekableStreamingIterator<T> currentIter = iter.next();
				if(currentIter.hasNext()){
					T currentElement = currentIter.peek();
					if(comparator.compare(currentElement, bestElement) < 0){
						//found new best
						bestIter = currentIter;
						bestElement = currentElement;
					}
					
				}
			}
			if(bestIter ==null){
				return null;
			}
			//advance best iterator
			return bestIter.next();

			
		}

		@Override
		public boolean hasNext() {
			return next!=null;
		}
		
		
		@Override
		public T next() {
			//don't need to check has next
			//since we can make sure we don't call it incorrectly
			T ret= next;
			next = getNext();
			return ret;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();				
		}
		
		
	                
	        

}