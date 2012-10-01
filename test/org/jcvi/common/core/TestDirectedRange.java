package org.jcvi.common.core;

import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDirectedRange {
	Range range = Range.of(0,9);
	@Test
	public void composite(){
		
		DirectedRange sut = DirectedRange.create(range, Direction.REVERSE);
		assertEquals(range, sut.getRange());
		assertEquals(Direction.REVERSE, sut.getDirection());
		assertEquals(range, sut.asRange());
	}
	
	@Test
	public void noDirectionShouldDefaultToForward(){
		DirectedRange sut = DirectedRange.create(range);
		assertEquals(Direction.FORWARD, sut.getDirection());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullRangeShouldThrowException(){
		DirectedRange.create(null);
	}
	@Test(expected = NullPointerException.class)
	public void nullDirectionShouldThrowException(){
		DirectedRange.create(range,null);
	}
	
	@Test
	public void notEqualToNull(){
		assertFalse(DirectedRange.create(range).equals(null));
	}
	@Test
	public void notEqualToNonDirectedRange(){
		assertFalse(DirectedRange.create(range).equals("not a range"));
	}
	@Test
	public void sameValuesAreEqual(){
		DirectedRange a = DirectedRange.create(range);
		DirectedRange b = DirectedRange.create(range);
		TestUtil.assertEqualAndHashcodeSame(a, b);
	}
	@Test
	public void sameReferencesAreEqual(){
		DirectedRange a = DirectedRange.create(range);		
		TestUtil.assertEqualAndHashcodeSame(a, a);
	}
	@Test
	public void differentDirectionsAreNotEqual(){
		DirectedRange a = DirectedRange.create(range);
		DirectedRange b = DirectedRange.create(range, Direction.REVERSE);
		TestUtil.assertNotEqualAndHashcodeDifferent(a, b);
	}
	@Test
	public void differentRangesAreNotEqual(){
		DirectedRange a = DirectedRange.create(range);
		DirectedRange b = DirectedRange.create(new Range.Builder(range).shiftRight(2).build());
		TestUtil.assertNotEqualAndHashcodeDifferent(a, b);
	}
	@Test
	public void parseStringStringDash(){
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("0 - 9"));
		assertEquals("no whitespace",expected, DirectedRange.parse("0-9"));
		assertEquals("tabbed",expected, DirectedRange.parse("0\t-\t9"));
	}
	@Test
	public void parseStringComma(){
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("0 , 9"));
		assertEquals("no whitespace",expected, DirectedRange.parse("0,9"));
		assertEquals("tabbed",expected, DirectedRange.parse("0\t,\t9"));
	}
	@Test
	public void parseStringDots(){
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("0 .. 9"));
		assertEquals("no whitespace",expected, DirectedRange.parse("0..9"));
		assertEquals("tabbed",expected, DirectedRange.parse("0\t..\t9"));
	}
	/////////////////////////////
	@Test
	public void parseStringReverseDash(){
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("9 - 0"));
		assertEquals("no whitespace",expected, DirectedRange.parse("9-0"));
		assertEquals("tabbed",expected, DirectedRange.parse("9\t-\t0"));
	}
	@Test
	public void parseStringReverseComma(){
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("9 , 0"));
		assertEquals("no whitespace",expected, DirectedRange.parse("9,0"));
		assertEquals("tabbed",expected, DirectedRange.parse("9\t,\t0"));
	}
	@Test
	public void parseStringReverseDots(){
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("9 .. 0"));
		assertEquals("no whitespace",expected, DirectedRange.parse("9..0"));
		assertEquals("tabbed",expected, DirectedRange.parse("9\t..\t0"));
	}
	/////////////////////////////
	@Test
	public void parseStringBothNegativeValuesDash(){
		Range range= Range.of(-10,-1);
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("-10 - -1"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-10--1"));
		assertEquals("tabbed",expected, DirectedRange.parse("-10\t-\t-1"));
	}
	@Test
	public void parseStringBothNegativeValuesComma(){
		Range range= Range.of(-10,-1);
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("-10 , -1"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-10,-1"));
		assertEquals("tabbed",expected, DirectedRange.parse("-10\t,\t-1"));
	}
	@Test
	public void parseStringBothNegativeValuesDots(){
		Range range= Range.of(-10,-1);
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("-10 .. -1"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-10..-1"));
		assertEquals("tabbed",expected, DirectedRange.parse("-10\t..\t-1"));
	}
	////////////////////////////
	@Test
	public void parseStringReverseBothNegativeValuesDash(){
		Range range= Range.of(-10,-1);
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("-1 - -10"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-1--10"));
		assertEquals("tabbed",expected, DirectedRange.parse("-1\t-\t-10"));
	}
	@Test
	public void parseStringReverseBothNegativeValuesComma(){
		Range range= Range.of(-10,-1);
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("-1 , -10"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-1,-10"));
		assertEquals("tabbed",expected, DirectedRange.parse("-1\t,\t-10"));
	}
	@Test
	public void parseStringReverseBothNegativeValuesDots(){
		Range range= Range.of(-10,-1);
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("-1 .. -10"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-1..-10"));
		assertEquals("tabbed",expected, DirectedRange.parse("-1\t..\t-10"));
	}
	////////////////////////////
	@Test
	public void parseStringOneNegativeValuesDash(){
		Range range= Range.of(-10,5);
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("-10 - 5"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-10-5"));
		assertEquals("tabbed",expected, DirectedRange.parse("-10\t-\t5"));
	}
	@Test
	public void parseStringOneNegativeValuesComma(){
		Range range= Range.of(-10,5);
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("-10 , 5"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-10,5"));
		assertEquals("tabbed",expected, DirectedRange.parse("-10\t,\t5"));
	}
	@Test
	public void parseStringOneNegativeValuesDots(){
		Range range= Range.of(-10,5);
		DirectedRange expected = DirectedRange.create(range,Direction.FORWARD);
		assertEquals(expected, DirectedRange.parse("-10 .. 5"));
		assertEquals("no whitespace",expected, DirectedRange.parse("-10..5"));
		assertEquals("tabbed",expected, DirectedRange.parse("-10\t..\t5"));
	}
	/////////////////////////////
	@Test
	public void parseStringReverseOneNegativeValuesDash(){
		Range range= Range.of(-10,5);
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("5 - -10"));
		assertEquals("no whitespace",expected, DirectedRange.parse("5--10"));
		assertEquals("tabbed",expected, DirectedRange.parse("5\t-\t-10"));
	}
	@Test
	public void parseStringReverseOneNegativeValuesComma(){
		Range range= Range.of(-10,5);
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("5 , -10"));
		assertEquals("no whitespace",expected, DirectedRange.parse("5,-10"));
		assertEquals("tabbed",expected, DirectedRange.parse("5\t,\t-10"));
	}
	@Test
	public void parseStringReverseOneNegativeValuesDots(){
		Range range= Range.of(-10,5);
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("5 .. -10"));
		assertEquals("no whitespace",expected, DirectedRange.parse("5..-10"));
		assertEquals("tabbed",expected, DirectedRange.parse("5\t..\t-10"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void parseStringInvalidRangeShouldThrowException(){
		DirectedRange.parse("not a range");
	}
	@Test(expected = NullPointerException.class)
	public void parseStringNullRangeShouldThrowException(){
		DirectedRange.parse(null);
	}
	
	@Test
	public void parseCoords(){
		DirectedRange expected = DirectedRange.create(range);
		assertEquals(expected, DirectedRange.parse(0,9));
	}
	@Test
	public void parseCoordsWithCoordinateSystem(){
		DirectedRange expected = DirectedRange.create(range);
		assertEquals(expected, DirectedRange.parse(1,10,Range.CoordinateSystem.RESIDUE_BASED));
	}
	@Test
	public void parseCoordsAsString(){
		DirectedRange expected = DirectedRange.create(range);
		assertEquals(expected, DirectedRange.parse("0","9"));
	}
	@Test
	public void parseCoordsAsStringWithCoordinateSystem(){
		DirectedRange expected = DirectedRange.create(range);
		assertEquals(expected, DirectedRange.parse("1","10",Range.CoordinateSystem.RESIDUE_BASED));
	}
	////////////////////////////////////
	@Test
	public void parseReverseCoords(){
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse(9,0));
	}
	@Test
	public void parseReverseCoordsWithCoordinateSystem(){
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse(10,1,Range.CoordinateSystem.RESIDUE_BASED));
	}
	@Test
	public void parseReverseCoordsAsString(){
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("9","0"));
	}
	@Test
	public void parseReverseCoordsAsStringWithCoordinateSystem(){
		DirectedRange expected = DirectedRange.create(range,Direction.REVERSE);
		assertEquals(expected, DirectedRange.parse("10","1",Range.CoordinateSystem.RESIDUE_BASED));
	}
}
