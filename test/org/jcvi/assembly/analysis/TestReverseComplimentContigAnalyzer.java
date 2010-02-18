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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestReverseComplimentContigAnalyzer {

    Contig contig;
    ReverseComplimentContigAnalyzer sut = new ReverseComplimentContigAnalyzer();
    ContigCheckerStruct<PlacedRead> struct;
    @Before
    public void setup(){
        contig = createMock(Contig.class);
        struct = new ContigCheckerStruct(contig,null, PhredQuality.valueOf(30));
    }
    
    @Test
    public void emptyContigShouldGiveAPercentOfNaN(){
        expect(contig.getNumberOfReads()).andReturn(0);
        expect(contig.getPlacedReads()).andReturn(Collections.<PlacedRead>emptySet());
        
        replay(contig);
        ReverseComplimentContigAnalysis analysis =sut.analyize(struct);
        assertEquals(Float.NaN,analysis.getPercentReverseComplimented(),0);
        verify(contig);
    }
    
    @Test
    public void validate(){
        int numberOfForward=10;
        int numberOfReverse =0;
        float totalNumberOfReads=10F;
        for(int i=0; i<=10;i++){
            reset(contig);
            final int currentNumberOfReverse = numberOfReverse+i;
            float expectedPercent = currentNumberOfReverse/totalNumberOfReads *100;
            assertAnalysisCorrectFor(numberOfForward-i, currentNumberOfReverse,expectedPercent);
        }
        
    }
    
    
    private void assertAnalysisCorrectFor(int numberOfForwardReads,
            int numberOfReverseReads, float expectedPercent) {
        Set<PlacedRead> reads = createReads(numberOfForwardReads,numberOfReverseReads);        
        expect(contig.getPlacedReads()).andReturn(reads);
        expect(contig.getNumberOfReads()).andReturn(reads.size());
        replay(contig);
        ReverseComplimentContigAnalysis analysis =sut.analyize(struct);
        assertEquals(expectedPercent,analysis.getPercentReverseComplimented(), 0.00F);   
        assertEquals(contig, analysis.getContig());
        assertEquals(numberOfReverseReads, analysis.getNumberOfReverseComplimentedReads());
        verify(contig);
    }
    
  

    private Set<PlacedRead> createReads(int numberOfForwardReads,
            int numberOfReverseReads) {
        Set<PlacedRead> reads = new HashSet<PlacedRead>();
        reads.addAll(createForwardReads(numberOfForwardReads));
        reads.addAll(createReverseReads(numberOfReverseReads));
        return reads;
    }

    private List<PlacedRead> createForwardReads(int numberOfReadsToCreate) {
        List<PlacedRead> reads = new ArrayList<PlacedRead>(numberOfReadsToCreate);
        for(int i=0; i< numberOfReadsToCreate; i++){
            reads.add(createForwardRead());
        }
        return reads;
    }
    private List<PlacedRead> createReverseReads(int numberOfReadsToCreate) {
        List<PlacedRead> reads = new ArrayList<PlacedRead>(numberOfReadsToCreate);
        for(int i=0; i< numberOfReadsToCreate; i++){
            reads.add(createReverseRead());
        }
        return reads;
    }
    private PlacedRead createReverseRead() {
        return createMockRead(SequenceDirection.REVERSE);
    }
    private PlacedRead createForwardRead() {
        return createMockRead(SequenceDirection.FORWARD);
    }

    private PlacedRead createMockRead(SequenceDirection dir) {
        PlacedRead forwardRead = createMock(PlacedRead.class);
        expect(forwardRead.getSequenceDirection()).andStubReturn(dir);
        replay(forwardRead);
        return forwardRead;
    }
}
