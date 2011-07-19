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

package org.jcvi.common.core.assembly.contig.ace;

import java.util.ArrayList;
import java.util.List;
import org.jcvi.Range;
import org.jcvi.assembly.trim.MinimumEndCoverageTrimmer;
import org.jcvi.assembly.trim.PlacedReadTrimmer;
import org.jcvi.assembly.trim.TrimmerException;
import org.jcvi.common.core.assembly.AssemblyTestUtil;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigTrimmer;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceContig;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAceContigTrimmer {

    private AceContigTrimmer sut;
    @Before
    public void setup(){
        List<PlacedReadTrimmer<AcePlacedRead, AceContig>> trimmers = new ArrayList<PlacedReadTrimmer<AcePlacedRead,AceContig>>();
        trimmers.add(new MinimumEndCoverageTrimmer<AcePlacedRead, AceContig>(2));
        sut = new AceContigTrimmer(trimmers);  
    }
    
    private static class TestAceBuilder{
    	private final DefaultAceContig.Builder builder;
    	
    	TestAceBuilder(String id, String consensus){
    		builder = new DefaultAceContig.Builder(id,consensus);
    	}
    	TestAceBuilder addRead(String readId, String gappedBasecalls,int offset, SequenceDirection dir, Range validRange){
    		builder.addRead(readId, gappedBasecalls,offset,dir,validRange,null,offset+gappedBasecalls.length());
    		return this;
    	}
    	AceContig build(){
    		return builder.build();
    	}
    }
    @Test
    public void trim1xFromEnds() throws TrimmerException{
       AceContig originalContig = new TestAceBuilder("id","ACGTACGTACGT")
                                   .addRead("read1", "ACGTACGT", 0, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   .addRead("read2",     "ACGTACGT", 4, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   .addRead("read3",   "GTACGTAC", 2, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       
       AceContig expectedContig = new TestAceBuilder("id_3_10","GTACGTAC")
                                   .addRead("read1", "GTACGT", 0, SequenceDirection.FORWARD, Range.buildRangeOfLength(22,6))
                                   .addRead("read2",   "ACGTAC", 2, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,6))
                                   .addRead("read3", "GTACGTAC", 0, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       AceContig actualContig =sut.trimContig(originalContig);
        
       assertEquals("trimmed id", expectedContig.getId(), actualContig.getId());
       assertEquals("consensus", expectedContig.getConsensus(), actualContig.getConsensus());
       for(AcePlacedRead expectedRead : expectedContig.getPlacedReads()){
           final String id = expectedRead.getId();
           AcePlacedRead actualRead = actualContig.getPlacedReadById(id);
           AssemblyTestUtil.assertPlacedReadCorrect(expectedRead, actualRead);
       }
    }
    @Test
    public void trim1xFromEndsWithReverseReads() throws TrimmerException{
       AceContig originalContig = new TestAceBuilder("id","ACGTACGTACGT")
                                   .addRead("read1", "ACGTACGT", 0, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   .addRead("read2", "ACGTACGT", 4, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   .addRead("read3", "GTACGTAC", 2, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       
       AceContig expectedContig = new TestAceBuilder("id_3_10","GTACGTAC")
                                   .addRead("read1", "GTACGT", 0, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,6))
                                   .addRead("read2", "ACGTAC", 2, SequenceDirection.REVERSE, Range.buildRangeOfLength(22,6))
                                   .addRead("read3", "GTACGTAC", 0, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       AceContig actualContig =sut.trimContig(originalContig);
        
       assertEquals("trimmed id", expectedContig.getId(), actualContig.getId());
       assertEquals("consensus", expectedContig.getConsensus(), actualContig.getConsensus());
       for(AcePlacedRead expectedRead : expectedContig.getPlacedReads()){
           final String id = expectedRead.getId();
           AcePlacedRead actualRead = actualContig.getPlacedReadById(id);
           AssemblyTestUtil.assertPlacedReadCorrect(expectedRead, actualRead);
       }
    }
    
    @Test
    public void trim1xFromEndsWithGaps() throws TrimmerException{
       AceContig originalContig = new TestAceBuilder("id","A-CGT-ACGTACG-T")
                                   .addRead("read1", "A-CGT-ACGT", 0, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   .addRead("read2", "ACGTACG-T", 6, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   .addRead("read3", "GT-ACGTAC", 3, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       
       AceContig expectedContig = new TestAceBuilder("id_3_10","GT-ACGTAC")
                                   .addRead("read1", "GT-ACGT", 0, SequenceDirection.FORWARD, Range.buildRangeOfLength(22,6))
                                   .addRead("read2", "ACGTAC", 3, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,6))
                                   .addRead("read3", "GT-ACGTAC", 0, SequenceDirection.FORWARD, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       AceContig actualContig =sut.trimContig(originalContig);
        
       assertEquals("trimmed id", expectedContig.getId(), actualContig.getId());
       assertEquals("consensus", expectedContig.getConsensus(), actualContig.getConsensus());
       for(AcePlacedRead expectedRead : expectedContig.getPlacedReads()){
           final String id = expectedRead.getId();
           AcePlacedRead actualRead = actualContig.getPlacedReadById(id);
           AssemblyTestUtil.assertPlacedReadCorrect(expectedRead, actualRead);
       }
    }
    @Test
    public void trim1xFromEndsWithGapsReverse() throws TrimmerException{
       AceContig originalContig = new TestAceBuilder("id","A-CGT-ACGTACG-T")
                                   .addRead("read1", "A-CGT-ACGT", 0, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   .addRead("read2", "ACGTACG-T", 6, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   .addRead("read3", "GT-ACGTAC", 3, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       
       AceContig expectedContig = new TestAceBuilder("id_3_10","GT-ACGTAC")
                                   .addRead("read1", "GT-ACGT", 0, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,6))
                                   .addRead("read2", "ACGTAC", 3, SequenceDirection.REVERSE, Range.buildRangeOfLength(22,6))
                                   .addRead("read3", "GT-ACGTAC", 0, SequenceDirection.REVERSE, Range.buildRangeOfLength(20,8))
                                   
                                   .build();
       AceContig actualContig =sut.trimContig(originalContig);
        
       assertEquals("trimmed id", expectedContig.getId(), actualContig.getId());
       assertEquals("consensus", expectedContig.getConsensus(), actualContig.getConsensus());
       for(AcePlacedRead expectedRead : expectedContig.getPlacedReads()){
           final String id = expectedRead.getId();
           AcePlacedRead actualRead = actualContig.getPlacedReadById(id);
           AssemblyTestUtil.assertPlacedReadCorrect(expectedRead, actualRead);
       }
    }
    //TODO make test to trim when consensus to start/end is a gap (test new flanking code)
}
