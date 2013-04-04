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
package org.jcvi.jillion.assembly.util.coverage;

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
public class TestDefaultCoverageMap {

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
        CoverageMap<Range> map =CoverageMapFactory.create(Collections.<Range>emptyList());
        assertEquals(0, map.getNumberOfRegions());
        assertEquals(-1,getLastCoveredOffsetIn(map));
        assertTrue(map.isEmpty());
    }
    @Test
    public void sizeOf1(){
        Range read = Range.of(0, 0);
        CoverageMap<Range> map =CoverageMapFactory.create(Arrays.asList(read));
        assertEquals(1, map.getNumberOfRegions());
        assertEquals(0, getLastCoveredOffsetIn(map));
    }
    @Test
    public void ignoreSequenceOfZeroSize(){

        CoverageMap<Range> map =CoverageMapFactory.create(Arrays.asList(seq_5_empty));
        assertEquals(0, map.getNumberOfRegions());
        assertEquals(-1,getLastCoveredOffsetIn(map));
        assertTrue(map.isEmpty());

    }
    @Test
    public void oneSequenceShouldHaveOneCoverageRegion(){

        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9));
        assertEquals(1, map.getNumberOfRegions());
        CoverageRegion<Range> expectedRegion = createCoverageRegion(0,9,seq_0_9 );
        assertEquals(expectedRegion,map.getRegion(0));
        assertEquals(9, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
    }
    
    @Test
    public void twoTiledSequencesShouldHave3CoverageRegions(){

        CoverageMap<Range> map =CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14));
        assertEquals(3, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,4,seq_0_9 ), map.getRegion(0));
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_5_14 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,14,seq_5_14 ), map.getRegion(2));
        assertEquals(14, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
    }
    
    @Test
    public void hasleavingSequence(){

        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12));
        assertEquals(4, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,4,seq_0_9,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_0_12,seq_5_14 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,12,seq_0_12,seq_5_14 ), map.getRegion(2));
        assertEquals(createCoverageRegion(13,14,seq_5_14 ), map.getRegion(3));
        assertEquals(14, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
    }
    
    @Test
    public void abutment(){

        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_10_12));
        assertEquals(2, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,9,seq_0_9 ), map.getRegion(0));
        assertEquals(createCoverageRegion(10,12,seq_10_12 ), map.getRegion(1));
        assertEquals(12, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
    }
    
    @Test
    public void zeroCoverageRegion(){

        CoverageMap<Range> map =  CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12,seq_10_12,seq_16_20));
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
    }
    
    @Test
    public void threeConsecutiveReadsEndAtSamePoint(){

        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_12,seq_8_12,seq_10_12,seq_11_15));
        assertEquals(5, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,7,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(8,9,seq_0_12,seq_8_12 ), map.getRegion(1));
        assertEquals(createCoverageRegion(10,10,seq_0_12,seq_8_12, seq_10_12), map.getRegion(2));
        assertEquals(createCoverageRegion(11,12,seq_0_12, seq_8_12,seq_10_12,seq_11_15), map.getRegion(3));
        assertEquals(createCoverageRegion(13,15,seq_11_15), map.getRegion(4));
        assertEquals(15, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
    }
    
    @Test
    public void enteringSequenceStartsAtPreviousRegionsEnd(){

        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_12,seq_8_12,seq_10_12,seq_11_15,seq_12_18));
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
    }
    
    @Test
    public void threeConsecutiveReadsStartAtSamePoint(){

        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_12,seq_11_15,seq_11_18,seq_11_20));
        assertEquals(5, map.getNumberOfRegions());
        assertEquals(createCoverageRegion(0,10,seq_0_12 ), map.getRegion(0));
        assertEquals(createCoverageRegion(11,12,seq_0_12, seq_11_15,seq_11_18,seq_11_20), map.getRegion(1));
        assertEquals(createCoverageRegion(13,15,seq_11_15,seq_11_18,seq_11_20), map.getRegion(2));
        assertEquals(createCoverageRegion(16,18,seq_11_18,seq_11_20), map.getRegion(3));
        assertEquals(createCoverageRegion(19,20,seq_11_20), map.getRegion(4));
        
        assertEquals(20, getLastCoveredOffsetIn(map));
        assertFalse(map.isEmpty());
    }
   
    
    @Test
    public void equalsSameRef(){

        CoverageMap<Range> map= CoverageMapFactory.create(
                                            Arrays.asList(seq_0_9,seq_5_14));
        TestUtil.assertEqualAndHashcodeSame(map, map);

    }
    @Test
    public void iterator(){
        CoverageMap<Range> map= CoverageMapFactory.create(
                                            Arrays.asList(seq_0_9,seq_5_14));
        Iterator<CoverageRegion<Range>> iter =map.iterator();
        assertEquals(createCoverageRegion(0,4,seq_0_9 ), iter.next());
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_5_14 ), iter.next());
        assertEquals(createCoverageRegion(10,14,seq_5_14 ), iter.next());
        assertFalse(iter.hasNext());
    }
    @Test
    public void equalsSameVaues(){
        CoverageMap<Range> map= CoverageMapFactory.create(
                                            Arrays.asList(seq_0_9,seq_5_14));
        CoverageMap<Range> sameValues= CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14));
        TestUtil.assertEqualAndHashcodeSame(map, sameValues);
    }
    @Test
    public void notEqualsNull(){
        CoverageMap<Range> map= CoverageMapFactory.create(
                                            Arrays.asList(seq_0_9,seq_5_14));
        assertFalse(map.equals(null));
    }
    
    @Test
    public void notEqualsDifferentClass(){
        CoverageMap<Range> map= CoverageMapFactory.create(
                                            Arrays.asList(seq_0_9,seq_5_14));
        assertFalse(map.equals("not a coverage Map"));
    }
    @Test
    public void notEqualsDifferentNumberOfRegions(){

        CoverageMap<Range> map= CoverageMapFactory.create(
                                            Arrays.asList(seq_0_9,seq_5_14));
        CoverageMap<Range> differentNumberOfRegions= CoverageMapFactory.create(
                Arrays.asList(seq_0_9));
        TestUtil.assertNotEqualAndHashcodeDifferent(map, differentNumberOfRegions);
    }
    
    @Test
    public void coverageRegionNotEqual(){
        Range differentSequence = Range.of(seq_5_14.getBegin(), seq_5_14.getEnd()+1);
       
        CoverageMap<Range> map= CoverageMapFactory.create(
                                            Arrays.asList(seq_0_9,seq_5_14));
        CoverageMap<Range> differentRegions= CoverageMapFactory.create(
                Arrays.asList(seq_0_9,differentSequence));
        TestUtil.assertNotEqualAndHashcodeDifferent(map, differentRegions);

    }
    
    @Test
    public void getRegionsWhichIntersectNoIntersectionsReturnEmptyList(){
        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12));
        
        assertTrue(map.getRegionsWhichIntersect(Range.of(-5,-1)).isEmpty());
      
        assertTrue(map.getRegionsWhichIntersect(Range.of(15,17)).isEmpty());
    }
    @Test
    public void getRegionsWhichCoversNoIntersectionsReturnNull(){
        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12));
        
        assertNull(map.getRegionWhichCovers(-1));
      
        assertNull(map.getRegionWhichCovers(15));
    }
    
    @Test
    public void getRegionsWhichIntersect(){
        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12));
        
        List<CoverageRegion<Range>> regions = map.getRegionsWhichIntersect(Range.of(6, 11));
        assertEquals(2, regions.size());
        assertEquals(createCoverageRegion(5,9,seq_0_9,seq_0_12,seq_5_14 ), regions.get(0));
        assertEquals(createCoverageRegion(10,12,seq_0_12,seq_5_14 ), regions.get(1));

    }
    
    @Test
    public void getRegionsWhichIntersectAllCombinations(){
        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12));
        
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
        CoverageMap<Range> map = CoverageMapFactory.create(
                Arrays.asList(seq_0_9,seq_5_14,seq_0_12));
        
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
