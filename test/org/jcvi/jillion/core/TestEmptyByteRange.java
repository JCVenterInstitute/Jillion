package org.jcvi.jillion.core;

public class TestEmptyByteRange extends AbstractTestRangeSubclasses{

	@Override
	protected Range getDifferentRange(){
		return Range.of(3,2);
	}
	@Override
	protected long getBegin(){
		return 1;	
	}
	@Override
	protected long getEnd(){
		return 0;	
	}
	@Override
	protected long getLength(){
		return 0;
	}

}
