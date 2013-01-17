package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestGrowableByteArray {

	@Test
	public void append(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		byte[] expected = new byte[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void replace(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.replace(1, (byte)15);
		byte[] expected = new byte[]{10,15,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendBeyondCapacityShouldGrowArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		sut.append((byte)60);
		
		byte[] expected = new byte[]{10,20,30,40,50,60};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void remove(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.remove(2);
		
		byte[] expected = new byte[]{10,20,40};
		assertToArrayCorrect(sut, expected);
	}
	private void assertToArrayCorrect(GrowableByteArray sut, byte[] expected) {
		assertEquals(expected.length, sut.getCurrentLength());
		assertArrayEquals(expected, sut.toArray());
		for(int i=0; i< expected.length; i++){
			assertEquals(expected[i], sut.get(i));
		}
	}
	@Test
	public void removeRange(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.remove(Range.of(1,2));
		
		byte[] expected = new byte[]{10,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void removeEmptyRangeShouldDoNothing(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.remove(new Range.Builder().shift(2).build());
		
		byte[] expected = new byte[]{10,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insert(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(1, (byte)15);
		
		byte[] expected = new byte[]{10,15,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void insertAtOffsetLengthShouldActLikeAppend(){
		GrowableByteArray sut = new GrowableByteArray(4);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(4, (byte)50);
		
		byte[] expected = new byte[]{10,20,30,40,50};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(1, new byte[]{15,16,17,18,19});
		
		byte[] expected = new byte[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	@Test
	public void insertOtherGrowableByteArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.insert(1, new GrowableByteArray(new byte[]{15,16,17,18,19}));
		
		byte[] expected = new byte[]{10,15,16,17,18,19,20,30,40};
		assertToArrayCorrect(sut, expected);
	}
	
	@Test
	public void prepend(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.prepend((byte)10);
		
		byte[] expected = new byte[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void prependArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.prepend(new byte[]{10,11,12,13});
		
		byte[] expected = new byte[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	
	@Test
	public void prependOtherGrowableByteArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.prepend(new GrowableByteArray(new byte[]{10,11,12,13}));
		
		byte[] expected = new byte[]{10,11,12,13,20,30,40};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.append(new byte[]{50,60,70,80,90});
		byte[] expected = new byte[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void appendOtherGrowableByteArray(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		sut.append(new GrowableByteArray(new byte[]{50,60,70,80,90}));
		byte[] expected = new byte[]{10,20,30,40,50,60,70,80,90};
		assertArrayEquals(expected, sut.toArray());
	}
	@Test
	public void constructUsingInitialArray(){
		byte[] array = new byte[]{10,20,30,40,50};
		GrowableByteArray sut = new GrowableByteArray(array);
		assertArrayEquals(array, sut.toArray());
	}
	@Test
	public void mixOfAllOperations(){
		byte[] initialArray = new byte[]{10,20,30,40,50};
		GrowableByteArray sut = new GrowableByteArray(initialArray);
		sut.append((byte)60);  //10,20,30,40,50,60
		sut.remove(3);  //10,20,30,50,60
		sut.prepend((byte)5);  //5,10,20,30,50,60
		sut.replace(2, (byte)15); //5,10,15,30,50,60
		sut.insert(3, new byte[]{25,26,27,28,29}); //5,10,15,25,26,27,28,29,30,50,60
		
		sut.reverse();  //60,50,30,29,28,27,26,25,15,10,5
		sut.remove(1);  //60,30,29,28,27,26,25,15,10,5
		sut.remove(Range.of(3,5)); //60,30,29,25,15,10,5
		sut.append((byte)99);
		
		byte[] expected = new byte[]{60,30,29,25,15,10,5,99};
		assertArrayEquals(expected, sut.toArray());
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithNegativeSizeShouldThrowException(){
		new GrowableByteArray(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void constructorWithSizeZeroShouldThrowException(){
		new GrowableByteArray(0);
	}
	@Test(expected = NullPointerException.class)
	public void constructorWithNullArrayShouldThrowException(){
		new GrowableByteArray(null);
	}
	@Test
	public void copy(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		GrowableByteArray copy = sut.copy();
		
		byte[] expected = new byte[]{10,20,30,40};
		assertArrayEquals(expected, sut.toArray());
		assertArrayEquals(expected, copy.toArray());
	}
	
	@Test
	public void modificationsToCopyShouldNotAffectOriginal(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		GrowableByteArray copy = sut.copy();
		copy.remove(2);

		assertArrayEquals(new byte[]{10,20,30,40}, sut.toArray());
		assertArrayEquals(new byte[]{10,20,40}, copy.toArray());
	}
	@Test
	public void modificationsToOriginalShouldNotAffectCopy(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		
		GrowableByteArray copy = sut.copy();
		sut.remove(2);

		assertArrayEquals(new byte[]{10,20,30,40}, copy.toArray());
		assertArrayEquals(new byte[]{10,20,40}, sut.toArray());
	}
	
	@Test
	public void reverseEvenNumberOfValues(){
		GrowableByteArray sut = new GrowableByteArray(5);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.reverse();
		
		assertArrayEquals(new byte[]{40,30,20,10}, sut.toArray());
	}
	@Test
	public void reverseOddNumberOfValues(){
		GrowableByteArray sut = new GrowableByteArray(10);
		sut.append((byte)10);
		sut.append((byte)20);
		sut.append((byte)30);
		sut.append((byte)40);
		sut.append((byte)50);
		sut.reverse();
		
		assertArrayEquals(new byte[]{50,40,30,20,10}, sut.toArray());
	}
}
