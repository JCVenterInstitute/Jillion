package org.jcvi.jillion.core;

public class TestByteRange extends AbstractTestRangeSubclasses{

	@Override
	protected Range getDifferentRange(){
		return Range.of(3,4);
	}
	@Override
	protected long getBegin(){
		return 1;	
	}
	@Override
	protected long getEnd(){
		return 2;	
	}
	@Override
	protected long getLength(){
		return 2;
	}
}
