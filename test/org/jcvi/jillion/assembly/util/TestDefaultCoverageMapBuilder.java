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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestDefaultCoverageMapBuilder {

    Range seq_0_9 = Range.of(0, 9);
    Range seq_0_12 =Range.of(0, 12);
    Range seq_5_14 =Range.of(5, 14);
    Range seq_5_empty = new Range.Builder(0).shift(5).build();
    Range seq_8_12 =Range.of(8, 12);
    
    Range seq_10_12 =Range.of(10, 12);
    Range seq_11_15 =Range.of(11, 15);
    Range seq_11_20 =Range.of(11, 20);
    Range seq_11_18 =Range.of(11, 18);
    
    Range seq_12_18 =Range.of(12, 18);
    
    Range seq_16_20 =Range.of(16, 20);
    
    
    
    
   
    
    private DefaultCoverageRegion<Range> createCoverageRegion(int start, int end, Range... elements){
        return new DefaultCoverageRegion.Builder<Range>(start, Arrays.asList(elements),null).end(end).build();
    }
    @Test
    public void emptyListShouldReturnEmptyCoverageMap(){
        CoverageMap<Range> map =new CoverageMapBuilder<Range>(Collections.<Range>emptyList()).build();
        assertEquals(0, map.getNumberOfRegions());
        assertEquals(-1,getLastCoveredOffsetIn(map));
        assertTrue(map.isEmpty());
        assertEquals(0D, map.getAverageCoverage(), 0);
        assertEquals(0, map.getMinCoverage());
        assertEquals(0, map.getMaxCoverage());
    }
    @Test
    public void sizeOf1(){
        Range read = Range.of(0, 0);
        CoverageMap<Range> map =new CoverageMapBuilder<Range>(Arrays.asList(read)).build();
        assertEquals(1, map.getNumberOfRegions());
        assertEquals(0, getLastCoveredOffsetIn(map));
        assertEquals(1D, map.getAverageCoverage(), 0);
        assertEquals(1, map.getMinCoverage());
        assertEquals(1, map.getMaxCoverage());
    }
    @Test
    public void ignoreSequenceOfZeroSize(){

        CoverageMap<Range> map =new CoverageMapBuilder<Range>(Arrays.asList(seq_5_empty)).build();
        assertEquals(0, map.getNumberOfRegions());
        assertEquals(-1,getLastCoveredOffsetIn(map));
        assertTrue(map.isEmpty());
        assertEquals(0D, map.getAverageCoverage(), 0);
        assertEquals(0, map.getMinCoverage());
        assertEquals(0, map.getMaxCoverage());

    }
    @Test
    public void oneSequenceShouldHaveOneCoverageRegion(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9)).build();
        assertEquals(1, map.getNumberOfRegions());
        CoverageRegion<Range> expectedRegion = createCoverageRegion(0,9,seq_0_9 );
        assertEquals(expectedRegion,map.getRegion(0));
        assertEquals(9, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        assertEquals(1D, map.getAverageCoverage(), 0);
        assertEquals(1, map.getMinCoverage());
        assertEquals(1, map.getMaxCoverage());
    }
    
    @Test
    public void twoTiledSequencesShouldHave3CoverageRegions(){

        CoverageMap<Range> map =new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14)).build();
        assertEquals(3, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,4,seq_0_9 ), map.getRegion(0));
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_5_14 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,14,seq_5_14 ), map.getRegion(2));
        assertEquals(14, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        
        assertEquals("avg cov", 20D/15, map.getAverageCoverage(), 0.001D);
        assertEquals("min cov", 1, map.getMinCoverage());
        assertEquals("max cov", 2, map.getMaxCoverage());
    }
    
    @Test
    public void hasleavingSequence(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12)).build();
        assertEquals(4, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,4,seq_0_9,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_0_12,seq_5_14 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,12,seq_0_12,seq_5_14 ), map.getRegion(2));
        assertEquals(createCoverageRegion(13,14,seq_5_14 ), map.getRegion(3));
        assertEquals(14, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        
        assertEquals("avg cov", 33D/15, map.getAverageCoverage(), 0.001D);
        assertEquals("min cov", 1, map.getMinCoverage());
        assertEquals("max cov", 3, map.getMaxCoverage());
    }
    
    @Test
    public void abutment(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_10_12)).build();
        assertEquals(2, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,9,seq_0_9 ), map.getRegion(0));
        assertEquals(createCoverageRegion(10,12,seq_10_12 ), map.getRegion(1));
        assertEquals(12, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        
        assertEquals("avg cov", 1D, map.getAverageCoverage(), 0.001D);
        assertEquals("min cov", 1, map.getMinCoverage());
        assertEquals("max cov", 1, map.getMaxCoverage());
    }
    
    @Test
    public void zeroCoverageRegion(){

        CoverageMap<Range> map =  new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12,seq_10_12,seq_16_20)).build();
        assertEquals(6, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,4,seq_0_9,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_0_12,seq_5_14 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,12,seq_0_12,seq_5_14,seq_10_12 ), map.getRegion(2));
        assertEquals(createCoverageRegion(13,14,seq_5_14 ), map.getRegion(3));
        //zero coverage region
        assertEquals(createCoverageRegion(15,15 ), map.getRegion(4));
        assertEquals(createCoverageRegion(16,20,seq_16_20 ), map.getRegion(5));
        assertEquals(20, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        
        assertEquals("avg cov", 41D/21, map.getAverageCoverage(), 0.001D);
        assertEquals("min cov", 0, map.getMinCoverage());
        assertEquals("max cov", 3, map.getMaxCoverage());
    }
    
    @Test
    public void threeConsecutiveReadsEndAtSamePoint(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_12,seq_8_12,seq_10_12,seq_11_15)).build();
        assertEquals(5, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,7,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(8,9,seq_0_12,seq_8_12 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,10,seq_0_12,seq_8_12, seq_10_12), map.getRegion(2));
        assertEquals(createCoverageRegion(11,12,seq_0_12, seq_8_12,seq_10_12,seq_11_15), map.getRegion(3));
        assertEquals(createCoverageRegion(13,15,seq_11_15), map.getRegion(4));
        assertEquals(15, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        
        assertEquals("avg cov", 26D/16, map.getAverageCoverage(), 0.001D);
        assertEquals("min cov", 1, map.getMinCoverage());
        assertEquals("max cov", 4, map.getMaxCoverage());
    }
    
    @Test
    public void enteringSequenceStartsAtPreviousRegionsEnd(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_12,seq_8_12,seq_10_12,seq_11_15,seq_12_18)).build();
        assertEquals(7, map.getNumberOfRegions());
       
        assertEquals(createCoverageRegion(0,7,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(8,9,seq_0_12,seq_8_12 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,10,seq_0_12,seq_8_12, seq_10_12), map.getRegion(2));       
        assertEquals(createCoverageRegion(11,11,seq_0_12, seq_8_12,seq_10_12,seq_11_15), map.getRegion(3));        
        assertEquals(createCoverageRegion(12,12,seq_0_12, seq_8_12,seq_10_12,seq_11_15,seq_12_18), map.getRegion(4));
        assertEquals(createCoverageRegion(13,15,seq_11_15,seq_12_18), map.getRegion(5));
        assertEquals(createCoverageRegion(16,18,seq_12_18), map.getRegion(6));
        
        assertEquals(18, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        
        assertEquals("avg cov", 33D/19, map.getAverageCoverage(), 0.001D);
        assertEquals("min cov", 1, map.getMinCoverage());
        assertEquals("max cov", 5, map.getMaxCoverage());
    }
    
    @Test
    public void firstElementDoesNotCover0(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_5_14,seq_11_15)).build();        
       
        assertEquals(3, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(5,10,seq_5_14 ), map.getRegion(0));
        assertEquals(createCoverageRegion(11,14,seq_5_14,seq_11_15 ), map.getRegion(1));
        assertEquals(createCoverageRegion(15,15,seq_11_15 ), map.getRegion(2));
	
      
    }
    @Test
    public void forceStartAt0ShouldMakeFirstElement0x(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_5_14,seq_11_15))
                .includeOrigin(true)
                .build();        
       
        assertEquals(4, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,4 ), map.getRegion(0));
        assertEquals(createCoverageRegion(5,10,seq_5_14 ), map.getRegion(1));
        assertEquals(createCoverageRegion(11,14,seq_5_14,seq_11_15 ), map.getRegion(2));
        assertEquals(createCoverageRegion(15,15,seq_11_15 ), map.getRegion(3));
	
      
    }
    @Test
    public void forceStartAt0WithNegativeRangesButCovers0ShouldNotBeChanged(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(Range.of(-10,-3),Range.of(4,8)))
                .includeOrigin(true)
                .build();        
       
        assertEquals(3, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(-10,-3, Range.of(-10,-3)), map.getRegion(0));
        assertEquals(createCoverageRegion(-2,3 ), map.getRegion(1));        
        assertEquals(createCoverageRegion(4,8,Range.of(4,8) ), map.getRegion(2));	
      
    }
    @Test
    public void forceStartAt0WithNegativeRangesDoesNotCover0ShouldGetExtra0x(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(Range.of(-10,-3)))
                .includeOrigin(true)
                .build();        
       
        assertEquals(2, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(-10,-3, Range.of(-10,-3)), map.getRegion(0));
        assertEquals(createCoverageRegion(-2,0 ), map.getRegion(1));        
      
    }
    
    @Test
    public void threeConsecutiveReadsStartAtSamePoint(){

        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_12,seq_11_15,seq_11_18,seq_11_20)).build();
        assertEquals(5, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,10,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(11,12,seq_0_12, seq_11_15,seq_11_18,seq_11_20), map.getRegion(1));
        assertEquals(createCoverageRegion(13,15,seq_11_15,seq_11_18,seq_11_20), map.getRegion(2));
        assertEquals(createCoverageRegion(16,18,seq_11_18,seq_11_20), map.getRegion(3));
        assertEquals(createCoverageRegion(19,20,seq_11_20), map.getRegion(4));
        
        assertEquals(20, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
        
        assertEquals("avg cov", 36D/21, map.getAverageCoverage(), 0.001D);
        assertEquals("min cov", 1, map.getMinCoverage());
        assertEquals("max cov", 4, map.getMaxCoverage());
    }
   
    
    @Test
    public void equalsSameRef(){

        CoverageMap<Range> map= new CoverageMapBuilder<Range>(
                                            Arrays.asList(seq_0_9,seq_5_14)).build();
        TestUtil.assertEqualAndHashcodeSame(map, map);

    }
    @Test
    public void iterator(){
        CoverageMap<Range> map= new CoverageMapBuilder<Range>(
                                            Arrays.asList(seq_0_9,seq_5_14)).build();
        Iterator<CoverageRegion<Range>> iter =map.iterator();
        assertEquals(createCoverageRegion(0,4,seq_0_9 ), iter.next());
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_5_14 ), iter.next());
        assertEquals(createCoverageRegion(10,14,seq_5_14 ), iter.next());
        assertFalse(iter.hasNext());
    }
    @Test
    public void equalsSameVaues(){
        CoverageMap<Range> map= new CoverageMapBuilder<Range>(
                                            Arrays.asList(seq_0_9,seq_5_14)).build();
        CoverageMap<Range> sameValues= new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14)).build();
        TestUtil.assertEqualAndHashcodeSame(map, sameValues);
    }
    @Test
    public void notEqualsNull(){
        CoverageMap<Range> map= new CoverageMapBuilder<Range>(
                                            Arrays.asList(seq_0_9,seq_5_14)).build();
        assertFalse(map.equals(null));
    }
    
    @Test
    public void notEqualsDifferentClass(){
        CoverageMap<Range> map= new CoverageMapBuilder<Range>(
                                            Arrays.asList(seq_0_9,seq_5_14)).build();
        assertFalse(map.equals("not a coverage Map"));
    }
    @Test
    public void notEqualsDifferentNumberOfRegions(){

        CoverageMap<Range> map= new CoverageMapBuilder<Range>(
                                            Arrays.asList(seq_0_9,seq_5_14)).build();
        CoverageMap<Range> differentNumberOfRegions= new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9)).build();
        TestUtil.assertNotEqualAndHashcodeDifferent(map, differentNumberOfRegions);
    }
    
    @Test
    public void coverageRegionNotEqual(){
        Range differentSequence = Range.of(seq_5_14.getBegin(), seq_5_14.getEnd()+1);
       
        CoverageMap<Range> map= new CoverageMapBuilder<Range>(
                                            Arrays.asList(seq_0_9,seq_5_14)).build();
        CoverageMap<Range> differentRegions= new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,differentSequence)).build();
        TestUtil.assertNotEqualAndHashcodeDifferent(map, differentRegions);

    }
    
    @Test
    public void getRegionsWhichIntersectNoIntersectionsReturnEmptyList(){
        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12)).build();
        
        assertTrue(map.getRegionsWhichIntersect(Range.of(-5,-1)).isEmpty());
      
        assertTrue(map.getRegionsWhichIntersect(Range.of(15,17)).isEmpty());
    }
    @Test
    public void getRegionsWhichCoversNoIntersectionsReturnNull(){
        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12)).build();
        
        assertNull(map.getRegionWhichCovers(-1));
      
        assertNull(map.getRegionWhichCovers(15));
    }
    
    @Test
    public void emptyCoverageMapWillNeverCover(){
        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Collections.<Range>emptyList()).build();
        
        assertNull(map.getRegionWhichCovers(-1));
      
        assertNull(map.getRegionWhichCovers(15));
    }
    @Test
    public void emptyCoverageMapWillNeverIntesect(){
        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Collections.<Range>emptyList()).build();
        
        assertTrue(map.getRegionsWhichIntersect(Range.of(0,10)).isEmpty());
      
    }
    @Test
    public void getRegionsWhichIntersect(){
        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12)).build();
        
        List<CoverageRegion<Range>> regions = map.getRegionsWhichIntersect(Range.of(6, 11));
        assertEquals(2, regions.size());
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_0_12,seq_5_14 ), regions.get(0));
        assertEquals(createCoverageRegion(10,12,seq_0_12,seq_5_14 ), regions.get(1));

    }
    
    @Test
    public void getRegionsWhichIntersectAllCombinations(){
        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12)).build();
        
        long lastCoordinate = map.getRegion(map.getNumberOfRegions() -1).asRange().getEnd();
        //test every possible range including empty ranges
        for(long i=0; i<= lastCoordinate; i++){
        	for(long j=i-1; j<=lastCoordinate; j++){
        		Range range = Range.of(i,j);
        		assertEquals("range = " + range, computeExpectedCoverageIntersections(map, range),
        				map.getRegionsWhichIntersect(range));
        	}
        }

    }
    
    /**
     * Compute intersection the simple but inefficient way,
     * since CoverageMap implementations might do something
     * more complicated but more efficient.
     */
    private List<CoverageRegion<Range>> computeExpectedCoverageIntersections(CoverageMap<Range> map, Range range){
    	List<CoverageRegion<Range>> ret = new ArrayList<CoverageRegion<Range>>();
    	for(CoverageRegion<Range> region : map){
    		Range regionRange = region.asRange();
    		if(range.intersects(regionRange)){
    			ret.add(region);
    		}
    	}
    	return ret;
    }
    @Test
    public void getRegionsWhichCovers(){
        CoverageMap<Range> map = new CoverageMapBuilder<Range>(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12)).build();
        
        for(int i=0; i<5; i++){
        	assertEquals("offset "+ i, map.getRegion(0), map.getRegionWhichCovers(i));
        }
        for(int i=5; i<=9; i++){
        	assertEquals("offset "+ i, map.getRegion(1), map.getRegionWhichCovers(i));
        }
        for(int i=10; i<=12; i++){
        	assertEquals("offset "+ i, map.getRegion(2), map.getRegionWhichCovers(i));
        }
        for(int i=13; i<=14; i++){
        	assertEquals("offset "+ i, map.getRegion(3), map.getRegionWhichCovers(i));
        }
       
    }
    
    private static long getLastCoveredOffsetIn(CoverageMap<?> coverageMap){
        if(coverageMap.isEmpty()){
            return -1L;
        }
        return coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd();
    }
}
