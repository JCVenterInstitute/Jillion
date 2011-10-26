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

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceContig;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAceContig {

    @Test
    public void noPlacedReadsShouldMakeEmptyContig(){
        AceContigBuilder sut =  DefaultAceContig.createBuilder("id",
                "ACGTACGTACGTACGT");
        AceContig contig =sut.build();
        NucleotideSequence consensus =contig.getConsensus();
        assertEquals(0, consensus.getLength());
        assertEquals("id",contig.getId());
        assertEquals(0,contig.getNumberOfReads());
    }
    @Test
    public void callingBuildTwiceShouldThrowIllegalStateException(){
        AceContigBuilder sut =  DefaultAceContig.createBuilder("id",
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
    public void readThatHasNegativeOffsetShouldGetTrimmedToOffsetZero(){
        AceContigBuilder sut =  DefaultAceContig.createBuilder("id",
                                            "ACGTACGTACGTACGT");
        sut.addRead("read", "ACGTACGTACGTACGT", -2, Direction.FORWARD, Range.buildRange(2, 18), null,16);
            AceContig contig =sut.build();
            NucleotideSequence consensus =contig.getConsensus();
            assertEquals(16, consensus.getLength());
            assertEquals("id",contig.getId());
            assertEquals(1,contig.getNumberOfReads());
            assertEquals("ACGTACGTACGTACGT", Nucleotides.asString(contig.getPlacedReadById("read").getNucleotideSequence().asList()));
    }
}
