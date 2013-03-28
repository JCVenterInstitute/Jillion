/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
