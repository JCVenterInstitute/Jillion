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
 * Created on Jun 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.coverage;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
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
    private static CoverageRegion<Placed> createCoverageRegion(long start, long end, int depth){
        CoverageRegion<Placed>  mock = createMock(CoverageRegion.class);
        expect(mock.getCoverage()).andStubReturn(depth);
        expect(mock.getBegin()).andStubReturn(start);
        expect(mock.getEnd()).andStubReturn(end);
        expect(mock.getLength()).andStubReturn(Range.create(start, end).getLength());
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
