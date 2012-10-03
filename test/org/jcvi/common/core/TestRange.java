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
package org.jcvi.common.core;



import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.junit.Test;

public class TestRange{
    private Range range = Range.of(1,10);
    private Range emptyRange = Range.of(0, -1);
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
        final Range range2 = Range.of(Range.CoordinateSystem.SPACE_BASED,range.getBegin(),range.getEnd());
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftSameRight_notEqual(){
        final Range range2 = Range.of(range.getBegin()-1,range.getEnd());
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRightDiffSystem_equal(){
        final Range range2 = Range.of(Range.CoordinateSystem.RESIDUE_BASED,range.getBegin()+1,range.getEnd()+1);
        assertEquals(range,range2);
        assertEquals(range.hashCode(),range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRightDiffSystem_notEqual(){
        final Range range2 = Range.of(Range.CoordinateSystem.SPACE_BASED,range.getBegin()+1,range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRight_notEqual(){
        final Range range2 = Range.of(range.getBegin(),range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRightDiffSystem_equal(){
        final Range range2 = Range.of(Range.CoordinateSystem.SPACE_BASED,range.getBegin(),range.getEnd()+1);
        assertEquals(range,range2);
        assertEquals(range.hashCode(),range2.hashCode());
    }

    @Test public void testEquals_sameLeftDifferentRightDiffSystem_notEqual(){
        final Range range2 = Range.of(Range.CoordinateSystem.RESIDUE_BASED,range.getBegin(),range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }

    @Test public void testEquals_differentLeftDifferentRight_notEqual(){
        final Range range2 = Range.of(range.getBegin()+1,range.getEnd()+1);
        assertFalse(range.equals(range2));
        assertFalse(range.hashCode()==range2.hashCode());
    }


    @Test public void testConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.of(left,right);
        assertEquals(left,sut.getBegin());
        assertEquals(right, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLength(){
        int left = 10;
        int length = 10;

        Range sut = new Range.Builder(length).shift(left).build();
        assertEquals(left,sut.getBegin());
        assertEquals(left+length-1, sut.getEnd());
    }

    @Test
    public void testBuildRangeOfLength_emptyRange(){
        int left = 10;
        int length = 0;

        Range sut = new Range.Builder(length).shift(left).build();
        assertEquals(left,sut.getBegin());
        assertEquals(left+length-1, sut.getEnd());
    }


    
    @Test(expected=IllegalArgumentException.class)
    public void testBuildRangeOfLength_negativeRange(){
        int left = 0;
        int length =-1;

        new Range.Builder(length).shift(left).build();
    }

    

    @Test public void testZeroBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.of(Range.CoordinateSystem.ZERO_BASED,left,right);
        assertEquals(left,sut.getBegin(CoordinateSystem.ZERO_BASED));
        assertEquals(right, sut.getEnd(CoordinateSystem.ZERO_BASED));
        assertEquals(left,sut.getBegin());
        assertEquals(right, sut.getEnd());
    }

    @Test public void testSpaceBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.of(Range.CoordinateSystem.SPACE_BASED,left,right);
        assertEquals(left,sut.getBegin(CoordinateSystem.SPACE_BASED));
        assertEquals(right, sut.getEnd(CoordinateSystem.SPACE_BASED));
        assertEquals(left,sut.getBegin());
        assertEquals(right, sut.getEnd()+1);
    }

    @Test public void testResidueBaseConstructor(){
        int left = 10;
        int right =20;

        Range sut = Range.of(Range.CoordinateSystem.RESIDUE_BASED,left,right);
        assertEquals(left,sut.getBegin(CoordinateSystem.RESIDUE_BASED));
        assertEquals(right, sut.getEnd(CoordinateSystem.RESIDUE_BASED));
        assertEquals(left,sut.getBegin()+1);
        assertEquals(right, sut.getEnd()+1);
    }

    @Test public void testResidueBaseEmptyRangeConstruction(){
        int left = 1;
        int right =0;

        Range sut = Range.of(Range.CoordinateSystem.RESIDUE_BASED,left,right);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testSpaceBaseEmptyRangeConstruction(){
        int left = 0;
        int right =0;

        Range sut = Range.of(Range.CoordinateSystem.SPACE_BASED,left,right);
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
        assertEquals(sut.hashCode(),emptyRange.hashCode());
    }

    @Test public void testDefaultBuildEmptyRangeConstruction(){

        Range sut = new Range.Builder().build();
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    }

    @Test public void testDefaultCoordinateSpecificBuildEmptyRangeConstruction(){
        Range sut = new Range.Builder()
						.build();
        assertTrue(sut.isEmpty());
        assertEquals(sut,emptyRange);
    } 

   

    @Test(expected=IllegalArgumentException.class) public void testInvalidRangeConstruction(){
        int left = 0;
        int right =-1;

        Range.of(Range.CoordinateSystem.SPACE_BASED,left,right);
    }

    @Test(expected=IllegalArgumentException.class) public void testConstructor_leftGreaterThanRight_shouldThrowIllegalArgumentException(){
        Range.of(10,0);
    }

    

    @Test public void testzeroToSpaceCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.of(Range.CoordinateSystem.ZERO_BASED,rangeStart,rangeEnd);
        assertEquals(range.getBegin(CoordinateSystem.SPACE_BASED),rangeStart);
        assertEquals(range.getEnd(CoordinateSystem.SPACE_BASED),rangeEnd+1);
    }

    @Test public void testzeroToResidueCoordinateSystem(){
        long rangeStart = 5;
        long rangeEnd = 15;
        Range range = Range.of(Range.CoordinateSystem.ZERO_BASED,rangeStart,rangeEnd);
        assertEquals(range.getBegin(CoordinateSystem.RESIDUE_BASED),rangeStart+1);
        assertEquals(range.getEnd(CoordinateSystem.RESIDUE_BASED),rangeEnd+1);
    }

   

    @Test public void testSubRangeOf_nullRange_isNotSubRange(){
        assertFalse(range.isSubRangeOf(null));
    }

    @Test public void testSubRangeOf_leftIsSameRightIsLess_isSubRange(){
        Range subRange = Range.of(range.getBegin(),range.getEnd()-1);
        assertTrue(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsSameRightIsMore_isNotSubRange(){
        Range subRange = Range.of(range.getBegin(),range.getEnd()+1);
        assertFalse(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsLessRightIsMore_isNotSubRange(){
        Range notSubRange = Range.of(range.getBegin()-1,range.getEnd()+1);
        assertFalse(notSubRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsLessRightIsLess_isNotSubRange(){
        Range notSubRange = Range.of(range.getBegin()-1,range.getEnd()-1);
        assertFalse(notSubRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsLessRightIsSame_isNotSubRange(){
        Range notSubRange = Range.of(range.getBegin()-1,range.getEnd());
        assertFalse(notSubRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsMoreRightIsSame_isSubRange(){
        Range subRange = Range.of(range.getBegin()+1,range.getEnd());
        assertTrue(subRange.isSubRangeOf(range));
    }
    @Test public void testSubRangeOf_leftIsMoreRightIsLess_isSubRange(){
        Range subRange = Range.of(range.getBegin()+1,range.getEnd()-1);

        assertTrue(subRange.isSubRangeOf(range));
    }

    @Test public void testSubRangeOf_leftIsMoreRightIsMore_isNotSubRange(){
        Range subRange = Range.of(range.getBegin()+1,range.getEnd()+1);

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
        long expectedLength = range.getEnd()-range.getBegin()+1;
        long actualLength = range.getLength();
        assertEquals(expectedLength,actualLength);
    }

    @Test public void testSize_sameLeftAndRight_sizeIsOne(){
        Range oneRange = Range.of(5,5);
        assertEquals(1, oneRange.getLength());
    }

    @Test public void testSize_leftAndRightAreZero_sizeIsOne(){
        Range zeroRange = Range.of(0,0);
        assertEquals(1, zeroRange.getLength());
    }

    @Test public void testIntersects()
    {
    	
        Range target = Range.of(5, 15);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_doesntReallyIntersect()
    {
        Range target = Range.of(15,25);
        assertFalse(this.range.intersects(target));
    }

    @Test public void testIntersects_barelyIntersectsStart()
    {
        Range target = Range.of(-10, 1);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_barelyIntersectsEnd()
    {
        Range target = Range.of(10, 12);
        assertTrue(this.range.intersects(target));
    }

    @Test public void testIntersects_subRange()
    {
        Range target = Range.of(5, 7);
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
        Range target = Range.of(5,15);
        assertEquals(Range.of(5, 10), this.range.intersection(target));
    }

    @Test public void testIntersection_subrange()
    {
        Range target = Range.of(5,7);
        assertEquals(target, this.range.intersection(target));
    }

    @Test public void testIntersection_superrange()
    {
        Range target = Range.of(-4, 20);
        assertEquals(this.range, this.range.intersection(target));
    }

    @Test public void testIntersection_onePointIntersectStart()
    {
        Range target = Range.of(-4, 1);
        assertEquals(Range.of(1, 1), this.range.intersection(target));
    }

    @Test public void testIntersection_onePointIntersectEnd()
    {
        Range target = Range.of(10, 12);
        assertEquals(Range.of(10, 10), this.range.intersection(target));
    }

    @Test public void testIntersection_empty()
    {
        assertEquals(emptyRange, this.range.intersection(emptyRange));
    }

    @Test public void testIntersection_nointersection()
    {
        Range target = Range.of(15,25);
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
        Range target = Range.of(15,25);
        assertTrue(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_false()
    {
        Range target = Range.of(-5, 10);
        assertFalse(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_sameStart()
    {
        Range target = Range.of(1, 15);
        assertFalse(this.range.startsBefore(target));
    }

    @Test public void testStartsBefore_sameRange()
    {
        assertFalse(this.range.startsBefore(this.range));
    }

    @Test(expected = NullPointerException.class)
    public void testStartsBefore_null()
    {
       this.range.startsBefore(null);
    }

    @Test public void testEndsBefore()
    {
        Range target = Range.of(12,20);
        assertTrue(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_false()
    {
        Range target = Range.of(-5, 8);
        assertFalse(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_sameEnd()
    {
        Range target = Range.of(5, 10);
        assertFalse(this.range.endsBefore(target));
    }

    @Test public void testEndsBefore_sameRange()
    {
        assertFalse(this.range.endsBefore(this.range));
    }

    @Test(expected = NullPointerException.class)
    public void testEndsBefore_null()
    {
      this.range.endsBefore(null);           
    }

   

    @Test public void testToString()
    {
        assertEquals("[ 1 - 10 ]/0B", this.range.toString());
    }
    @Test public void testToStringResidueBasedCoordinate()
    {
        assertEquals("[ 2 - 11 ]/RB", this.range.toString(CoordinateSystem.RESIDUE_BASED));
    }
    @Test public void testToStringSpacedBasedCoordinate()
    {
        assertEquals("[ 1 - 11 ]/SB", this.range.toString(CoordinateSystem.SPACE_BASED));
    }
    @Test public void testToStringZeroBased()
    {
        assertEquals("[ 1 - 10 ]/0B", this.range.toString(CoordinateSystem.ZERO_BASED));
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
        final Range expected = Range.of(start, end);
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
        assertEquals(range, Range.of(range.getBegin(), range.getEnd()));
    }
    @Test
    public void buildRangeWithCoordinateSystem(){
        assertEquals(range, Range.of(CoordinateSystem.RESIDUE_BASED,range.getBegin()+1, range.getEnd()+1));
    }
    @Test(expected = NullPointerException.class)
    public void buildRangeWithNullCoordinateSystemShouldThrowNPE(){
        Range.of(null,range.getBegin()+1, range.getEnd()+1);
    }

    @Test
    public void buildEmptyRange(){
        Range emptyRange = Range.of(10, 9);
        assertEquals(10, emptyRange.getBegin());
        assertEquals(9, emptyRange.getEnd());
        assertTrue(emptyRange.isEmpty());
    }
    
    @Test
    public void buildInclusiveRangesEmptyListShouldReturnEmptyRange(){
        Range emptyRange = Ranges.createInclusiveRange(Collections.<Range>emptyList());
        assertEquals(0, emptyRange.getBegin());
        assertEquals(-1, emptyRange.getEnd());
        assertTrue(emptyRange.isEmpty());
    }
    
    @Test
    public void buildInclusiveRange(){
        List<Range> ranges = Arrays.asList(
                    Range.of(10, 20),
                    Range.of( 50, 100),
                    Range.of( -5, 3)
                    );
        Range expected = Range.of(-5,100);
        assertEquals(expected, Ranges.createInclusiveRange(ranges));
    }
    
    @Test
    public void shiftRight(){
        int units = 5;
        Range shifted = new Range.Builder(range)
							.shift(units)
							.build();
        assertEquals(range.getBegin()+units, shifted.getBegin());
        assertEquals(range.getEnd()+units, shifted.getEnd());
        assertEquals(range.getLength(), shifted.getLength());
        
    }
    @Test
    public void shiftLeft(){
        int units = 5;
        Range shifted = new Range.Builder(range)
							.shift(-units)
							.build();
        assertEquals(range.getBegin()-units, shifted.getBegin());
        assertEquals(range.getEnd()-units, shifted.getEnd());
        assertEquals(range.getLength(), shifted.getLength());
        
    }

    @Test
    public void mergeEmpty(){
        assertTrue(Ranges.merge(Collections.<Range>emptyList()).isEmpty());
    }
    
    @Test
    public void mergeOneRange(){
        
        final List<Range> oneRange = Arrays.asList(range);
        assertEquals(
                oneRange,
                Ranges.merge(oneRange));
    }
    
    @Test
    public void mergeTwoRangesNoOverlapShouldReturnTwoRanges(){
        Range nonOverlappingRange = Range.of(12, 20);
        List<Range> nonOverlappingRanges = Arrays.asList(range,nonOverlappingRange);
        assertEquals(
                nonOverlappingRanges,
                Ranges.merge(nonOverlappingRanges));
        
    }
    
    @Test
    public void mergeTwoRanges(){
        Range overlappingRange = Range.of(5, 20);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange);
        List<Range> expectedRanges = Arrays.asList(Range.of(range.getBegin(), overlappingRange.getEnd()));
        assertEquals(
                expectedRanges,
                Ranges.merge(overlappingRanges));
        
    }
    @Test
    public void mergeTwoAdjacentButNotOverlappingRangesShouldMergeIntoOne(){
        Range adjacentRange = Range.of(11, 20);
        List<Range> rangesToMerge = Arrays.asList(range,adjacentRange);
        List<Range> expectedRanges = Arrays.asList(
                Range.of(range.getBegin(), adjacentRange.getEnd()));
        assertEquals(
                expectedRanges,
                Ranges.merge(rangesToMerge));
        
    }
    @Test
    public void mergeThreeRanges(){
        Range overlappingRange_1 = Range.of(5, 20);
        Range overlappingRange_2 = Range.of(15, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange_1,overlappingRange_2);
        List<Range> expectedRanges = Arrays.asList(Range.of(range.getBegin(), overlappingRange_2.getEnd()));
        assertEquals(
                expectedRanges,
                Ranges.merge(overlappingRanges));
        
    }
    @Test
    public void mergeThreeRangesThirdRangeConnectsTwoRangeIslands(){
        Range overlappingRange_2 = Range.of(5, 20);
        Range overlappingRange_1 = Range.of(15, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange_1,overlappingRange_2);
        List<Range> expectedRanges = Arrays.asList(Range.of(range.getBegin(), overlappingRange_1.getEnd()));
        assertEquals(
                expectedRanges,
                Ranges.merge(overlappingRanges));
        
    }
    @Test
    public void mergeThreeRangesOnlyTwoMerge(){
        Range overlappingRange = Range.of(5, 20);
        Range nonOverlappingRange = Range.of(22, 30);
        List<Range> overlappingRanges = Arrays.asList(range,overlappingRange,nonOverlappingRange);
        List<Range> expectedRanges = Arrays.asList(Range.of(range.getBegin(), overlappingRange.getEnd()),nonOverlappingRange);
        assertEquals(
                expectedRanges,
                Ranges.merge(overlappingRanges));
        
    }
    @Test
    public void mergeThreeConsecutiveRanges(){
        List<Range> consecutiveRanges = Arrays.asList(range,range,range);
        List<Range> expectedRanges = Arrays.asList(range);
        assertEquals(
                expectedRanges,
                Ranges.merge(consecutiveRanges));
    }
    private Range createRangeSeparatedFrom(Range range, int distance){
        return new Range.Builder(range.getLength())
        		.shift(range.getEnd()+ distance).build();
    }
    @Test
    public void mergeRightClusteredRanges(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.of(range.getBegin(), clusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Ranges.merge(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeClusteredRangesAbutmentShouldStillMerge(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,clusterDistance+1);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.of(range.getBegin(), clusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Ranges.merge(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeClusteredRangesBeyondClusterShouldNotMerge(){
        int clusterDistance=30;
        Range unclusterableRange = createRangeSeparatedFrom(range,clusterDistance+2);
        List<Range> clusteredRanges = Arrays.asList(range, unclusterableRange);
        assertEquals(
                clusteredRanges,
                Ranges.merge(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeLeftClusteredRanges(){
        int clusterDistance=30;
        Range clusterableRange = createRangeSeparatedFrom(range,-clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, clusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.of(clusterableRange.getBegin(), range.getEnd()));
        assertEquals(
                expectedRange,
                Ranges.merge(clusteredRanges,clusterDistance));
    }
    @Test
    public void mergeThreeClusteredRanges(){
        int clusterDistance=30;
        Range leftClusterableRange = createRangeSeparatedFrom(range,-clusterDistance);
        Range rightClusterableRange = createRangeSeparatedFrom(range,clusterDistance);
        List<Range> clusteredRanges = Arrays.asList(range, leftClusterableRange,rightClusterableRange);
        List<Range> expectedRange = Arrays.asList(Range.of(leftClusterableRange.getBegin(), rightClusterableRange.getEnd()));
        assertEquals(
                expectedRange,
                Ranges.merge(clusteredRanges,clusterDistance));
    }
    
    @Test
    public void mergeWithNegativeClusterDistanceShouldThrowIllegalArgumentException(){
        try{
            Ranges.merge(Arrays.asList(range), -1);
            fail("should catch illegal argumentException when cluster distance is -1");
        }
        catch(IllegalArgumentException e){
            assertEquals("cluster distance can not be negative",e.getMessage());
        }
    }
    
    @Test
    public void growRight(){
        Range expected = Range.of(1, 15);
        assertEquals(expected, new Range.Builder(range)
							.growEnd(5)
							.build());
    }
    @Test
    public void growLeft(){
        Range expected = Range.of(-4, 10);
        
        assertEquals(expected, 
        		new Range.Builder(range)
        			.growBegin(5)
        			.build());
    }
    @Test
    public void grow(){
        Range expected = Range.of(-4, 15);
        assertEquals(expected, new Range.Builder(range)
								.growBegin(5)
								.growEnd(5)
								.build());
    }
    
    @Test
    public void shrinkLeft(){
        Range expected = Range.of(6, 10);
        assertEquals(expected, new Range.Builder(range)
									.shrinkBegin(5)
									.build());
    }
    @Test
    public void shrinkRight(){
        Range expected = Range.of(1, 5);
        assertEquals(expected, new Range.Builder(range)
		.shrinkEnd(5)
		.build());
    }
    @Test
    public void shrink(){
        Range expected = Range.of(6, 5);
        assertEquals(expected, new Range.Builder(range)
								.shrinkBegin(5)
								.shrinkEnd(5)
								.build());
    }
  
    
    @Test
    public void iterator(){
        Iterator<Long> iter = range.iterator();
        assertTrue(iter.hasNext());
        for(long l = range.getBegin(); l<= range.getEnd(); l++){
            assertEquals(Long.valueOf(l), iter.next());
        }
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void complementNoIntersectionShouldReturnOriginalRange(){
        Range noOverlapRange = new Range.Builder(range).shift(1000).build();
        assertEquals(Arrays.asList(range),range.complement(noOverlapRange));
    }
    
    @Test
    public void complementOfSubRangeShouldReturn2DisjointRanges(){
        Range subrange = new Range.Builder(range)
        					.shrinkBegin(2)
        					.shrinkEnd(2)
        					.build();
        assertEquals(Arrays.asList(Range.of(range.getBegin(),2), Range.of(range.getEnd()-1, range.getEnd())),
                range.complement(subrange));
    }
    
    @Test
    public void complementOfSuperRangeShouldReturnEmptyList(){
        Range superRange = new Range.Builder(range)
        					.growBegin(2)
        					.growEnd(2)
        					.build();
        assertEquals(Collections.emptyList(), range.complement(superRange));
    }
    @Test
    public void complementOfLeftSideShouldReturnArrayOfOneElementContainingRightSide(){
        Range left = new Range.Builder(range)
        				.shrinkEnd(2)
        				.build();
        assertEquals(Arrays.asList(Range.of(range.getEnd()-1, range.getEnd())),
                range.complement(left));
    }
    
    @Test
    public void complementOfRightSideShouldReturnArrayOfOneElementContainingLeftSide(){
        Range right = new Range.Builder(range)
        				.shrinkBegin(2)
        				.build();
        assertEquals(Arrays.asList(Range.of(range.getBegin(),2)),
                range.complement(right));
    }
    
    @Test
    public void splitUnderMaxSplitLengthShouldReturnListContainingSameRange(){
        assertEquals(Arrays.asList(range), range.split(range.getLength()+1));
    }
    @Test
    public void splitInto2Ranges(){
        List<Range> expected = Arrays.asList(
                new Range.Builder(range.getLength()/2).shift(range.getBegin()).build() ,
                Range.of(range.getLength()/2+1, range.getEnd())
        );
        
        assertEquals(expected, range.split(range.getLength()/2));
    }
    @Test
    public void splitInto4Ranges(){
        //range is [1-10]
        List<Range> expected = Arrays.asList(
                Range.of(1,3),
                Range.of(4,6),
                Range.of(7,9),
                Range.of(10,10)
        );
        
        assertEquals(expected, range.split(3));
    }
    
    @Test
    public void mergeIntoClustersEmptyListShouldReturnEmptyList(){
        assertTrue(
                Ranges.mergeIntoClusters(Collections.<Range>emptyList(), 100)
                .isEmpty());
    }
    
    @Test
    public void mergeIntoClustersOneRangeShouldReturnSameRange(){
        final List<Range> inputList = Arrays.asList(range);
        assertEquals(inputList, Ranges.mergeIntoClusters(inputList, 100));
    }
    @Test
    public void mergeIntoClusters2RangesFartherAwayThanMaxClusterDistanceSame2Ranges(){
        int maxClusterDistance=100;
        Range farAwayRange = new Range.Builder(range).shift(maxClusterDistance+1).build();
        final List<Range> inputList = Arrays.asList(range,farAwayRange);
        assertEquals(inputList, Ranges.mergeIntoClusters(inputList, maxClusterDistance));
    }
    @Test
    public void mergeIntoClusters2OverLappingRanges(){
        int maxClusterDistance=100;
        Range overlappingRange = new Range.Builder(range).shift(5).build();
        final List<Range> inputList = Arrays.asList(range,overlappingRange);
        final List<Range> expectedList = Arrays.asList(Range.of(range.getBegin(), overlappingRange.getEnd()));
        assertEquals(expectedList, Ranges.mergeIntoClusters(inputList, maxClusterDistance));
    }
    @Test
    public void mergeIntoClusters3OverLappingRanges(){
        int maxClusterDistance=100;
        Range overlappingRange = new Range.Builder(range).shift(5).build();
        Range overlappingRange2 = new Range.Builder(overlappingRange).shift(10).build();
        final List<Range> inputList = Arrays.asList(range,overlappingRange,overlappingRange2);
        final List<Range> expectedList = Arrays.asList(Range.of(range.getBegin(), overlappingRange2.getEnd()));
        assertEquals(expectedList, Ranges.mergeIntoClusters(inputList, maxClusterDistance));
    }
    
    @Test
    public void mergeIntoClustersWhenRangeIsLongerThanClusterDistanceShouldSplit(){
        int maxClusterDistance=100;
        Range range = Range.of(0,10);
        //range [10,110] -> [10, 109][110-110]
        Range hugeRange = Range.of(10,110);
       
        final List<Range> inputList = Arrays.asList(range,hugeRange);
        final List<Range> expectedList = Arrays.asList(
                Range.of(0,99),
                Range.of(100,110)
            );
        assertEquals(expectedList, Ranges.mergeIntoClusters(inputList, maxClusterDistance));
    }
    
    @Test
    public void mergeIntoClustersReSplitHugeRangeToMakeMoreEfficentClusters(){
        int maxClusterDistance=100;
        Range range = Range.of(0,10);
        Range hugeRange = Range.of(10,110);
        Range range2 = Range.of(10,20);
        Range range3 = Range.of(108,120);
        final List<Range> inputList = Arrays.asList(range,range2,range3,hugeRange);
        final List<Range> expectedList = Arrays.asList(
                Range.of(0,99),
                Range.of(100,120)
            );
        assertEquals(expectedList, Ranges.mergeIntoClusters(inputList, maxClusterDistance));
    }
   
    @Test
    public void byteRangeWithByteLength(){
    	Range r = Range.of(12,123);
    	assertEquals(12, r.getBegin());
    	assertEquals(123, r.getEnd());
    	assertEquals(112, r.getLength());
    }
    @Test
    public void byteRangeWithUnsignedByteLength(){
    	Range r = Range.of(12,223);
    	assertEquals(12, r.getBegin());
    	assertEquals(223, r.getEnd());
    	assertEquals(212, r.getLength());
    }
    @Test
    public void byteRangeWithShortLength(){
    	Range r = Range.of(0,499);
    	assertEquals(0, r.getBegin());
    	assertEquals(499, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    @Test
    public void byteRangeWithUnsignedShortLength(){
    	Range r = Range.of(0,59999);
    	assertEquals(0, r.getBegin());
    	assertEquals(59999, r.getEnd());
    	assertEquals(60000, r.getLength());
    }
    
    @Test
    public void byteRangeWithIntLength(){
    	Range r = Range.of(0,99999);
    	assertEquals(0, r.getBegin());
    	assertEquals(99999, r.getEnd());
    	assertEquals(100000, r.getLength());
    }
    @Test
    public void byteRangeWithUnsignedIntLength(){
    	long end = Integer.MAX_VALUE+1L;
    	Range r = Range.of(0,end);
    	assertEquals(0, r.getBegin());
    	assertEquals(end, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+2L, r.getLength());
    }
    @Test
    public void byteRangeWithLongLength(){
    	long end = Long.MAX_VALUE-1L;
    	Range r = Range.of(0,end);
    	assertEquals(0, r.getBegin());
    	assertEquals(end, r.getEnd());
    	assertEquals(Long.MAX_VALUE, r.getLength());
    }
    
    @Test
    public void unsignedByteWithShortLength(){
    	Range r = new Range.Builder(500).shift(Byte.MAX_VALUE+1).build();
    	assertEquals(Byte.MAX_VALUE+1, r.getBegin());
    	assertEquals(Byte.MAX_VALUE+500, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void unsignedByteWithUnsignedShortLength(){
    	Range r = new Range.Builder(Short.MAX_VALUE+1).shift(Byte.MAX_VALUE+1).build();
    	assertEquals(Byte.MAX_VALUE+1, r.getBegin());
    	assertEquals(Byte.MAX_VALUE+Short.MAX_VALUE+1, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void unsignedByteWithIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE).shift(Byte.MAX_VALUE+1).build();
    	assertEquals(Byte.MAX_VALUE+1, r.getBegin());
    	assertEquals(Byte.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void unsignedByteWithUnsignedIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE+1L).shift(Byte.MAX_VALUE+1).build();
    	assertEquals(Byte.MAX_VALUE+1, r.getBegin());
    	assertEquals(Byte.MAX_VALUE+(long)Integer.MAX_VALUE+1L, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void unsignedByteWithLongLength(){
    	Range r = new Range.Builder(0x100000000L).shift(Byte.MAX_VALUE+1).build();
    	assertEquals(Byte.MAX_VALUE+1, r.getBegin());
    	assertEquals(4294967423L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    //////////////////////////
    @Test
    public void shortWithShortLength(){
    	Range r = new Range.Builder(500).shift(Short.MAX_VALUE).build();
    	assertEquals(Short.MAX_VALUE, r.getBegin());
    	assertEquals(Short.MAX_VALUE+499, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void shortWithUnsignedShortLength(){
    	Range r = new Range.Builder(Short.MAX_VALUE+1).shift(Short.MAX_VALUE).build();
    	assertEquals(Short.MAX_VALUE, r.getBegin());
    	assertEquals(Short.MAX_VALUE+Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void shortWithIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE).shift(Short.MAX_VALUE).build();
    	assertEquals(Short.MAX_VALUE, r.getBegin());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE-1, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void shortWithUnsignedIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE+1L).shift(Short.MAX_VALUE).build();
    	assertEquals(Short.MAX_VALUE, r.getBegin());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void shortWithLongLength(){
    	Range r = new Range.Builder(0x100000000L).shift(Short.MAX_VALUE).build();
    	assertEquals(Short.MAX_VALUE, r.getBegin());
    	assertEquals(4295000062L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    /////////////////////////////////
    @Test
    public void intWithShortLength(){
    	Range r = new Range.Builder(500).shift(Integer.MAX_VALUE).build();
    	assertEquals(Integer.MAX_VALUE, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+499L, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void intWithUnsignedShortLength(){
    	Range r = new Range.Builder(Short.MAX_VALUE+1).shift(Integer.MAX_VALUE).build();
    	assertEquals(Integer.MAX_VALUE, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+(long)Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void intWithIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE).shift(Integer.MAX_VALUE).build();
    	assertEquals(Integer.MAX_VALUE, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE-1, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void intWithUnsignedIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE+1L).shift(Integer.MAX_VALUE).build();
    	assertEquals(Integer.MAX_VALUE, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void intWithLongLength(){
    	Range r = new Range.Builder(0x100000000L).shift(Integer.MAX_VALUE).build();
    	assertEquals(Integer.MAX_VALUE, r.getBegin());
    	assertEquals(6442450942L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    ////////////////////////////////
    @Test
    public void unsignedShortWithShortLength(){
    	Range r = new Range.Builder(500).shift(Short.MAX_VALUE+1).build();
    	assertEquals(Short.MAX_VALUE+1, r.getBegin());
    	assertEquals(Short.MAX_VALUE+1+499, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void unsignedShortWithUnsignedShortLength(){
    	Range r = new Range.Builder(Short.MAX_VALUE+1).shift(Short.MAX_VALUE+1).build();
    	assertEquals(Short.MAX_VALUE+1, r.getBegin());
    	assertEquals(Short.MAX_VALUE+1+Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void unsignedShortWithIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE).shift(Short.MAX_VALUE+1).build();
    	assertEquals(Short.MAX_VALUE+1, r.getBegin());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void unsignedShortWithUnsignedIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE+1L).shift(Short.MAX_VALUE+1).build();
    	assertEquals(Short.MAX_VALUE+1, r.getBegin());
    	assertEquals(Short.MAX_VALUE+(long)Integer.MAX_VALUE+1, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }
    @Test
    public void unsignedShortWithLongLength(){
    	Range r = new Range.Builder(0x100000000L).shift(Short.MAX_VALUE+1).build();
    	assertEquals(Short.MAX_VALUE+1, r.getBegin());
    	assertEquals(4295000063L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    ///////////////////////
    @Test
    public void unsignedIntWithShortLength(){
    	Range r = new Range.Builder(500).shift(Integer.MAX_VALUE+1L).build();
    	assertEquals(Integer.MAX_VALUE+1L, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+1L+499L, r.getEnd());
    	assertEquals(500, r.getLength());
    }
    
    @Test
    public void unsignedIntWithUnsignedShortLength(){
    	Range r = new Range.Builder(Short.MAX_VALUE+1).shift(Integer.MAX_VALUE+1L).build();
    	assertEquals(Integer.MAX_VALUE+1L, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+1L+(long)Short.MAX_VALUE, r.getEnd());
    	assertEquals(Short.MAX_VALUE+1, r.getLength());
    }
    
    @Test
    public void unsignedIntWithIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE).shift(Integer.MAX_VALUE+1L).build();
    	assertEquals(Integer.MAX_VALUE+1L, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE, r.getEnd());
    	assertEquals(Integer.MAX_VALUE, r.getLength());
    }
    @Test
    public void unsignedIntWithUnsignedIntLength(){
    	Range r = new Range.Builder(Integer.MAX_VALUE+1L).shift(Integer.MAX_VALUE+1L).build();
    	assertEquals(Integer.MAX_VALUE+1L, r.getBegin());
    	assertEquals(Integer.MAX_VALUE+(long)Integer.MAX_VALUE+1L, r.getEnd());
    	assertEquals(Integer.MAX_VALUE+1L, r.getLength());
    }   
    @Test
    public void unsignedIntWithLongLength(){
    	Range r = new Range.Builder(0x100000000L).shift(Integer.MAX_VALUE+1L).build();
    	assertEquals(Integer.MAX_VALUE+1L, r.getBegin());
    	assertEquals(6442450943L, r.getEnd());
    	assertEquals(4294967296L, r.getLength());
    }
    
    /////////////////////////////////
    
	@Test
	public void longWithShortLength() {
		Range r = new Range.Builder(500).shift(0x100000000L).build();
		assertEquals(0x100000000L, r.getBegin());
		assertEquals(4294967795L, r.getEnd());
		assertEquals(500, r.getLength());
	}

	@Test
	public void longWithUnsignedShortLength() {
		Range r = new Range.Builder(Short.MAX_VALUE + 1).shift(0x100000000L).build();
		assertEquals(0x100000000L, r.getBegin());
		assertEquals(4295000063L, r.getEnd());
		assertEquals(Short.MAX_VALUE + 1, r.getLength());
	}

	@Test
	public void longWithIntLength() {
		Range r = new Range.Builder(Integer.MAX_VALUE)
					.shift(0x100000000L)
					.build();
		assertEquals(0x100000000L, r.getBegin());
		assertEquals(6442450942L, r.getEnd());
		assertEquals(Integer.MAX_VALUE, r.getLength());
	}

	@Test
	public void longWithUnsignedIntLength() {
		Range r =new Range.Builder(Integer.MAX_VALUE + 1L)
						.shift(0x100000000L)
						.build();
		assertEquals(0x100000000L, r.getBegin());
		assertEquals(6442450943L, r.getEnd());
		assertEquals(Integer.MAX_VALUE + 1L, r.getLength());
	}

    @Test
    public void emptyRangeWithNegativeCoordinate(){
    	Range r = new Range.Builder()
					.shift(-1)
					.build();
    	assertEquals(-1, r.getBegin());
    	assertTrue(r.isEmpty());
    	assertEquals(-2, r.getEnd());
    }
    @Test
    public void emptyRangeWithNegativeShortValueCoordinate(){
    	Range r = new Range.Builder()
		.shift(Short.MIN_VALUE)
		.build();
    	assertEquals(Short.MIN_VALUE, r.getBegin());
    	assertTrue(r.isEmpty());
    	assertEquals(Short.MIN_VALUE -1, r.getEnd());
    }
    @Test
    public void emptyRangeWithNegativeIntValueCoordinate(){
    	Range r = new Range.Builder()
					.shift(Integer.MIN_VALUE)
					.build();
    	assertEquals(Integer.MIN_VALUE, r.getBegin());
    	assertTrue(r.isEmpty());
    	assertEquals(Integer.MIN_VALUE -1L, r.getEnd());
    }
    @Test
    public void emptyRangeWithShortValueCoordinate(){
    	Range r = new Range.Builder()
					.shift(Short.MAX_VALUE)
					.build();
    	assertEquals(Short.MAX_VALUE, r.getBegin());
    	assertTrue(r.isEmpty());
    	assertEquals(Short.MAX_VALUE -1, r.getEnd());
    }
    @Test
    public void emptyRangeWithIntValueCoordinate(){
    	
    	Range r = new Range.Builder()
							.shift(Integer.MAX_VALUE)
							.build();
    	assertEquals(Integer.MAX_VALUE, r.getBegin());
    	assertTrue(r.isEmpty());
    	assertEquals(Integer.MAX_VALUE -1L, r.getEnd());
    }
    @Test
    public void emptyRangeWithLongValueCoordinate(){
    	Range r = new Range.Builder()
						.shift(Long.MAX_VALUE)
						.build();
    	assertEquals(Long.MAX_VALUE, r.getBegin());
    	assertTrue(r.isEmpty());
    	assertEquals(Long.MAX_VALUE -1L, r.getEnd());
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void createRangeWithLengthThatIsTooBigShouldThrowException(){
    	new Range.Builder(400).shift(Long.MAX_VALUE).build();
    }
    @Test(expected = IllegalArgumentException.class)
    public void createRangeWithNegativeLengthShouldThrowException(){
    	new Range.Builder(20, -40).build();
    }
    
    @Test
    public void longRange(){
    	Range r = Range.of(Long.MIN_VALUE, Integer.MIN_VALUE);
    	assertEquals(Long.MIN_VALUE, r.getBegin());
    	assertEquals(Integer.MIN_VALUE, r.getEnd());
    	assertEquals(
    			BigInteger.valueOf(Integer.MIN_VALUE).subtract(BigInteger.valueOf(Long.MIN_VALUE)).longValue()+1L, 
    			r.getLength());
    }
    @Test
    public void intRange(){
    	Range r = Range.of(Integer.MIN_VALUE, 0);
    	assertEquals(Integer.MIN_VALUE, r.getBegin());
    	assertEquals(0, r.getEnd());
    	assertEquals(Math.abs((long)Integer.MIN_VALUE)+1L, r.getLength());
    }
}
