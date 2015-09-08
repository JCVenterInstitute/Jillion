/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAceContig {

    @Test
    public void noPlacedReadsShouldMakeEmptyContig(){
    	AceContigBuilder sut =  new AceContigBuilder("id",
                "ACGTACGTACGTACGT");
        AceContig contig =sut.build();
        NucleotideSequence consensus =contig.getConsensusSequence();
        assertEquals(0, consensus.getLength());
        assertEquals("id",contig.getId());
        assertEquals(0,contig.getNumberOfReads());
    }
    @Test
    public void callingBuildTwiceOnEmptyContigShouldThrowIllegalStateException(){
    	AceContigBuilder sut =  new AceContigBuilder("id",
                "ACGTACGTACGTACGT");
        sut.build();
        
        try{
            sut.build();
            fail("should throw IllegalStateException if build() called twice");
        }catch(IllegalStateException e){
            //expected
        }
    }
    @Test
    public void callingBuildTwiceOnPopulatedContigShouldThrowIllegalStateException(){
    	AceContigBuilder sut =  new AceContigBuilder("id", "ACGTACGTACGTACGT");
        sut.build();
        sut.addRead("read", 
        		new NucleotideSequenceBuilder("ACGTACGTACGTACGT").build(), 
        		0, 
        		Direction.FORWARD, 
        		Range.of(2, 18),
        		new PhdInfo("traceName", "phdName", new Date()),
        		18);
        
        try{
            sut.build();
            fail("should throw IllegalStateException if build() called twice");
        }catch(IllegalStateException e){
            //expected
        }
    }
    @Test
    public void readThatHasNegativeOffsetShouldGetTrimmedToOffsetZero(){
    	AceContigBuilder sut =  new AceContigBuilder("id",
                                            "ACGTACGTACGTACGT");
        sut.addRead("read", new NucleotideSequenceBuilder("ACGTACGTACGTACGT").build(), -2, Direction.FORWARD, Range.of(2, 18), null,18);
            AceContig contig =sut.build();
            NucleotideSequence consensus =contig.getConsensusSequence();
            assertEquals(16, consensus.getLength());
            assertEquals("id",contig.getId());
            assertEquals(1,contig.getNumberOfReads());
            assertEquals("ACGTACGTACGTACGT", contig.getRead("read").getNucleotideSequence().toString());
    }
}
