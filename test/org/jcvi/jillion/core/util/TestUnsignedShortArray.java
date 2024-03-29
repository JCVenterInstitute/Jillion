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
package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestUnsignedShortArray {

	@Test(expected = NullPointerException.class)
	public void nullConstructorShouldThrowNPE(){
		new UnsignedShortArray(null);
	}
	
	@Test
	public void arrayOfPositiveSignedValues(){
		short[] expected = new short[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE
				
		};
		
		UnsignedShortArray sut = new UnsignedShortArray(expected);
		assertEquals(expected.length, sut.getLength());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
		
	}
	
	@Test
	public void arrayOfMixOfSignedAndUnsignedValues(){
		short[] expected = new short[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
				
		};
		
		UnsignedShortArray sut = new UnsignedShortArray(expected);
		assertEquals(expected.length, sut.getLength());
		for(int i=0; i< expected.length; i++){
			int actual = sut.get(i);
			if(actual >Short.MAX_VALUE){
				assertEquals(expected[i], IOUtil.toSignedShort(actual));
			}else{
				assertEquals(expected[i], actual);
			}
		}
		
	}
	
	@Test
	public void changeValuesInArrayAfterObjCreationShouldSeeUpdatedValues(){
		short[] input = new short[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1			
				
		};
		
		UnsignedShortArray sut = new UnsignedShortArray(input);
		
		sut.put(3, 100);
		short[] expected = Arrays.copyOf(input, input.length);
		expected[3] = 100;
		
		assertEquals(input.length, sut.getLength());
		for(int i=0; i< input.length; i++){
			int actual = sut.get(i);
			if(actual >Short.MAX_VALUE){
				assertEquals(expected[i], IOUtil.toSignedShort(actual));
			}else{
				assertEquals(expected[i], actual);
			}
		}
		
	}
	
	@Test
	public void notEqualToNull(){
		short[] expected = new short[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedShortArray sut1 = new UnsignedShortArray(expected);
		assertFalse(sut1.equals(null));
	}
	
	@Test
	public void notEqualToOtherClass(){
		short[] expected = new short[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedShortArray sut1 = new UnsignedShortArray(expected);
		assertFalse(sut1.equals(null));
	}
	
	@Test
	public void sameValuesShouldBeEquals(){
		short[] expected = new short[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedShortArray sut1 = new UnsignedShortArray(expected);
		UnsignedShortArray sut2 = new UnsignedShortArray(expected);
		TestUtil.assertEqualAndHashcodeSame(sut1, sut2);
	} 
	@Test
	public void diffValuesShouldNotBeEquals(){
		short[] expected = new short[]{
				12, 42, 57,123, Byte.MAX_VALUE, 2, Byte.MAX_VALUE+1,
				Short.MAX_VALUE,
				Short.MIN_VALUE, -1
		};
		
		UnsignedShortArray sut1 = new UnsignedShortArray(expected);
		short[] array2 = new short[expected.length];
		
		System.arraycopy(expected, 0, array2, 0	, array2.length);
		//change some values
		array2[2] = 17;
		UnsignedShortArray sut2 = new UnsignedShortArray(array2);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut1, sut2);
	} 
}
