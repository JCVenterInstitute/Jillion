/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jul 18, 2007
 *
 * @author dkatzel
 */
package org.jcvi;



import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.Range.CoordinateSystem;
import org.junit.Test;

public class TestRange{

    private Range range = Range.buildRange(1,10);
    private Range emptyRange = Range.buildRange(0, -1);
    @Test
    public void testEquals_null_notEqual(){
        assertFalse(range.equals(null));

    }
    @Test
    public void testEquals_sameRef_notEqual(){
        assertEquals(range,range);
        assertEquals(range.hashCode(),range.hashCode());
    }
    @Test public void testEquals_diffObj_notEqual(){
        final Object object = new Object();
        assertFalse(range.equals(object));
        assertFalse(range.hashCode()==object.hashCode());
    }

    @Test public void testEquals_sameLeftSameRightDiffSystem_notEqual(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,range.getStart(),range.getEnd());
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftSameRight_notEqual(){
        final Range range2 = Range.buildRange(range.getStart()-1,range.getEnd());
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRightDiffSystem_equal(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,range.getStart()+1,range.getEnd()+1);
        assertEquals(range,range2);
        assertEquals(range.hashCode(),range2.hashCode());
        assertNotSame(range,range2);
    }

    @Test public void testEquals_differentLeftDifferentRightDiffSystem_notEqual(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,range.getStart()+1,range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRight_notEqual(){
        final Range range2 = Range.buildRange(range.getStart(),range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRightDiffSystem_equal(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,range.getStart(),range.getEnd()+1);
        assertEquals(range,range2);
        assertEquals(range.hashCode(),range2.hashCode());
        assertNotSame(range,range2);
    }

    @Test public void testEquals_sameLeftDifferentRightDiffSystem_notEqual(){
        final Range range2 = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,range.getStart(),range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRight_notEqual(){
        final Range range2 = Range.buildRange(range.getStart()+1,range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }


    @Test public void testConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(left,right);
        assertEquals(left,sut.getStart());
        assertEquals(right, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLength(){
        int left = 10;
        int length = 10;

        Range sut = Range.buildRangeOfLength(left,length);
        assertEquals(left,sut.getStart());
        assertEquals(left+length-1, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLength_emptyRange(){
        int left = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLength(left,length);
        assertEquals(left,sut.getStart());
        assertEquals(left+length-1, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLengthSpaceBasedRange_emptyRange(){
        int left = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLength(Range.CoordinateSystem.SPACE_BASED,left,length);
        assertEquals(left,sut.getStart());
        assertEquals(left+length, sut.getLocalEnd());
        assertEquals(left+length-1, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLengthResidueBasedRange_emptyRange(){
        int left = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLength(Range.CoordinateSystem.RESIDUE_BASED,left,length);
        assertEquals(left-1,sut.getStart());
        assertEquals(left,sut.getLocalStart());
        assertEquals(left+length-1-1, sut.getEnd());
        assertEquals(left+length-1, sut.getLocalEnd());
    }

    
    @Test(expected=IllegalArgumentException.class)
    public void testBuildRangeOfLength_negativeRange(){
        int left = 0;
        int length =-1;

        Range.buildRangeOfLength(left,length);
    }

    @Test
    public void testBuildRangeOfLengthFromEndCoordinate(){
        int right = 19;
        int length = 10;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLengthFromEndCoordinate_emptyRange(){
        int right = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right, sut.getEnd());
    }
    
    @Test
    public void testBuildRangeOfLengthFromEndCoordinateSpaceBased_emptyRange(){
        int right = 10;
        int length = 0;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(Range.CoordinateSystem.SPACE_BASED,right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right-1, sut.getEnd());
        assertEquals(right, sut.getLocalEnd());
    }

    @Test
    public void testBuildRangeOfLengthFromEndCoordinateResidueBased(){
        int right = 19;
        int length = 10;

        Range sut = Range.buildRangeOfLengthFromEndCoordinate(Range.CoordinateSystem.RESIDUE_BASED,right,length);
        assertEquals(length,sut.getLength());
        assertEquals(right-1, sut.getEnd());
        assertEquals(right, sut.getLocalEnd());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuildRangeOfLengthFromEndCoordinate_negativeRange(){
        int right = 0;
        int length =-1;

        Range.buildRangeOfLengthFromEndCoordinate(right,length);
    }

    @Test public void testZeroBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(Range.CoordinateSystem.ZERO_BASED,left,right);
        assertEquals(left,sut.getLocalStart());
        assertEquals(right, sut.getLocalEnd());
        assertEquals(left,sut.getStart());
        assertEquals(right, sut.getEnd());
    }

    @Test public void testSpaceBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,left,right);
        assertEquals(left,sut.getLocalStart());
        assertEquals(right, sut.getLocalEnd());
        assertEquals(left,sut.getStart());
        assertEquals(right, sut.getEnd()+1);
    }

    @Test public void testResidueBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,left,right);
        assertEquals(left,sut.getLocalStart());
        assertEquals(right, sut.getLocalEnd());
        assertEquals(left,sut.getStart()+1);
        assertEquals(right, sut.getEnd()+1);
    }

    @Test public void testResidueBaseEmptyRangeConstruction(){
        int left = 1;
        int right =0;

        Range sut = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,left,right);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testSpaceBaseEmptyRangeConstruction(){
        int left = 0;
        int right =0;

        Range sut = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,left,right);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
        assertEquals(sut.hashCode(),emptyRange.hashCode());
        assertNotSame(sut,emptyRange);
    }

    @Test public void testDefaultBuildEmptyRangeConstruction(){

        Range sut = Range.buildEmptyRange();
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testDefaultCoordinateSpecificBuildEmptyRangeConstruction(){
        Range sut = Range.buildEmptyRange(0);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testResidueCoordinateSpecificBuildEmptyRangeConstruction(){
        Range sut = Range.buildEmptyRange(Range.CoordinateSystem.RESIDUE_BASED,1);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testSpaceCoordinateSpecificEmptyRangeConstruction(){
        Range sut = Range.buildEmptyRange(Range.CoordinateSystem.SPACE_BASED,0);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testNonZeroCoordinateSpecificEmptyRangeConstruction(){
        int zeroRangeLocation = 7;
        Range sut = Range.buildEmptyRange(Range.CoordinateSystem.SPACE_BASED,zeroRangeLocation);
        assertTrue(sut.isEmpty());
        assertEquals(sut.getLocalStart(),zeroRangeLocation);
        assertEquals(sut.getLocalEnd(),zeroRangeLocation);
        assertFalse(sut.equals(emptyRange));
    }

    @Test
    public void copyConstructor(){
        Range copy = range.copy();
        assertEquals(range, copy);
        assertNotSame(range, copy);
    }

    @Test(expected=IllegalArgumentException.class) public void testInvalidRangeConstruction(){
        int left = 0;
        int right =-1;

        Range.buildRange(Range.CoordinateSystem.SPACE_BASED,left,right);
    }

    @Test(expected=IllegalArgumentException.class) public void testConstructor_leftGreaterThanRight_shouldThrowIllegalArgumentException(){
        Range.buildRange(10,0);
    }

    @Test public void testConvertRange_sameCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.buildRange(Range.CoordinateSystem.ZERO_BASED,rangeStart,rangeEnd);
        Range convertedRange = range.convertRange(range.getRangeCoordinateSystem());
        assertEquals(range,convertedRange);
        assertEquals(range.getStart(),convertedRange.getStart());
        assertEquals(range.getEnd(),convertedRange.getEnd());
        assertEquals(convertedRange.getLocalStart(),rangeStart);
        assertEquals(convertedRange.getLocalEnd(),rangeEnd);
        assertNotSame(range,convertedRange);
    }

    @Test public void testConvertRange_zeroToSpaceCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.buildRange(Range.CoordinateSystem.ZERO_BASED,rangeStart,rangeEnd);
        Range convertedRange = range.convertRange(Range.CoordinateSystem.SPACE_BASED);
        assertEquals(range,convertedRange);
        assertEquals(range.getStart(),convertedRange.getStart());
        assertEquals(range.getEnd(),convertedRange.getEnd());
        assertEquals(convertedRange.getLocalStart(),rangeStart);
        assertEquals(convertedRange.getLocalEnd(),rangeEnd+1);
        assertNotSame(range,convertedRange);
    }

    @Test public void testConvertRange_zeroToResidueCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.buildRange(Range.CoordinateSystem.ZERO_BASED,rangeStart,rangeEnd);
        Range convertedRange = range.convertRange(Range.CoordinateSystem.RESIDUE_BASED);
        assertEquals(range,convertedRange);
        assertEquals(range.getStart(),convertedRange.getStart());
        assertEquals(range.getEnd(),convertedRange.getEnd());
        assertEquals(convertedRange.getLocalStart(),rangeStart+1);
        assertEquals(convertedRange.getLocalEnd(),rangeEnd+1);
        assertNotSame(range,convertedRange);
    }
    @Test public void testConvertRange_spaceToResidueCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.buildRange(Range.CoordinateSystem.SPACE_BASED,rangeStart,rangeEnd);
        Range convertedRange = range.convertRange(Range.CoordinateSystem.RESIDUE_BASED);
        assertEquals(range,convertedRange);
        assertEquals(range.getStart(),convertedRange.getStart());
        assertEquals(range.getEnd(),convertedRange.getEnd());
        assertEquals(convertedRange.getLocalStart(),rangeStart+1);
        assertEquals(convertedRange.getLocalEnd(),rangeEnd);
        assertNotSame(range,convertedRange);
    }

    @Test public void testConvertEmptyRange_zeroToResidueCoordinateSystem(){
        long rangeStart = 0;
        Range range = Range.buildEmptyRange(Range.CoordinateSystem.ZERO_BASED,0);
        Range convertedRange = range.convertRange(Range.CoordinateSystem.RESIDUE_BASED);
        assertEquals(range,convertedRange);
        assertEquals(range.getStart(),convertedRange.getStart());
        assertEquals(range.getEnd(),convertedRange.getEnd());
        assertEquals(convertedRange.getLocalStart(),rangeStart+1);
        assertEquals(convertedRange.getLocalEnd(),rangeStart);
        assertNotSame(range,convertedRange);
    }

    @Test public void testSubRangeOf_nullRange_isNotSubRange(){
        assertFalse(range.isSubRangeOf(null));
    }

    @Test public void testSubRangeOf_leftIsSameRightIsLess_isSubRange(){
        Range subRange = Range.buildRange(range.getStart(),range.getEnd()-1);
        assertTrue(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsSameRightIsMore_isNotSubRange(){
        Range subRange = Range.buildRange(range.getStart(),range.getEnd()+1);
        assertFalse(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsLessRightIsMore_isNotSubRange(){
        Range notSubRange = Range.buildRange(range.getStart()-1,range.getEnd()+1);
        assertFalse(notSubRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsLessRightIsLess_isNotSubRange(){
        Range notSubRange = Range.buildRange(range.getStart()-1,range.getEnd()-1);
        assertFalse(notSubRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsLessRightIsSame_isNotSubRange(){
        Range notSubRange = Range.buildRange(range.getStart()-1,range.getEnd());
        assertFalse(notSubRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsMoreRightIsSame_isSubRange(){
        Range subRange = Range.buildRange(range.getStart()+1,range.getEnd());
        assertTrue(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsMoreRightIsLess_isSubRange(){
        Range subRange = Range.buildRange(range.getStart()+1,range.getEnd()-1);

        assertTrue(subRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsMoreRightIsMore_isNotSubRange(){
        Range subRange = Range.buildRange(range.getStart()+1,range.getEnd()+1);

        assertFalse(subRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsSameRightIsSame_isSubRange()
    {
        assertTrue(range.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_emptyRange()
    {
        assertFalse(range.isSubRangeOf(emptyRange));
    }

    @Test public void testSubRangeOf_realRangeVsEmptyRange_isSubRange()
    {
        assertFalse(emptyRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_emptyRangeVsEmptyRange_isSubRange()
    {
        assertTrue(emptyRange.isSubRangeOf(emptyRange));
    }

    @Test public void testSize(){
        long expectedLength = range.getEnd()-range.getStart()+1;
        long actualLength = range.size();
        assertEquals(expectedLength,actualLength);
    }

    @Test public void testSize_sameLeftAndRight_sizeIsOne(){
        Range oneRange = Range.buildRange(5,5);
        assertEquals(1, oneRange.size());
    }

    @Test public void testSize_leftAndRightAreZero_sizeIsOne(){
        Range zeroRange = Range.buildRange(0,0);
        assertEquals(1, zeroRange.size());
    }
    
    @Test
    public void intersectsSingleCoordinate(){
        assertTrue(range.intersects(5));
    }
    @Test
    public void intersectsSingleCoordinateBeforeRangeShouldNotIntersect(){
        assertFalse(range.intersects(0));
    }
    @Test
    public void intersectsSingleCoordinateAfterRangeShouldNotIntersect(){
        assertFalse(range.intersects(range.getEnd()+1));
    }
    @Test public void testIntersects()
    {
        Range target = Range.buildRange(5, 15);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_doesntReallyIntersect()
    {
        Range target = Range.buildRange(15,25);
        assertFalse(this.range.intersects(target));
    }

    @Test public void testIntersects_barelyIntersectsStart()
    {
        Range target = Range.buildRange(-10, 1);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_barelyIntersectsEnd()
    {
        Range target = Range.buildRange(10, 12);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_subRange()
    {
        Range target = Range.buildRange(5, 7);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_sameRange()
    {
        assertTrue(this.range.intersects(this.range));
    }
    @Test public void testInstersects_emptyRange_shouldReturnFalse(){
        assertFalse(this.range.intersects(emptyRange));
    }
    @Test public void testEndsBefore_emptyRange_shouldReturnFalse(){
        assertFalse(this.range.endsBefore(emptyRange));
    }
    @Test public void testStartsBefore_emptyRange_shouldReturnFalse(){
        assertFalse(this.range.startsBefore(emptyRange));
    }

    @Test public void testIntersects_null()
    {
        try
        {
            this.range.intersects(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

  
    @Test public void testIntersection_normal()
    {
        Range target = Range.buildRange(5,15);
        assertEquals(Range.buildRange(5, 10), this.range.intersection(target));
    }

    @Test public void testIntersection_subrange()
    {
        Range target = Range.buildRange(5,7);
        assertEquals(target, this.range.intersection(target));
    }

    @Test public void testIntersection_superrange()
    {
        Range target = Range.buildRange(-4, 20);
        assertEquals(this.range, this.range.intersection(target));
    }

    @Test public void testIntersection_onePointIntersectStart()
    {
        Range target = Range.buildRange(-4, 1);
        assertEquals(Range.buildRange(1, 1), this.range.intersection(target));
    }

    @Test public void testIntersection_onePointIntersectEnd()
    {
        Range target = Range.buildRange(10, 12);
        assertEquals(Range.buildRange(10, 10), this.range.intersection(target));
    }

    @Test public void testIntersection_empty()
    {
        assertEquals(emptyRange, this.range.intersection(emptyRange));
    }

    @Test public void testIntersection_nointersection()
    {
        Range target = Range.buildRange(15,25);
        assertEquals(emptyRange, this.range.intersection(target));
    }
    @Test public void testIntersection_self()
    {
        assertEquals(this.range, this.range.intersection(this.range));
    }

    @Test public void testIntersection_null()
    {
        try
        {
            this.range.intersection(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

    @Test public void testStartsBefore()
    {
        Range target = Range.buildRange(15,25);
        assertTrue(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_false()
    {
        Range target = Range.buildRange(-5, 10);
        assertFalse(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_sameStart()
    {
        Range target = Range.buildRange(1, 15);
        assertFalse(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_sameRange()
    {
        assertFalse(this.range.startsBefore(this.range));
    }

    @Test public void testStartsBefore_null()
    {
        try
        {
            this.range.startsBefore(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

    @Test public void testEndsBefore()
    {
        Range target = Range.buildRange(12,20);
        assertTrue(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_false()
    {
        Range target = Range.buildRange(-5, 8);
        assertFalse(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_sameEnd()
    {
        Range target = Range.buildRange(5, 10);
        assertFalse(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_sameRange()
    {
        assertFalse(this.range.endsBefore(this.range));
    }

    @Test public void testEndsBefore_null()
    {
        try
        {
            this.range.endsBefore(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this
        }
    }

    private void assertRangesEqual(Range[] expected, Range[] actual)
    {
        assertEquals("Range array lengths don't match.", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], actual[i]);
        }
    }

    private Range[] rangeList(Range ... ranges)
    {
        return ranges;
    }

    @Test public void testUnion()
    {
        Range add = Range.buildRange(5, 15);
        assertRangesEqual(rangeList(Range.buildRange(1, 15)), this.range.union(add));
    }
    @Test public void testUnion_startsAfterTarget(){
        Range target= Range.buildRange(this.range.getStart()-30,this.range.getStart()-10 );
        assertRangesEqual(rangeList(target,this.range), this.range.union(target));
    }

    @Test public void testUnion_disjoint()
    {
        Range add = Range.buildRange(15, 25);
        assertRangesEqual(rangeList(this.range, add), this.range.union(add));
    }

    @Test public void testUnion_subrange()
    {
        Range add = Range.buildRange(4, 8);
        assertRangesEqual(rangeList(this.range), this.range.union(add));
    }

    @Test public void testUnion_superrange()
    {
        Range add = Range.buildRange(-4, 18);
        assertRangesEqual(rangeList(add), this.range.union(add));
    }

    @Test public void testUnion_sizeOneBeginning()
    {
        Range add = Range.buildRange(1, 1);
        assertRangesEqual(rangeList(this.range), this.range.union(add));
    }

    @Test public void testUnion_sizeOneEnd()
    {
        Range add = Range.buildRange(10, 10);
        assertRangesEqual(rangeList(this.range), this.range.union(add));
    }

    @Test public void testUnion_empty()
    {
        assertRangesEqual(rangeList(this.range), this.range.union(emptyRange));
    }

    @Test public void testUnion_self()
    {
        assertRangesEqual(rangeList(this.range), this.range.union(this.range));
    }

    @Test public void testUnion_null()
    {
        try
        {
            this.range.union(null);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            // We expect this.
        }
    }

    @Test public void testToString()
    {
        assertEquals("[ 1 - 10 ]/0B", this.range.toString());
    }
    private String convertIntoString(Object left, Object right, String seperator){
        StringBuilder result = new StringBuilder();
        result.append(left);
        result.append(seperator);
        result.append(right);
        return result.toString();
    }
    @Test
    public void validDotParse(){
        validParse("\t..  ");
    }
    @Test
    public void invalidDotParseShouldThrowIllegalArgumentException(){
        invalidParseShouldFail("..");
    }
    @Test
    public void validDashParse(){
        validParse("\t-  ");
    }
    @Test
    public void invalidDashParseShouldThrowIllegalArgumentException(){
        invalidParseShouldFail("-");
    }
    @Test
    public void validCommaParse(){
        validParse("\t,  ");
    }
    @Test
    public void invalidCommaParseShouldThrowIllegalArgumentException(){
        invalidParseShouldFail(",");
    }
    
    @Test
    public void invalidParseNotARangeAtAll(){
        assertParseShouldFail("notARange");
    }
    private void validParse(String sep) {
        long start = 15;
        long end = 45;
        final Range expected = Range.buildRange(start, end);
        assertEquals(expected,Range.parseRange(convertIntoString(start,end,sep)));
        assertEquals(expected,Range.parseRange(convertIntoString(start,end," "+sep+"\t")));
    }
    
  
    private void invalidParseShouldFail(final String sep) {
        long start = 15;
        assertParseShouldFail(convertIntoString(start,"notANumber",sep));
        assertParseShouldFail(convertIntoString(start,"notANumber"," "+sep+"\t"));
    }
    private void assertParseShouldFail(String asString) {
        try{            
            Range.parseRange(asString);
            fail("shouldthrow IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("can not parse "+ asString +" into a Range", e.getMessage());
        }
    }
    @Test
    public void buildRange(){
        assertEquals(range, Range.buildRange(range.getStart(), range.getEnd()));
    }
    @Test
    public void buildRangeWithCoordinateSystem(){
        assertEquals(range, Range.buildRange(CoordinateSystem.RESIDUE_BASED,range.getStart()+1, range.getEnd()+1));
    }
    @Test(expected = NullPointerException.class)
    public void buildRangeWithNullCoordinateSystemShouldThrowNPE(){
        Range.buildRange(null,range.getStart()+1, range.getEnd()+1);
    }
    @Test(expected = NullPointerException.class)
    public void buildEmptyRangeWithNullCoordinateSystemShouldThrowNPE(){
        Range.buildEmptyRange(null,range.getStart()+1);
    }
    @Test
    public void buildEmptyRange(){
        Range emptyRange = Range.buildRange(10, 9);
        assertEquals(10, emptyRange.getStart());
        assertEquals(9, emptyRange.getEnd());
        assertTrue(emptyRange.isEmpty());
    }
    
    @Test
    public void buildInclusiveRangesEmptyListShouldReturnEmptyRange(){
        Range emptyRange = Range.buildInclusiveRange(Collections.<Range>emptyList());
        assertEquals(0, emptyRange.getStart());
        assertEquals(-1, emptyRange.getEnd());
        assertTrue(emptyRange.isEmpty());
    }
    
    @Test
    public void buildInclusiveRange(){
        List<Range> ranges = Arrays.asList(
                    Range.buildRange(10, 20),
                    Range.buildRange( 50, 100),
                    Range.buildRange( -5, 3)
                    );
        Range expected = Range.buildRange(-5,100);
        assertEquals(expected, Range.buildInclusiveRange(ranges));
    }
    
    @Test
    public void shiftRight(){
        int units = 5;
        Range shifted = range.shiftRight(units);
        assertEquals(range.getStart()+units, shifted.getStart());
        assertEquals(range.getEnd()+units, shifted.getEnd());
        assertEquals(range.size(), shifted.size());
        
    }
    @Test
    public void shiftLeft(){
        int units = 5;
        Range shifted = range.shiftLeft(units);
        assertEquals(range.getStart()-units, shifted.getStart());
        assertEquals(range.getEnd()-units, shifted.getEnd());
        assertEquals(range.size(), shifted.size());
        
    }

    @Test
    public void mergeEmpty(){
        assertTrue(Range.mergeRanges(Collections.<Range>emptyList()).isEmpty());
    }
    
    @Test
    public void mergeOneRange(){
        
        final List<Range> oneRange = Arrays.asList(range);
        assertEquals(
                oneRange,
                Range.mergeRanges(oneRange));
    }
    
    @Test
    public void mergeTwoRangesNoOverlapShouldReturnTwoRanges(){
        Range nonOverlappingRange = Range.buildRange(12, 20);
        List<Range> nonOverlappingRanges = Arrays.asList(range,nonOverlappingRange);
        assertEquals(
                nonOverlappingRanges,
                Range.mergeRanges(nonOverlappingRanges));
        
    }
    
    @Test
    public void mergeTwoRanges(){
        Range overlappingRange = Range.buildRange(5, 20);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeTwoAdjacentButNotOverlappingRangesShouldMergeIntoOne(){
        Range adjacentRange = Range.buildRange(11, 20);
        List<Range> rangesToMerge = Arrays.asList(range,adjacentRange);
        List<Range> expectedRanges = Arrays.asList(
                Range.buildRange(range.getStart(), adjacentRange.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(rangesToMerge));
        
    }
    @Test
    public void mergeThreeRanges(){
        Range overlappingRange_1 = Range.buildRange(5, 20);
        Range overlappingRange_2 = Range.buildRange(15, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange_1,overlappingRange_2);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange_2.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeThreeRangesThirdRangeConnectsTwoRangeIslands(){
        Range overlappingRange_2 = Range.buildRange(5, 20);
        Range overlappingRange_1 = Range.buildRange(15, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange_1,overlappingRange_2);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange_1.getEnd()));
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeThreeRangesOnlyTwoMerge(){
        Range overlappingRange = Range.buildRange(5, 20);
        Range nonOverlappingRange = Range.buildRange(22, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange,nonOverlappingRange);
        List<Range> expectedRanges = Arrays.asList(Range.buildRange(range.getStart(), overlappingRange.getEnd()),nonOverlappingRange);
        assertEquals(
                expectedRanges,
                Range.mergeRanges(overlappingRanges));
        
    }
    @Test
    public void mergeThreeConsecutiveRanges(){
        List<Range> consecutiveRanges = Arrays.asList(range,range,range);
        List<Range> expectedRanges = Arrays.asList(range);
        assertEquals(
                expectedRanges,
                Range.mergeRanges(consecutiveRanges));
    }
    private Range createRangeSeparatedFrom(Range range, int distance){
        return Range.buildRangeOfLength(range.getEnd()+ distance, range.size());
    }
    @Test
    public void mergeRightClusteredRanges(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(range.getStart(), clusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeClusteredRangesAbutmentShouldStillMerge(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,clusterDistance+1);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(range.getStart(), clusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeClusteredRangesBeyondClusterShouldNotMerge(){
        int clusterDistance=30;
        Range unclusterableRange = createRangeSeparatedFrom(range,clusterDistance+2);
        List<Range> clusteredRanges = Arrays.asList(range, unclusterableRange);
        assertEquals(
                clusteredRanges,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeLeftClusteredRanges(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,-clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(clusterableRange.getStart(), range.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeThreeClusteredRanges(){
        int clusterDistance=30;
        Range leftClusterableRange = createRangeSeparatedFrom(range,-clusterDistance);
        Range rightClusterableRange = createRangeSeparatedFrom(range,clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, leftClusterableRange,rightClusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.buildRange(leftClusterableRange.getStart(), rightClusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Range.mergeRanges(clusteredRanges,clusterDistance));
    }
    
    @Test
    public void mergeWithNegativeClusterDistanceShouldThrowIllegalArgumentException(){
        try{
            Range.mergeRanges(Arrays.asList(range), -1);
            fail("should catch illegal argumentException when cluster distance is -1");
        }
        catch(IllegalArgumentException e){
            assertEquals("cluster distance can not be negative",e.getMessage());
        }
    }
    
    @Test
    public void growRight(){
        Range expected = Range.buildRange(1, 15);
        assertEquals(expected, range.grow(0, 5));
    }
    @Test
    public void growLeft(){
        Range expected = Range.buildRange(-4, 10);
        assertEquals(expected, range.grow(5, 0));
    }
    @Test
    public void grow(){
        Range expected = Range.buildRange(-4, 15);
        assertEquals(expected, range.grow(5, 5));
    }
    
    @Test
    public void shrinkLeft(){
        Range expected = Range.buildRange(6, 10);
        assertEquals(expected, range.shrink(5, 0));
    }
    @Test
    public void shrinkRight(){
        Range expected = Range.buildRange(1, 5);
        assertEquals(expected, range.shrink(0, 5));
    }
    @Test
    public void shrink(){
        Range expected = Range.buildRange(6, 5);
        assertEquals(expected, range.shrink(5, 5));
    }
    
    @Test
    public void convertCoordinateSystem(){
        Range convertedRange = range.convertRange(CoordinateSystem.RESIDUE_BASED);
        assertEquals(range.getStart()+1, convertedRange.getLocalStart());
        assertEquals(range.getEnd()+1, convertedRange.getLocalEnd());
        assertEquals(range.getLength(), convertedRange.getLength());
    }
    @Test(expected = NullPointerException.class)
    public void convertNullCoordinateSystemShouldThrowNPE(){
        range.convertRange(null);
    }
    
    @Test
    public void iterator(){
        Iterator<Long> iter = range.iterator();
        assertTrue(iter.hasNext());
        for(long l = range.getStart(); l<= range.getEnd(); l++){
            assertEquals(Long.valueOf(l), iter.next());
        }
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void complimentNoIntersectionShouldReturnOriginalRange(){
        Range noOverlapRange = range.shiftRight(1000);
        assertEquals(Arrays.asList(range),range.compliment(noOverlapRange));
    }
    
    @Test
    public void complimentOfSubRangeShouldReturn2DisjointRanges(){
        Range subrange = range.shrink(2, 2);
        assertEquals(Arrays.asList(Range.buildRange(range.getStart(),2), Range.buildRange(range.getEnd()-1, range.getEnd())),
                range.compliment(subrange));
    }
    
    @Test
    public void complimentOfSuperRangeShouldReturnEmptyList(){
        Range superRange = range.grow(2, 2);
        assertEquals(Collections.emptyList(), range.compliment(superRange));
    }
    @Test
    public void complimentOfLeftSideShouldReturnArrayOfOneElementContainingRightSide(){
        Range left = range.shrink(0, 2);
        assertEquals(Arrays.asList(Range.buildRange(range.getEnd()-1, range.getEnd())),
                range.compliment(left));
    }
    
    @Test
    public void complimentOfRightSideShouldReturnArrayOfOneElementContainingLeftSide(){
        Range right = range.shrink(2, 0);
        assertEquals(Arrays.asList(Range.buildRange(range.getStart(),2)),
                range.compliment(right));
    }
}
