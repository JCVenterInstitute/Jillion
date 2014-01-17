package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {

	private final T[] array;
	private int count;
	
	public ArrayIterator(T[] array) {
		if(array==null){
			throw new NullPointerException("array can not be null");
		}
		this.array = array;
	}

	@Override
	public boolean hasNext() {
		return count<array.length;
	}

	@Override
	public T next() {
		if(!hasNext()){
			throw new NoSuchElementException();
		}
		return array[count++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");
		
	}

}
