package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestRangeUnionParameterCheck {
	
	
	@Test
	public void nullListFirstParamShouldThrowException() {
		assertThrows(NullPointerException.class, ()->Ranges.union((List<Range>)null, new ArrayList<>()));
	}
	@Test
	public void nullRangeFirstParamShouldThrowException() {
		assertThrows(NullPointerException.class, ()->Ranges.union((Range)null, new ArrayList<>()));
	}
	@Test
	public void nullSecondParamShouldThrowException() {
		assertThrows(NullPointerException.class, ()->Ranges.union(new ArrayList<>(), null));
	}
	
	
}
