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
package org.jcvi.jillion.core.qual;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.junit.Before;
import org.junit.Test;
public abstract class AbstractTestQualitySequence {

	
	private final byte[] qualities = new byte[]{20,20,20,20,30,30,40,50,60};
	
	private QualitySequence sut;
	
	protected abstract QualitySequence create(byte[] qualities);
	
	@Before
	public void createSut(){
		sut = create(qualities);
	}
	
	@Test
	public void getLength(){	
		assertEquals(qualities.length, sut.getLength());
	}
	
	@Test
	public void get(){
		for(int i=0; i<qualities.length; i++){
			assertEquals(qualities[i], sut.get(i).getQualityScore());
		}
	}
	
	@Test
	public void iterator(){
		Iterator<PhredQuality> iter = sut.iterator();
		assertTrue(iter.hasNext());
		int i=0;
		while(iter.hasNext()){
			assertEquals(qualities[i], iter.next().getQualityScore());
			i++;
		}
		assertEquals(qualities.length,i);
	}
	@Test
	public void rangedIterator(){
		Iterator<PhredQuality> iter = sut.iterator(Range.of(3,7));
		assertTrue(iter.hasNext());
		for(int i=3; i<8; i++){
			assertEquals(qualities[i], iter.next().getQualityScore());
		}
		assertFalse(iter.hasNext());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void removingFromRangeIterThrowsException(){
		Iterator<PhredQuality> iter = sut.iterator(Range.of(3,7));
		assertTrue(iter.hasNext());
		iter.next();
		iter.remove();
	}
	@Test(expected=UnsupportedOperationException.class)
	public void removingFromIterThrowsException(){
		Iterator<PhredQuality> iter = sut.iterator();
		assertTrue(iter.hasNext());
		iter.next();
		iter.remove();
	}
	@Test
	public void testToString(){
		StringBuilder expected = new StringBuilder(4*qualities.length);
		for(int i=0; i< qualities.length-1; i++){
			expected.append(qualities[i])
					.append(", ");
		}
		expected.append(qualities[qualities.length-1]);
	}
	
	@Test
	public void toArray(){
		assertArrayEquals(qualities, sut.toArray());
	}
	
	@Test
	public void minQuality(){
		assertEquals(20, sut.getMinQuality().get().getQualityScore());
	}
	@Test
	public void maxQuality(){
		assertEquals(60, sut.getMaxQuality().get().getQualityScore());
	}
	@Test
	public void avgQuality(){
		assertEquals(32.222222D, sut.getAvgQuality().getAsDouble(), 0.0001D);
	}
	
	@Test
	public void minQualityOnEmptySequenceShouldReturnEmptyOptional(){
		assertFalse(createEmtpySequence().getMinQuality().isPresent());
	}

	private QualitySequence createEmtpySequence() {
		return create(new byte[0]);
	}
	@Test
	public void maxQualityOnEmptySequenceShouldReturnEmpty(){
		assertFalse(createEmtpySequence().getMaxQuality().isPresent());
	}
	
	@Test
	public void avgQualityOnEmptySequenceShouldReturnEmpty(){
		assertFalse(createEmtpySequence().getAvgQuality().isPresent());
	}
}
