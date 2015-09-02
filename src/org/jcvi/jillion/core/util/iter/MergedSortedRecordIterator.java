package org.jcvi.jillion.core.util.iter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
		private final SortedElementComparator<T> comparator;
		private final List<SortedRecordElement<T>> elementList;
		
		
		public MergedSortedRecordIterator(List<? extends Iterator<T>> iterators, Comparator<T> comparator) {
			this.iterators = new ArrayList<>();
			for(Iterator<T> iter : iterators){
			    if(iterators.isEmpty()){
			        //skip empties
			        continue;
			    }
			    this.iterators.add(IteratorUtil.createPeekableStreamingIterator(iter));
			}
			this.comparator = new SortedElementComparator<T>(comparator);
			elementList = new ArrayList<SortedRecordElement<T>>(iterators.size());
			
			next= getNext();
		}
		
		private T getNext(){
			elementList.clear();
			for(PeekableStreamingIterator<T> iter : iterators){
				if(iter.hasNext()){
					//we peek instead of next()
					//incase we don't pick this record yet
					elementList.add(new SortedRecordElement<T>(iter.peek(), iter));
				}
			}
			if(elementList.isEmpty()){
				return null;
			}
			Collections.sort(elementList, comparator);
			SortedRecordElement<T> element= elementList.get(0);
			//advance iterator
			element.source.next();
			return element.record;
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
		
		/**
	         * Struct that has a record and which parent
	         * iterator that record belongs to so we can advance
	         * the iterator if selected.
	         * @author dkatzel
	         *
	         */
	        private static class SortedRecordElement<T>{
	                T record;
	                Iterator<T> source;
	                
	                public SortedRecordElement(T record,
	                                Iterator<T> source) {
	                        this.record = record;
	                        this.source = source;
	                }

	                @Override
	                public String toString() {
	                        return "SortedRecordElement [record=" + record + ", source="
	                                        + source + "]";
	                }
	                
	        }
	        
	        private static class SortedElementComparator<T> implements Comparator<SortedRecordElement<T>>{
	                private final Comparator<T> comparator;
	                

	                public SortedElementComparator(Comparator<T> comparator) {
	                        this.comparator = comparator;
	                }


	                @Override
	                public int compare(SortedRecordElement<T> o1, SortedRecordElement<T> o2) {
	                        return comparator.compare(o1.record, o2.record);
	                }
	        }
	                
	        

}