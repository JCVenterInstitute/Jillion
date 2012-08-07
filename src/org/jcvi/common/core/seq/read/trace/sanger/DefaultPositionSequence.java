package org.jcvi.common.core.seq.read.trace.sanger;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;

public class DefaultPositionSequence implements PositionSequence{

	private final short[] data;
	
	DefaultPositionSequence(short[] data) {
		this.data = data;
	}

	@Override
	public Position get(int index) {
		return Position.valueOf(IOUtil.toUnsignedShort(data[index]));
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
				throw new NoSuchElementException(String.format("offset = %d"));
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
