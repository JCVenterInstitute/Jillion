package org.jcvi.jillion.core;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestRangeComplementParameterCheck {
	
	
	@Test
	public void nullFirstParamShouldThrowException() {
		assertThrows(NullPointerException.class, ()->Ranges.complement(null, new ArrayList<>()));
	}
	@Test
	public void nullSecondParamShouldThrowException() {
		assertThrows(NullPointerException.class, ()->Ranges.complement(new ArrayList<>(), null));
	}
	
	
}
