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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.qual;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestQualitySequenceBuilder {

	private byte[] toByteArray(QualitySequence s){
		byte[] array = new byte[(int)s.getLength()];
		Iterator<PhredQuality> iter = s.iterator();
		int i=0;
		while(iter.hasNext()){
			array[i]=iter.next().getQualityScore();
			i++;
		}
		return array;
	}
	@Test
	public void initialArray(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(new byte[]{10,12,13,13,13,13,13,13,8});
		assertEquals(9, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void replace(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.replace(5, PhredQuality.valueOf(99));
		assertEquals(9, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,99,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void deleteMiddle(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.delete(Range.of(3,6));
		assertEquals(5, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void deleteBeginning(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.delete(Range.of(0,2));
		assertEquals(6, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{13,13,13,13,13,8},
				toByteArray(seq));
	}
	
	@Test
	public void deleteEnd(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.delete(Range.of(6,8));
		assertEquals(6, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13},
				toByteArray(seq));
	}
	@Test
	public void trim(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.trim(Range.of(3,6));
		assertEquals(4, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{13,13,13,13},
				toByteArray(seq));
	}
	
	@Test
	public void trimEmptyRangeShouldRemoveEntireSequence(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.trim(new Range.Builder(0).build());
		assertEquals(0, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[0],
				toByteArray(seq));
	}
	@Test
	public void appendArray(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		assertEquals(9, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	
	@Test
	public void initialSizeTooSmallShouldGrowAsNeeded(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(5);
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		assertEquals(9, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	
	@Test
	public void appendIntValue(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.append(25);
		assertEquals(10, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8,25},
				toByteArray(seq));
	}
	
	@Test
	public void appendByteValue(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.append((byte)25);
		assertEquals(10, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8,25},
				toByteArray(seq));
	}
	
	@Test
	public void appendAnotherSequence(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		
		QualitySequenceBuilder other = new QualitySequenceBuilder(new byte[]{1,2,3,4});
		sut.append(other.build());
		assertEquals(13, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8,1,2,3,4},
				toByteArray(seq));
	}
	
	@Test
	public void appendAnotherSequenceBuilder(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		
		QualitySequenceBuilder other = new QualitySequenceBuilder(new byte[]{1,2,3,4});
		sut.append(other);
		assertEquals(13, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8,1,2,3,4},
				toByteArray(seq));
	}
	@Test
	public void prepend(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.prepend(25);
		assertEquals(10, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{25,10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void prependArray(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.prepend(new byte[]{25,25,25,25,26});
		assertEquals(14, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{25,25,25,25,26,10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	
	@Test
	public void prependAnotherSequence(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.prepend(new QualitySequenceBuilder(new byte[]{25,25,25,25,26}).build());
		assertEquals(14, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{25,25,25,25,26,10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void prependAnotherSequenceBuilder(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.prepend(new QualitySequenceBuilder(new byte[]{25,25,25,25,26}));
		assertEquals(14, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{25,25,25,25,26,10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void reverse(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(
				new byte[]{10,12,13,13,13,13,13,13,8});
		sut.reverse();
		assertEquals(9, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{8,13,13,13,13,13,13,12,10},
				toByteArray(seq));
	}
	
	@Test
	public void useRunLengthEncoding(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		byte[] array = new byte[500];
		Arrays.fill(array, (byte)20);
		sut.append(array);
		assertEquals(500, sut.getLength());
		QualitySequence seq =sut.build();
		assertTrue(seq instanceof RunLengthEncodedQualitySequence);
	}
	
	@Test
	public void copy(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		QualitySequenceBuilder copy = sut.copy();
		assertEquals(9, copy.getLength());
		QualitySequence seq =copy.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void changesToCopyDoNotAffectOther(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		QualitySequenceBuilder copy = sut.copy();
		copy.append(99);
		
		sut.delete(Range.of(2,5));
		
		assertEquals(10, copy.getLength());
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8,99},
				toByteArray(copy.build()));
		
		assertEquals(5, sut.getLength());
		assertArrayEquals(
				new byte[]{10,12,13,13,8},
				toByteArray(sut.build()));
	}
	
	@Test
	public void mixOfOperations(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(30);
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8})
		.insert(5, new byte[]{99,99,99,99,5}) //10,12,13,13,13,99,99,99,99,5,13,13,13,8
		.reverse() // 8,13,13,13,5,99,99,99,99,13,13,13,12,10
		.trim(Range.of(2,12)) // 13,13,5,99,99,99,99,13,13,13,12		
		.prepend(45)// 45,13,13,5,99,99,99,99,13,13,13,12
		.append(37) // 45,13,13,5,99,99,99,99,13,13,13,12,37
		.replace(10, PhredQuality.valueOf(5)); // 45,13,13,5,99,99,99,99,13,13,5,12,37
		
		assertEquals(13, sut.getLength());
		assertArrayEquals(
				new byte[]{45,13,13,5,99,99,99,99,13,13,5,12,37},
				toByteArray(sut.build()));
	}
	
	@Test
	public void buildEmptySequence(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder(30);
		QualitySequence seq =sut.build();
		assertEquals(0,seq.getLength());
	}
	
	@Test
	public void insert(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.insert(4, PhredQuality.valueOf(25));
		assertEquals(10, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,25,13,13,13,13,8},
				toByteArray(seq));
	}
	@Test
	public void negativeInsertOffsetShouldThrowException(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		try{
			sut.insert(-1, PhredQuality.valueOf(25));
			fail("should throw exception if offset -1");
		}catch(IndexOutOfBoundsException expected){
			assertEquals("invalid offset -1 only values between 0 and 9 are allowed", expected.getMessage());
		}
	}
	@Test
	public void insertOffsetGreatherThanLengthShouldThrowException(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		try{
			sut.insert(10, PhredQuality.valueOf(25));
			fail("should throw exception if offset -1");
		}catch(IndexOutOfBoundsException expected){
			assertEquals("invalid offset 10 only values between 0 and 9 are allowed", expected.getMessage());
		}
	}
	@Test
	public void insertOffsetOfLengthShouldActLikeAppend(){
		QualitySequenceBuilder sut = new QualitySequenceBuilder();
		sut.append(new byte[]{10,12,13,13,13,13,13,13,8});
		sut.insert(9, PhredQuality.valueOf(25));
		assertEquals(10, sut.getLength());
		QualitySequence seq =sut.build();
		assertArrayEquals(
				new byte[]{10,12,13,13,13,13,13,13,8,25},
				toByteArray(seq));
		
	}
}
