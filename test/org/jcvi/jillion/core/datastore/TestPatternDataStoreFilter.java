package org.jcvi.jillion.core.datastore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;
public class TestPatternDataStoreFilter {

	@Test
	public void matchesCompletely(){
		DataStoreFilter filter = DataStoreFilters.newMatchFilter(Pattern.compile("^\\d+$"));
		assertTrue(filter.accept("1234"));
		assertFalse(filter.accept("not a number"));
		assertFalse(filter.accept("1234 submatch"));
	}
	@Test
	public void patternIsOnlyPrefix(){
		DataStoreFilter filter = DataStoreFilters.newMatchFilter(Pattern.compile("^[a-z]+"));
		assertTrue(filter.accept("lowercase"));
		assertFalse(filter.accept("UPPERCASE"));
		assertFalse(filter.accept("white space"));
		assertFalse(filter.accept("prefixUPPERCASE"));
	}
	
	@Test
	public void patternMatchesOnlyInMiddle(){
		DataStoreFilter filter = DataStoreFilters.newMatchFilter(Pattern.compile("[a-z]+"));
		assertTrue(filter.accept("lowercase"));
		assertFalse(filter.accept("UPPERCASE"));
		assertFalse(filter.accept("white space"));
		assertFalse(filter.accept("prefixUPPERCASE"));
		assertFalse(filter.accept("PREFIXlowercase"));
	}
	
	@Test(expected = NullPointerException.class)
	public void nullPatternShouldThrowNPE(){
		DataStoreFilters.newMatchFilter(null);
	}
}
