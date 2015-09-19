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
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.internal.core.util.iter.PrimitiveArrayIterators;
import org.junit.Test;
public class TestCharArrayIterator {

	private final char[] expected = new char[]{20,30,40,50};
	
	@Test(expected= NullPointerException.class)
	public void nullArrayShouldThrowNPE(){
		PrimitiveArrayIterators.create((char[]) null);
	}
	
	@Test(expected= NullPointerException.class)
	public void nullArrayWithLengthConstructorShouldThrowNPE(){
		PrimitiveArrayIterators.create((char[]) null, 20);
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void NegativeLengthConstructorShouldThrowException(){
		PrimitiveArrayIterators.create(expected, -1);
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void LengthBeyondArrayConstructorShouldThrowException(){
		PrimitiveArrayIterators.create(expected, expected.length +1);
	}
	@Test
	public void ZeroLengthConstructorShouldReturnEmptyArray(){
		assertFalse(PrimitiveArrayIterators.create(expected, 0).hasNext());
	}
	
	@Test(expected= NullPointerException.class)
	public void nullArrayWithStartAndEndConstructorShouldThrowNPE(){
		PrimitiveArrayIterators.create((char[]) null, 20,40);
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void constructorWithNegativeStartShouldThrowExcepption(){
		PrimitiveArrayIterators.create(expected, -1,40);
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void constructorWithNegativeEndShouldThrowExcepption(){
		PrimitiveArrayIterators.create(expected, 0,-1);
	}
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void constructorWithTooLargeEndShouldThrowExcepption(){
		PrimitiveArrayIterators.create(expected, 0,40);
	}
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void constructorWithTooLargeStartShouldThrowExcepption(){
		PrimitiveArrayIterators.create(expected, 10,3);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithStartBeyondEndStartShouldThrowExcepption(){
		PrimitiveArrayIterators.create(expected, 3,1);
	}
	
	@Test
	public void iterateEntireArrayWithSingleArgConstructor(){
		assertIterateCorrectly(PrimitiveArrayIterators.create(expected), expected);
	}
	
	@Test
	public void iterateEntireArrayWithLengthConstructor(){
		assertIterateCorrectly(PrimitiveArrayIterators.create(expected, expected.length), expected);
	}
	
	@Test
	public void iterateSubArrayWithLengthConstructor(){
		assertIterateCorrectly(PrimitiveArrayIterators.create(expected, expected.length-1), 
				new char[]{20,30,40});
	}
	
	@Test
	public void iterateEntireArrayWithStartAndEndConstructor(){
		assertIterateCorrectly(PrimitiveArrayIterators.create(expected, 0, expected.length-1), 
				expected);
	}
	
	@Test
	public void iterateSubArrayWithStartAndEndConstructor(){
		assertIterateCorrectly(PrimitiveArrayIterators.create(expected, 1, 2), 
				new char[]{30,40});
	}
	
	@Test
	public void iterateSubArrayWithEqualStartAndEndConstructorShouldIterateOverOneElement(){
		assertIterateCorrectly(PrimitiveArrayIterators.create(expected, 1, 1), 
				new char[]{30});
	}
	
	private void assertIterateCorrectly(Iterator<Character> actual, char...expectedBytes){
		for(int i=0; i< expectedBytes.length; i++){
			assertTrue(actual.hasNext());
			assertEquals(expectedBytes[i], actual.next().charValue());
		}
		try{
			actual.remove();
			fail("should throw unsupported operation");
		}catch(UnsupportedOperationException ignore){
			//expected
		}
		assertFalse(actual.hasNext());
		try{
			actual.next();
			fail("shoudl throw NoSuchElementException when run out");
		}catch(NoSuchElementException ignore){
			//expected
		}
	}
}
