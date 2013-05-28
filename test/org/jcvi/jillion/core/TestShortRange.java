package org.jcvi.jillion.core;

public class TestShortRange extends AbstractTestRangeSubclasses{

	int begin = Byte.MIN_VALUE -5;
	int end = Byte.MIN_VALUE-1;
	@Override
	protected Range getDifferentRange(){
		return Range.of(begin-5, end-5);
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
		return 5;
	}

}
