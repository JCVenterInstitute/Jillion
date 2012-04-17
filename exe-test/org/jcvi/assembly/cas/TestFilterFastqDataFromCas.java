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

package org.jcvi.assembly.cas;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jcvi.assembly.cas.FilterFastqDataFromCas.ReadRange;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author dkatzel
 *
 *
 */
public class TestFilterFastqDataFromCas {

    @Test
    public void tooFewReadsShouldNeedAll(){
        
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGTACGT")
                                        .addRead("SOLEXA_1", 0, "ACGTACGT")
                                        .addRead("SOLEXA_2", 2,   "GTACGT")
                                        .addRead("SOLEXA_3", 6,   "GTACGT")
                                        .build();
        
        Set<String> actual =FilterFastqDataFromCas.getNeededReadsFor(2, convertToReadRangeCoverageMap(contig));
        assertEquals(3, actual.size());
    }
    
    @Test
    public void oneReadProovidesExtraCoverageOverWholeLengthShouldGetExcluded(){
        
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGTACGT")
                                        .addRead("SOLEXA_1", 0, "ACGTACGT")
                                        .addRead("SOLEXA_2", 2,   "GTACGT")
                                        .addRead("SOLEXA_3", 6,   "GTACGT")
                                        .addRead("SOLEXA_extra", 6,   "GT")
                                        .build();
        
        Set<String> actual =FilterFastqDataFromCas.getNeededReadsFor(2, convertToReadRangeCoverageMap(contig));
        assertEquals(3, actual.size());
        assertTrue(actual.contains("SOLEXA_1"));
        assertTrue(actual.contains("SOLEXA_2"));
        assertTrue(actual.contains("SOLEXA_3"));
        
        assertFalse(actual.contains("SOLEXA_extra"));
    }
    
    @Test
    public void oneExtraReadOnlyProvidesPartialExtraCoverageShouldNotGetExcluded(){
        
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGTACGT")
                                        .addRead("SOLEXA_1", 0, "ACGTACGT")
                                        .addRead("SOLEXA_2", 2,   "GTACGT")
                                        .addRead("SOLEXA_3", 6,   "GTACGT")
                                        //edge of contig now has 2x coverage
                                        .addRead("SOLEXA_4", 6,   "GTACGT")
                                        .build();
        
        Set<String> actual =FilterFastqDataFromCas.getNeededReadsFor(2, convertToReadRangeCoverageMap(contig));
        assertEquals(4, actual.size());
        assertTrue(actual.contains("SOLEXA_1"));
        assertTrue(actual.contains("SOLEXA_2"));
        assertTrue(actual.contains("SOLEXA_3"));        
        assertTrue(actual.contains("SOLEXA_4"));
    }
    
    @Test
    public void twoExtraReadsShouldGetExcluded(){
        
        Contig<PlacedRead> contig = new DefaultContig.Builder("contigId", "ACGTACGTACGT")
                                        .addRead("SOLEXA_1", 0, "ACGTACGT")
                                        .addRead("SOLEXA_2", 2,   "GTACGT")
                                        .addRead("SOLEXA_3", 6,   "GTACGT")
                                        .addRead("SOLEXA_4", 6,   "GTACGT")
                                        .addRead("SOLEXA_EXTRA_1", 2,   "GTACGT")
                                        .addRead("SOLEXA_EXTRA_2", 6,   "GTACGT")
                                        .build();
        
        Set<String> actual =FilterFastqDataFromCas.getNeededReadsFor(2, convertToReadRangeCoverageMap(contig));
        assertEquals(4, actual.size());
        assertTrue(actual.contains("SOLEXA_1"));
        assertTrue(actual.contains("SOLEXA_2"));
        assertTrue(actual.contains("SOLEXA_3"));        
        assertTrue(actual.contains("SOLEXA_4"));
    }
    
    private CoverageMap<CoverageRegion<ReadRange>> convertToReadRangeCoverageMap(Contig<? extends PlacedRead> contig){
        List<ReadRange> readRanges = new ArrayList<ReadRange>();
        CloseableIterator<? extends PlacedRead> iter = null;
        try{
        	iter = contig.getReadIterator();
        	while(iter.hasNext()){
        		PlacedRead read = iter.next();
        		readRanges.add(new ReadRange(read.getId(), read.asRange()));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        return DefaultCoverageMap.buildCoverageMap(readRanges);
    }
}
