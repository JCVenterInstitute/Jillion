package org.jcvi.jillion.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public abstract class AbstractTestRangeSubclasses {

	
	private Range getRange(){
		return Range.of(getBegin(),getEnd());
	}
	
	protected abstract Range getDifferentRange();
	
	protected abstract long getBegin();
	protected abstract long getEnd();
	
	protected abstract long getLength();
	
	@Test
	public void getters(){
		Range range = getRange();
		assertEquals(getBegin(), range.getBegin());
		assertEquals(getEnd(), range.getEnd());
		assertEquals(getLength(), range.getLength());
	}

	@Test
	public void equalsSameRef(){
		TestUtil.assertEqualAndHashcodeSame(getRange(), getRange());
	}
	@Test
	public void notEqualToNoRange(){
		
		assertFalse(getRange().equals("not a range"));
	}
	@Test
	public void notEqualsDifferentValues(){
		TestUtil.assertNotEqualAndHashcodeDifferent(getRange(), getDifferentRange());
	}
}
