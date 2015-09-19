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
package org.jcvi.jillion.sam.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestSamHeaderTagKey {

	SamHeaderTagKey sut = new SamHeaderTagKey('a','b');
	
	@Test
	public void getters(){
		assertEquals('a', sut.getFirstChar());
		assertEquals('b', sut.getSecondChar());
	}
	
	@Test
	public void testToString(){
		assertEquals("ab", sut.toString());
	}
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void sameRefEquals(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	
	@Test
	public void sameValuesAreEqual(){
		SamHeaderTagKey same = new SamHeaderTagKey('a','b');
		
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
	
	@Test
	public void differentFirstCharValuesAreNotEqual(){
		SamHeaderTagKey different = new SamHeaderTagKey('x','b');		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);		
	}
	@Test
	public void differentSecondCharValuesAreNotEqual(){
		SamHeaderTagKey different = new SamHeaderTagKey('a','x');		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);		
	}
	
	@Test
	public void stringCreationMethod(){
		SamHeaderTagKey same = SamHeaderTagKey.getKey('a','b');
		
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
}
