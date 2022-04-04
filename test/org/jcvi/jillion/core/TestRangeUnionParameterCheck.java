package org.jcvi.jillion.core;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestRangeUnionParameterCheck {
	
	
	@Test
	public void nullFirstParamShouldThrowException() {
		assertThrows(NullPointerException.class, ()->Ranges.union(null, new ArrayList<>()));
	}
	@Test
	public void nullSecondParamShouldThrowException() {
		assertThrows(NullPointerException.class, ()->Ranges.union(new ArrayList<>(), null));
	}
	
	
}
