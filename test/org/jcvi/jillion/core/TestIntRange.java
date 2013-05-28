package org.jcvi.jillion.core;

public class TestIntRange extends AbstractTestRangeSubclasses{

	int begin = Short.MAX_VALUE +1;
	int end = Short.MAX_VALUE+5;
	@Override
	protected Range getDifferentRange(){
		return Range.of(begin+5, end+5);
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
