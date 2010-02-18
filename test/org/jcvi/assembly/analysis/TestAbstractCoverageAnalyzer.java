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
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestAbstractCoverageAnalyzer {

    private static class CoverageAnalyzerTestDouble extends AbstractCoverageAnalyzer<VirtualPlacedRead<PlacedRead>,PlacedRead>{

        CoverageMap<CoverageRegion<VirtualPlacedRead<PlacedRead>>> coverageMap;
        public CoverageAnalyzerTestDouble(CoverageMap<CoverageRegion<VirtualPlacedRead<PlacedRead>>> coverageMap,int lowCoverageThreshold,
                int highCoverageTheshold){
            super(lowCoverageThreshold, highCoverageTheshold);
            this.coverageMap = coverageMap;
            
        }
        @Override
        protected CoverageMap<CoverageRegion<VirtualPlacedRead<PlacedRead>>> buildCoverageMap(
                ContigCheckerStruct<PlacedRead> struct) {
            return coverageMap;
        }
        
    }
    
    CoverageMap<CoverageRegion<VirtualPlacedRead<PlacedRead>>> coverageMap;
    Contig contig = createMock(Contig.class);
    ContigCheckerStruct struct;
    Iterator<CoverageRegion<VirtualPlacedRead<PlacedRead>>> mockIter;
    private static final int LOW_COVERAGE_THRESHOLD =2;
    private static final int IGNORED_COVERAGE =3;
    private static final int HIGH_COVERAGE_THRESHOLD =4;
    @Before
    public void setup(){
        coverageMap = createMock(CoverageMap.class);
        mockIter = createMock(Iterator.class);
        expect(coverageMap.iterator()).andReturn(mockIter);
        struct = new ContigCheckerStruct<VirtualPlacedRead<PlacedRead>>(contig,null, PhredQuality.valueOf(30));
    }
    @Test
    public void emptyMap(){
        
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertTrue(analysis.getLowCoverageRegions().isEmpty());
        assertTrue(analysis.getHighCoverageRegions().isEmpty());
    }
    private void finalizeIterator() {
        expect(mockIter.hasNext()).andReturn(false);
    }
    private ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analyze() {
        CoverageAnalyzerTestDouble sut = new CoverageAnalyzerTestDouble(coverageMap, LOW_COVERAGE_THRESHOLD,HIGH_COVERAGE_THRESHOLD);
        ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analysis =sut.analyize(struct);
        return analysis;
    }
    
    @Test
    public void noLowOrHighCoverageRegions(){
        createNextCoverageRegion(IGNORED_COVERAGE);
        createNextCoverageRegion(IGNORED_COVERAGE);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertTrue(analysis.getLowCoverageRegions().isEmpty());
        assertTrue(analysis.getHighCoverageRegions().isEmpty());
    }
    
    @Test
    public void lowCoverageRegions(){
        CoverageRegion<VirtualPlacedRead<PlacedRead>> lowCoverageRegion =
                            createNextCoverageRegion(LOW_COVERAGE_THRESHOLD);
        createNextCoverageRegion(IGNORED_COVERAGE);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertEquals(Arrays.asList(lowCoverageRegion), analysis.getLowCoverageRegions());
        assertTrue(analysis.getHighCoverageRegions().isEmpty());
    }
    
    @Test
    public void highCoverageRegions(){
        CoverageRegion<VirtualPlacedRead<PlacedRead>> highCoverageRegion =
                            createNextCoverageRegion(HIGH_COVERAGE_THRESHOLD);
        createNextCoverageRegion(IGNORED_COVERAGE);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertTrue(analysis.getLowCoverageRegions().isEmpty());
        assertEquals(Arrays.asList(highCoverageRegion), analysis.getHighCoverageRegions());
       
    }
    
    @Test
    public void lowAndhighCoverageRegions(){
        CoverageRegion<VirtualPlacedRead<PlacedRead>> lowCoverageRegion =
                            createNextCoverageRegion(LOW_COVERAGE_THRESHOLD);
        createNextCoverageRegion(IGNORED_COVERAGE);
        CoverageRegion<VirtualPlacedRead<PlacedRead>> highCoverageRegion =
            createNextCoverageRegion(HIGH_COVERAGE_THRESHOLD);
        finalizeIterator();
        replay(coverageMap, mockIter);
        ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertEquals(Arrays.asList(lowCoverageRegion), analysis.getLowCoverageRegions());
        assertEquals(Arrays.asList(highCoverageRegion), analysis.getHighCoverageRegions());
       
    }
    @Test
    public void manyLowAndHighRegions(){
        List<CoverageRegion<VirtualPlacedRead<PlacedRead>>> lowRegions = new ArrayList<CoverageRegion<VirtualPlacedRead<PlacedRead>>>();
        List<CoverageRegion<VirtualPlacedRead<PlacedRead>>> highRegions = new ArrayList<CoverageRegion<VirtualPlacedRead<PlacedRead>>>();
        
        
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
        ContigCoverageAnalysis<VirtualPlacedRead<PlacedRead>> analysis = analyze();
        
        assertEquals(contig, analysis.getContig());
        assertEquals(lowRegions, analysis.getLowCoverageRegions());
        assertEquals(highRegions, analysis.getHighCoverageRegions());
        
    }
    
    private CoverageRegion<VirtualPlacedRead<PlacedRead>> createNextCoverageRegion(int coverage) {
        CoverageRegion<VirtualPlacedRead<PlacedRead>> nextRegion = createCoverageRegion(coverage);
        expect(mockIter.hasNext()).andReturn(true);        
        expect(mockIter.next()).andReturn(nextRegion);
        return nextRegion;
    }
    
    private CoverageRegion<VirtualPlacedRead<PlacedRead>> createCoverageRegion(int coverage){
        CoverageRegion<VirtualPlacedRead<PlacedRead>> region = createMock(CoverageRegion.class);
        expect(region.getCoverage()).andReturn(coverage);
        replay(region);
        return region;
    }
}
