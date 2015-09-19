/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
