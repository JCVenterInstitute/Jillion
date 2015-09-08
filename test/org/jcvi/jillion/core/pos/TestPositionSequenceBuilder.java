/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.pos;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ShortBuffer;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
public class TestPositionSequenceBuilder {
	
	private short[] toShortArray(PositionSequence s){
		ShortBuffer buf = ShortBuffer.allocate((int)s.getLength());

		Iterator<Position> iter = s.iterator();
		while(iter.hasNext()){
			buf.put(IOUtil.toSignedShort(iter.next().getValue()));
		}
		return buf.array();
	}
	@Test
	public void createEmpty(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder();
		PositionSequence seq = sut.build();
		assertEquals(0, seq.getLength());
	}
	@Test
	public void initialArray(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		
		assertArrayEquals(new short[]{10,20,30,40},
				toShortArray(sut.build()));
	}
	
	@Test
	public void replace(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.replace(2, Position.valueOf(35));
		assertArrayEquals(new short[]{10,20,35,40},
				toShortArray(sut.build()));
	}
	
	@Test
	public void reverse(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.reverse();
		assertArrayEquals(new short[]{40,30,20,10},
				toShortArray(sut.build()));
	}
	
	@Test
	public void append(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.append(50);
		assertArrayEquals(new short[]{10,20,30,40, 50},
				toShortArray(sut.build()));
	}
	
	@Test
	public void appendValueGreaterThanShortMax(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.append(Position.valueOf(Short.MAX_VALUE+1));
		assertArrayEquals(new short[]{10,20,30,40, Short.MIN_VALUE},
				toShortArray(sut.build()));
	}
	
	@Test
	public void appendArray(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.append(new short[]{50,60,70});
		assertArrayEquals(new short[]{10,20,30,40, 50,60,70},
				toShortArray(sut.build()));
	}
	@Test
	public void appendOtherBuilder(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.append(new PositionSequenceBuilder(new short[]{50,60,70}));
		assertArrayEquals(new short[]{10,20,30,40, 50,60,70},
				toShortArray(sut.build()));
	}
	
	@Test
	public void appendOtherSequence(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.append(new PositionSequenceBuilder(new short[]{50,60,70}).build());
		assertArrayEquals(new short[]{10,20,30,40, 50,60,70},
				toShortArray(sut.build()));
	}
	@Test
	public void prependArray(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.prepend(new short[]{5,6,7,8});
		assertArrayEquals(new short[]{5,6,7,8,10,20,30,40},
				toShortArray(sut.build()));
	}
	@Test
	public void prependOtherBuilder(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.prepend(new PositionSequenceBuilder(new short[]{5,6,7,8}));
		assertArrayEquals(new short[]{5,6,7,8,10,20,30,40},
				toShortArray(sut.build()));
	}
	@Test
	public void prependOtherSequence(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.prepend(new PositionSequenceBuilder(new short[]{5,6,7,8}).build());
		assertArrayEquals(new short[]{5,6,7,8,10,20,30,40},
				toShortArray(sut.build()));
	}
	@Test
	public void prepend(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.prepend(Short.MAX_VALUE);
		assertArrayEquals(new short[]{Short.MAX_VALUE,10,20,30,40},
				toShortArray(sut.build()));
	}
	@Test
	public void insert(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.insert(1, Position.valueOf(Short.MAX_VALUE));
		assertArrayEquals(new short[]{10,Short.MAX_VALUE,20,30,40},
				toShortArray(sut.build()));
	}
	@Test
	public void insertAtLengthOffsetShouldActLikeAppend(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.insert(4, Position.valueOf(Short.MAX_VALUE));
		assertArrayEquals(new short[]{10,20,30,40,Short.MAX_VALUE},
				toShortArray(sut.build()));
	}
	@Test
	public void insertArray(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.insert(1, new short[]{12,14,16,18});
		assertArrayEquals(new short[]{10,12,14,16,18,20,30,40},
				toShortArray(sut.build()));
	}
	@Test
	public void insertOtherBuilder(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.insert(1, new PositionSequenceBuilder(new short[]{12,14,16,18}));
		assertArrayEquals(new short[]{10,12,14,16,18,20,30,40},
				toShortArray(sut.build()));
	}
	@Test
	public void insertOtherSequence(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.insert(1, new PositionSequenceBuilder(new short[]{12,14,16,18}).build());
		assertArrayEquals(new short[]{10,12,14,16,18,20,30,40},
				toShortArray(sut.build()));
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void insertNegativeOffsetShouldThrowException(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.insert(-1, Position.valueOf(Short.MAX_VALUE));
		
	}
	@Test(expected = IndexOutOfBoundsException.class)
	public void insertOffsetGreatherThanLengthShouldThrowException(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.insert(5, Position.valueOf(Short.MAX_VALUE));		
	}
	@Test
	public void delete(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.delete(Range.of(1,2));	
		assertArrayEquals(new short[]{10,40},
				toShortArray(sut.build()));
	}
	@Test
	public void trim(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.trim(Range.of(1,2));	
		assertArrayEquals(new short[]{20,30},
				toShortArray(sut.build()));
	}
	
	@Test
	public void trimEmptyRangeShouldRemoveEntireSequence(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.trim(new Range.Builder(0).shift(-1).build());	
		assertArrayEquals(new short[0],
				toShortArray(sut.build()));
	}
	
	@Test
	public void copyDoesntAffectOriginal(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		PositionSequenceBuilder copy = sut.copy();
		copy.append(99);
		assertArrayEquals(new short[]{10,20,30,40},
				toShortArray(sut.build()));
		assertArrayEquals(new short[]{10,20,30,40,99},
				toShortArray(copy.build()));
	}
	@Test
	public void originalDoesntAffectCopy(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		PositionSequenceBuilder copy = sut.copy();
		sut.append(99);
		assertArrayEquals(new short[]{10,20,30,40,99},
				toShortArray(sut.build()));
		assertArrayEquals(new short[]{10,20,30,40},
				toShortArray(copy.build()));
	}
	
	@Test
	public void mixOfOperations(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(
										new PositionSequenceBuilder(new short[]{10,20,30,40})
											.build());
		
		sut.append(99) 	//10,20,30,40,99
		.reverse()	//99,40,30,20,10
		.replace(1, Position.valueOf(60))	//99,60,30,20,10
		.delete(Range.of(2,3))//99,60,10
		.prepend(new short[]{1,2,3,4,5,6,7,Short.MAX_VALUE})  //1,2,3,4,5,6,7,Short.MAX_VALUE,99,60,10
		.trim(Range.of(2,8));  //3,4,5,6,7,Short.MAX_VALUE,99
		assertArrayEquals(new short[]{3,4,5,6,7,Short.MAX_VALUE,99},
				toShortArray(sut.build()));
	}
	
	@Test
	public void get(){
		short[] positions = new short[]{10,20,30,40};
		PositionSequenceBuilder sut = new PositionSequenceBuilder(positions);
		
		for(int i=0; i<positions.length; i++){
			assertEquals(positions[i], sut.get(i).getValue());
		}
	}
	
	@Test(expected= IndexOutOfBoundsException.class)
	public void getNegativeLengthShouldThrowIndexOutOfBoundsException(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.get(-1);
	}

	@Test(expected= IndexOutOfBoundsException.class)
	public void getOffsetBeyondLengthShouldThrowIndexOutOfBoundsException(){
		PositionSequenceBuilder sut = new PositionSequenceBuilder(new short[]{10,20,30,40});
		sut.get(4);
	}
}
