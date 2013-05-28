package org.jcvi.jillion.core;

public class TestLongRange extends AbstractTestRangeSubclasses{

	long length = 4294967295L + 1L;
	long begin = length+ 1L;
	long end = length + length;
	@Override
	protected Range getDifferentRange(){
		Range range = Range.of(begin+5L, end+5L);
		return range;
	}
	@Override
	protected long getBegin(){
		return begin;	
	}
	@Override
	protected long getEnd(){
		return end;	
	}
	@Override
	protected long getLength(){
		return length;
	}

}
