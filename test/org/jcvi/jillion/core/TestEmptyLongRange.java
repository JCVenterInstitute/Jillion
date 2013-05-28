package org.jcvi.jillion.core;

public class TestEmptyLongRange extends AbstractTestRangeSubclasses{

	private long begin = Integer.MAX_VALUE+1L;
	
	@Override
	protected Range getDifferentRange(){
		return Range.of(begin+1,begin);
	}
	@Override
	protected long getBegin(){
		return begin;	
	}
	@Override
	protected long getEnd(){
		return begin -1;	
	}
	@Override
	protected long getLength(){
		return 0;
	}

}
