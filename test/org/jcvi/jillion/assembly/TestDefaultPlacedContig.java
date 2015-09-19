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
package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestDefaultPlacedContig {

	private String id = "id";
	private Range range = Range.of(1,10);

	DefaultPlacedContig sut = new DefaultPlacedContig(id, range, Direction.REVERSE);
	@Test
	public void fullConstructor(){
		assertEquals(id, sut.getContigId());
		assertEquals(range, sut.asRange());
		assertEquals(Direction.REVERSE, sut.getDirection());
		assertEquals(range.getBegin(), sut.getBegin());
		assertEquals(range.getEnd(), sut.getEnd());
		assertEquals(range.getLength(), sut.getLength());
	}
	
	@Test
	public void constructorDefaultsToForwardDir(){
		DefaultPlacedContig sut = new DefaultPlacedContig(id, range);
		assertEquals(id, sut.getContigId());
		assertEquals(range, sut.asRange());
		assertEquals(Direction.FORWARD, sut.getDirection());
		assertEquals(range.getBegin(), sut.getBegin());
		assertEquals(range.getEnd(), sut.getEnd());
		assertEquals(range.getLength(), sut.getLength());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNPE(){
		new DefaultPlacedContig(null, range);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullRangeShouldThrowNPE(){
		new DefaultPlacedContig(id, null);
	}
	@Test(expected = NullPointerException.class)
	public void nullDirShouldThrowNPE(){
		new DefaultPlacedContig(id, range,null);
	}
	
	@Test
	public void notEqualtoNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void notEqualtoDifferentClass(){
		assertFalse(sut.equals("not a placedContig"));
	}
	
	@Test
	public void equalToSameRef(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void equalToSameValues(){
		DefaultPlacedContig sameValues = new DefaultPlacedContig(id, range, Direction.REVERSE);
		TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
	}
	@Test
	public void notEqualToDifferentId(){
		DefaultPlacedContig differentValues = new DefaultPlacedContig("not"+id, range, Direction.REVERSE);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	
	@Test
	public void notEqualToDifferentDir(){
		DefaultPlacedContig differentValues = new DefaultPlacedContig(id, range, Direction.FORWARD);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	@Test
	public void notEqualToDifferentRange(){
		DefaultPlacedContig differentValues = new DefaultPlacedContig(id, new Range.Builder(range).shift(1).build(), Direction.REVERSE);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
	}
	
}
