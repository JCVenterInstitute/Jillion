package org.jcvi.jillion.trace.sanger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;

public class DefaultPositionSequence implements PositionSequence{

	private final short[] data;
	
	DefaultPositionSequence(short[] data) {
		this.data = data;
	}

	@Override
	public short[] toArray() {
		//defensive copy
		return Arrays.copyOf(data, data.length);
	}

	@Override
	public Position get(long index) {
		return Position.valueOf(IOUtil.toUnsignedShort(data[(int)index]));		
	}

	@Override
	public long getLength() {
		return data.length;
	}

	@Override
	public Iterator<Position> iterator(Range range) {
		return new IteratorImpl(range);
	}

	@Override
	public Iterator<Position> iterator() {
		return new IteratorImpl();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DefaultPositionSequence)) {
			return false;
		}
		DefaultPositionSequence other = (DefaultPositionSequence) obj;
		if (!Arrays.equals(data, other.data)) {
			return false;
		}
		return true;
	}

	private class IteratorImpl implements Iterator<Position>{
		private final int end;
		private int offset;
		
		IteratorImpl(){
			end = data.length;
			offset=0;
		}
		IteratorImpl(Range range){
			
			offset = (int)range.getBegin();
			end = (int)range.getEnd();
			if(offset<0){
				throw new IllegalArgumentException(String.format("range %s can not have negative values",range));
			}
			if(end>data.length){
				throw new IllegalArgumentException(
						String.format("range %s can not extend past sequence length %d",range,data.length));
			}
		}
		@Override
		public boolean hasNext() {
			return offset<end;
		}
		@Override
		public Position next() {
			if(!hasNext()){
				throw new NoSuchElementException(String.format("offset = %d",offset));
			}
			Position next = get(offset);
			offset++;
			return next;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
		
	}
}
