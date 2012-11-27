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

package org.jcvi.common.core.assembly.ace.consed;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.SortedMap;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.AceAssembledRead;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigTestUtil;
import org.jcvi.common.core.assembly.ace.DefaultAceContigBuilder;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.junit.Test;
/**
 * Tests Split 0x regions if
 * there are no gaps in the consensus.
 * 
 * @author dkatzel
 *
 *
 */
public class TestConsedUtil_Split0xNoGaps {

    private final String originalId="origId";
    private final String referenceConsensus = "AACGTACGTAAACGTACGTAA";
    
    
    @Test
    public void contigWithNo0xRegionsShouldNotTrim(){       
    	DefaultAceContigBuilder contigBuilder =
        	new DefaultAceContigBuilder(originalId,referenceConsensus)
        .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
        		0, 
                Direction.FORWARD, 
                Range.of(0, 10), 
                createMock(PhdInfo.class),
                20)
        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(10)).build(),
        		10,
                Direction.FORWARD, 
                Range.of(0, 11), 
                createMock(PhdInfo.class),
                20);
        
        final SortedMap<Range,AceContig> actualcontigs = ConsedUtil.split0xContig(contigBuilder, false);
        assertEquals(1,actualcontigs.size());
        AceContig expected = new DefaultAceContigBuilder(originalId,referenceConsensus)
							        		 .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
							        	        		0, 
							        	                Direction.FORWARD, 
							        	                Range.of(0, 10), 
							        	                createMock(PhdInfo.class),
							        	                20)
							        	        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(10)).build(),
							        	        		10,
							        	                Direction.FORWARD, 
							        	                Range.of(0, 11), 
							        	                createMock(PhdInfo.class),
							        	                20)
                                        .build();
        Range expectedRange = Range.of(0,20);
        assertEquals(expectedRange, actualcontigs.firstKey());
        AceContigTestUtil.assertContigsEqual(expected, actualcontigs.get(expectedRange));
    }
    
    @Test
    public void singleContigThatMissesEdgesShouldReturnUntrimmedButWithSubRange(){       
    	DefaultAceContigBuilder contigBuilder =
        	new DefaultAceContigBuilder(originalId,
        			new NucleotideSequenceBuilder(referenceConsensus)
        					.prepend("NNNN")
        					.append("NNNN")
        					.build())
        .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
        		4, 
                Direction.FORWARD, 
                Range.of(0, 10), 
                createMock(PhdInfo.class),
                20)
        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(10)).build(),
        		14,
                Direction.FORWARD, 
                Range.of(0, 11), 
                createMock(PhdInfo.class),
                20);
        
        final SortedMap<Range,AceContig> actualcontigs = ConsedUtil.split0xContig(contigBuilder, false);
        assertEquals(1,actualcontigs.size());
        AceContig expected = new DefaultAceContigBuilder(originalId+"_5_25",referenceConsensus)
							        		 .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
							        	        		0, 
							        	                Direction.FORWARD, 
							        	                Range.of(0, 10), 
							        	                createMock(PhdInfo.class),
							        	                20)
							        	        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(10)).build(),
							        	        		10,
							        	                Direction.FORWARD, 
							        	                Range.of(0, 11), 
							        	                createMock(PhdInfo.class),
							        	                20)
                                        .build();
        Range expectedRange = Range.of(4,24);
        assertEquals(expectedRange, actualcontigs.firstKey());
        AceContigTestUtil.assertContigsEqual(expected, actualcontigs.get(expectedRange));
    }
    
    
    
    
    @Test
    public void one0xRegionShouldSplitContigIn2(){
        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);

        DefaultAceContigBuilder contig = new DefaultAceContigBuilder(originalId,referenceConsensus)
		        .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(), 
		        		0, 
		                Direction.FORWARD, 
		                Range.of(0, 10), 
		                read1Phd,
		                20)
		        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(12)).build(),
		        		12, 
		                Direction.FORWARD, 
		                Range.of(0, 9), 
		                read2Phd,
		                20);
		       
    
        SortedMap<Range,AceContig> splitContigs = ConsedUtil.split0xContig(contig,  false);
        assertEquals("# of split contigs", 2, splitContigs.size());
        
        AceContig expectedFirstContig = new DefaultAceContigBuilder(
                String.format("%s_%d_%d",originalId,1,11),referenceConsensus.substring(0, 11))
                            .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
                            		0, 
                                    Direction.FORWARD, 
                                    Range.of(0, 10), 
                                    read1Phd,
                                    20)
                                    .build();
        AceContig expectedSecondContig = new DefaultAceContigBuilder(
                String.format("%s_%d_%d",originalId,13,21),referenceConsensus.substring(12))
                        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(12)).build(),
                        		0, 
                                Direction.FORWARD, 
                                Range.of(0, 9), 
                                read2Phd,
                                20)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(Range.of(0,10)));
        assertContigsEqual(expectedSecondContig, splitContigs.get(Range.of(12,20)));
    }
    
    @Test
    public void contigIdAlreadyHasCoordinatesAtTheEnd_ShouldModifyThoseCoordinates(){

        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);
        DefaultAceContigBuilder contig = new DefaultAceContigBuilder("id_1_12",referenceConsensus)
        
		        .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
		        		0, 
		                Direction.FORWARD, 
		                Range.of(0, 10), 
		                read1Phd,
		                20)
		        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(12)).build(),
		        		12, 
		                Direction.FORWARD, 
		                Range.of(0, 9), 
		                read2Phd,
		                20);
        SortedMap<Range,AceContig> splitContigs = ConsedUtil.split0xContig(contig, true);
        
        assertEquals("# of split contigs", 2, splitContigs.size());
        
        AceContig expectedFirstContig = new DefaultAceContigBuilder(
                String.format("id_%d_%d",1,11),referenceConsensus.substring(0, 11))
                            .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
                            		0, 
                                    Direction.FORWARD, 
                                    Range.of(0, 10), 
                                    read1Phd,
                                    20)
                                    .build();
        AceContig expectedSecondContig = new DefaultAceContigBuilder(
                String.format("id_%d_%d",13,21),referenceConsensus.substring(12))
                        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(12)).build(),
                        		0, 
                                Direction.FORWARD, 
                                Range.of(0, 9), 
                                read2Phd,
                                20)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(Range.of(0,10)));
        assertContigsEqual(expectedSecondContig, splitContigs.get(Range.of(12,20)));
    }
    
    
    @Test
    public void contigIdAlreadyHasCoordinatesThatTakeIntoAccountMissing5primeAtTheEnd_ShouldModifyThoseCoordinates(){

        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);
        DefaultAceContigBuilder contig = new DefaultAceContigBuilder("id_5_12",
        		new NucleotideSequenceBuilder(referenceConsensus)
        								.prepend("NNNN")
        								.build())
        
		        .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
		        		4, 
		                Direction.FORWARD, 
		                Range.of(0, 10), 
		                read1Phd,
		                20)
		        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(12)).build(),
		        		16, 
		                Direction.FORWARD, 
		                Range.of(0, 9), 
		                read2Phd,
		                20);
        SortedMap<Range,AceContig> splitContigs = ConsedUtil.split0xContig(contig, true);
        
        assertEquals("# of split contigs", 2, splitContigs.size());
        //since consideUtil thinks we are already shifted 5 we shift 4 more to 9!
        AceContig expectedFirstContig = new DefaultAceContigBuilder(
                String.format("id_%d_%d",9,19),referenceConsensus.substring(0, 11))
                            .addRead("read1", new NucleotideSequenceBuilder(referenceConsensus.substring(0, 11)).build(),
                            		0, 
                                    Direction.FORWARD, 
                                    Range.of(0, 10), 
                                    read1Phd,
                                    20)
                                    .build();
        AceContig expectedSecondContig = new DefaultAceContigBuilder(
                String.format("id_%d_%d",21,29),referenceConsensus.substring(12))
                        .addRead("read2", new NucleotideSequenceBuilder(referenceConsensus.substring(12)).build(),
                        		0, 
                                Direction.FORWARD, 
                                Range.of(0, 9), 
                                read2Phd,
                                20)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(Range.of(4,14)));
        assertContigsEqual(expectedSecondContig, splitContigs.get(Range.of(16,24)));
    }
    
    private void assertContigsEqual(AceContig expected, AceContig actual){
        assertEquals("id",expected.getId(),actual.getId());
        assertEquals("consensus", expected.getConsensusSequence(), actual.getConsensusSequence());
        assertEquals("numberOfReads", expected.getNumberOfReads(), actual.getNumberOfReads());
        StreamingIterator<AceAssembledRead> iter = null;
        try{
        	iter = expected.getReadIterator();
        	while(iter.hasNext()){
        		AceAssembledRead expectedRead = iter.next();
				final String id = expectedRead.getId();
				assertTrue("missing read " + id, actual.containsRead(id));
				assertAcePlacedReadsEqual(expectedRead, actual.getRead(id));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }

    /**
     * @param expectedRead
     * @param placedReadById
     */
    private void assertAcePlacedReadsEqual(AceAssembledRead expected,
            AceAssembledRead actual) {
        assertEquals("id",expected.getId(),actual.getId());
        assertEquals("offset",expected.getGappedStartOffset(),actual.getGappedStartOffset());
        assertEquals("direction",expected.getDirection(),actual.getDirection());
        
        assertEquals("phdInfo",expected.getPhdInfo(),actual.getPhdInfo());
        assertEquals("basecalls",expected.getNucleotideSequence(),actual.getNucleotideSequence());
        assertEquals("validRange",expected.getReadInfo().getValidRange(),actual.getReadInfo().getValidRange());
    }
}
