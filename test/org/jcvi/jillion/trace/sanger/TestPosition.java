package org.jcvi.jillion.trace.sanger;

import org.jcvi.jillion.trace.sanger.Position;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPosition {

	@Test
	public void createValidPosition(){
		Position sut = Position.valueOf(1234);
		assertEquals(1234, sut.getValue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creatingNegativeValueShouldThrowException(){
		Position.valueOf(-1);
	}
	
	@Test
	public void flyweightReusesSameValues(){
		Position a = Position.valueOf(123);
		Position b = Position.valueOf(123);
		assertSame(a,b);
	}
	
	@Test
	public void valueLargerThanShortMax(){
		Position sut = Position.valueOf(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, sut.getValue());
	}
}
