/*
 * Created on Jun 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.Arrays;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.Placed;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultCoverageMapAvgCoverage {

    private static class DefaultCoverageMapTestDouble extends DefaultCoverageMap<Placed,CoverageRegion<Placed>>{

        public DefaultCoverageMapTestDouble(
                List<CoverageRegion<Placed>> regions) {
            super(regions);
        }
        
        
    }
    private static double getAvgCoverageOf(CoverageRegion<Placed>...regions){
        return new DefaultCoverageMapTestDouble(Arrays.asList(regions)).getAverageCoverage();
    }
    private static CoverageRegion<Placed> createCoverageRegion(int start, int end, int depth){
        CoverageRegion<Placed>  mock = createMock(CoverageRegion.class);
        expect(mock.getCoverage()).andStubReturn(depth);
        expect(mock.getStart()).andStubReturn(start);
        expect(mock.getEnd()).andStubReturn(end);
        expect(mock.getLength()).andStubReturn((int)Range.buildRange(start, end).size());
        replay(mock);
        return mock;
    }
    @Test
    public void emtpyMapShouldHaveAvgCoverageOfZero(){
        assertEquals(0D, 
                    getAvgCoverageOf(),
                    0D);
    }
    
    @Test
    public void oneReadAvgCoverageShouldBeOne(){
        assertEquals(1D, getAvgCoverageOf(createCoverageRegion(0,10,1)),0D);
    }
    @Test
    public void oneCoverageRegionAvgCoverageShouldBeDepth(){
        int depth =10;
        assertEquals(depth, 
                    getAvgCoverageOf(
                            createCoverageRegion(0,10,depth))
                
                ,0D);
    }
    @Test
    public void twoCoverageRegionsSameDepth(){
        assertEquals(1D, 
                    getAvgCoverageOf(
                            createCoverageRegion(0,10,1),
                            createCoverageRegion(11,20,1))
                
                ,0D);
    }
    @Test
    public void twoCoverageRegions(){
        assertEquals("same length",31/21D, 
                    getAvgCoverageOf(
                            createCoverageRegion(0,10,1),
                            createCoverageRegion(11,20,2))
                
                ,0D);
        
        assertEquals("different lengths",51/31D, 
                getAvgCoverageOf(
                        createCoverageRegion(0,10,1),
                        createCoverageRegion(11,30,2))
            
            ,0D);
    }
    
    @Test
    public void manyRegions(){
        assertEquals(
                (10 + 2*20 + 3*6 + 2*4 + 20*10)/50D, 
                getAvgCoverageOf(
                        createCoverageRegion(1,10,1),
                        createCoverageRegion(11,30,2),
                        createCoverageRegion(31,36,3),
                        createCoverageRegion(37,40,2),
                        createCoverageRegion(41,50,20))
            
            ,0D);
    }
    @Test
    public void zeroCoverageRegion(){
        assertEquals(
                (10 + 2*20 + 0 + 2*4 + 20*10)/50D, 
                getAvgCoverageOf(
                        createCoverageRegion(1,10,1),
                        createCoverageRegion(11,30,2),
                        createCoverageRegion(31,36,0),
                        createCoverageRegion(37,40,2),
                        createCoverageRegion(41,50,20))
            
            ,0D);
    }
}
