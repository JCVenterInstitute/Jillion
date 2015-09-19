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
package org.jcvi.jillion.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestSingleElementIterator {

	private String foo = "foo";
	private Iterator<String> iter;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setuo(){
		 iter = new SingleElementIterator<>(foo);
	}
	@Test
	public void iterate(){
		
		assertTrue(iter.hasNext());
		assertEquals(foo, iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void shouldThrowNoSuchElementExceptionIfAlreadyIterated(){
		iter.next();
		
		assertFalse(iter.hasNext());
		expectedException.expect(NoSuchElementException.class);
		
		iter.next();
	}
}
