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
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestAbstractCoverageAnalyzer {

    private static class CoverageAnalyzerTestDouble extends AbstractCoverageAnalyzer<PlacedRead,PlacedRead>{

        CoverageMap<CoverageRegion<PlacedRead>> coverageMap;
        public CoverageAnalyzerTestDouble(CoverageMap<CoverageRegion<PlacedRead>> coverageMap,int lowCoverageThreshold,
                int highCoverageTheshold){
            super(lowCoverageThreshold, highCoverageTheshold);
            this.coverageMap = coverageMap;
            
        }
        @Override
        protected CoverageMap<CoverageRegion<PlacedRead>> buildCoverageMap(
                ContigCheckerStruct<PlacedRead> struct) {
            return coverageMap;
        }
        
    }
    
    CoverageMap<CoverageRegion<PlacedRead>> coverageMap;
    Contig contig = createMock(Contig.class);
    ContigCheckerStruct struct;
    Iterator<CoverageRegion<PlacedRead>> mockIter;
    private static final int LOW_COVERAGE_THRESHOLD =2;
    private static final int IGNORED_COVERAGE =3;
    private static final int HIGH_COVERAGE_THRESHOLD =4;
    @Before
    public void setup(){
        coverageMap = createMock(CoverageMap.class);
        mockIter = createMock(Iterator.class);
        expect(coverageMap.iterator()).andReturn(mockIter);
        struct = new ContigCheckerStruct<PlacedRead>(contig,null);
    }
    @Test
    public void emptyMap(){
        
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<PlacedRead> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertTrue(analysis.getLowCoverageRegions().isEmpty());
        assertTrue(analysis.getHighCoverageRegions().isEmpty());
    }
    private void finalizeIterator() {
        expect(mockIter.hasNext()).andReturn(false);
    }
    private ContigCoverageAnalysis<PlacedRead> analyze() {
        CoverageAnalyzerTestDouble sut = new CoverageAnalyzerTestDouble(coverageMap, LOW_COVERAGE_THRESHOLD,HIGH_COVERAGE_THRESHOLD);
        ContigCoverageAnalysis<PlacedRead> analysis =sut.analyize(struct);
        return analysis;
    }
    
    @Test
    public void noLowOrHighCoverageRegions(){
        createNextCoverageRegion(IGNORED_COVERAGE);
        createNextCoverageRegion(IGNORED_COVERAGE);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<PlacedRead> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertTrue(analysis.getLowCoverageRegions().isEmpty());
        assertTrue(analysis.getHighCoverageRegions().isEmpty());
    }
    
    @Test
    public void lowCoverageRegions(){
        CoverageRegion<PlacedRead> lowCoverageRegion =
                            createNextCoverageRegion(LOW_COVERAGE_THRESHOLD);
        createNextCoverageRegion(IGNORED_COVERAGE);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<PlacedRead> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertEquals(Arrays.asList(lowCoverageRegion), analysis.getLowCoverageRegions());
        assertTrue(analysis.getHighCoverageRegions().isEmpty());
    }
    
    @Test
    public void highCoverageRegions(){
        CoverageRegion<PlacedRead> highCoverageRegion =
                            createNextCoverageRegion(HIGH_COVERAGE_THRESHOLD);
        createNextCoverageRegion(IGNORED_COVERAGE);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<PlacedRead> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertTrue(analysis.getLowCoverageRegions().isEmpty());
        assertEquals(Arrays.asList(highCoverageRegion), analysis.getHighCoverageRegions());
       
    }
    
    @Test
    public void lowAndhighCoverageRegions(){
        CoverageRegion<PlacedRead> lowCoverageRegion =
                            createNextCoverageRegion(LOW_COVERAGE_THRESHOLD);
        createNextCoverageRegion(IGNORED_COVERAGE);
        CoverageRegion<PlacedRead> highCoverageRegion =
            createNextCoverageRegion(HIGH_COVERAGE_THRESHOLD);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<PlacedRead> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertEquals(Arrays.asList(lowCoverageRegion), analysis.getLowCoverageRegions());
        assertEquals(Arrays.asList(highCoverageRegion), analysis.getHighCoverageRegions());
       
    }
    @Test
    public void manyLowAndHighRegions(){
        List<CoverageRegion<PlacedRead>> lowRegions = new ArrayList<CoverageRegion<PlacedRead>>();
        List<CoverageRegion<PlacedRead>> highRegions = new ArrayList<CoverageRegion<PlacedRead>>();
        
        
        lowRegions.add(createNextCoverageRegion(LOW_COVERAGE_THRESHOLD));
        lowRegions.add(createNextCoverageRegion(LOW_COVERAGE_THRESHOLD-1));
        createNextCoverageRegion(IGNORED_COVERAGE);
        highRegions.add(createNextCoverageRegion(HIGH_COVERAGE_THRESHOLD));
        highRegions.add(createNextCoverageRegion(HIGH_COVERAGE_THRESHOLD+1));
        lowRegions.add(createNextCoverageRegion(LOW_COVERAGE_THRESHOLD-1));
        highRegions.add(createNextCoverageRegion(HIGH_COVERAGE_THRESHOLD));
        createNextCoverageRegion(IGNORED_COVERAGE);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<PlacedRead> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertEquals(lowRegions, analysis.getLowCoverageRegions());
        assertEquals(highRegions, analysis.getHighCoverageRegions());
        
    }
    
    private CoverageRegion<PlacedRead> createNextCoverageRegion(int coverage) {
        CoverageRegion<PlacedRead> nextRegion = createCoverageRegion(coverage);
        expect(mockIter.hasNext()).andReturn(true);        
        expect(mockIter.next()).andReturn(nextRegion);
        return nextRegion;
    }
    
    private CoverageRegion<PlacedRead> createCoverageRegion(int coverage){
        CoverageRegion<PlacedRead> region = createMock(CoverageRegion.class);
        expect(region.getCoverage()).andReturn(coverage);
        replay(region);
        return region;
    }
}
