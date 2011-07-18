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

package org.jcvi.assembly.ace;

import org.jcvi.Range;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
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
        DefaultAceContig.Builder sut =  new DefaultAceContig.Builder("id",
                "ACGTACGTACGTACGT");
        DefaultAceContig contig =sut.build();
        NucleotideSequence consensus =contig.getConsensus();
        assertEquals(0, consensus.getLength());
        assertEquals("id",contig.getId());
        assertEquals(0,contig.getNumberOfReads());
    }
    @Test
    public void readThatHasNegativeOffsetShouldGetTrimmedToOffsetZero(){
        DefaultAceContig.Builder sut =  new DefaultAceContig.Builder("id",
                                            "ACGTACGTACGTACGT");
        sut.addRead("read", "ACGTACGTACGTACGT", -2, SequenceDirection.FORWARD, Range.buildRange(2, 18), null,16);
            DefaultAceContig contig =sut.build();
            NucleotideSequence consensus =contig.getConsensus();
            assertEquals(16, consensus.getLength());
            assertEquals("id",contig.getId());
            assertEquals(1,contig.getNumberOfReads());
            assertEquals("ACGTACGTACGTACGT", NucleotideGlyph.convertToString(contig.getPlacedReadById("read").getEncodedGlyphs().decode()));
    }
}
