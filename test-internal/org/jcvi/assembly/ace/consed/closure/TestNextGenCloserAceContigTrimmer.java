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

package org.jcvi.assembly.ace.consed.closure;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestNextGenCloserAceContigTrimmer {

    NextGenClosureAceContigTrimmer sut = new NextGenClosureAceContigTrimmer(2,5, 10);
    NucleotideEncodedGlyphs consensusWithNoGaps = new DefaultNucleotideEncodedGlyphs("ACGTACGTACGT");
    long consensusLength = consensusWithNoGaps.getLength();
    
    NucleotideEncodedGlyphs consensusWithGaps = new DefaultNucleotideEncodedGlyphs(  "A-GTA-GTAC-T");
    long consensusUngappedLength = consensusWithNoGaps.getUngappedLength();
    
    
    @Test
    public void newContigIdNoTrimsShouldReturnSameId(){
        String id= "id";
        String newId =sut.createNewContigId(id, consensusWithNoGaps, Range.buildRangeOfLength(0, consensusLength));
        assertEquals(newId, id);
    }
    @Test
    public void newContigIdLeftOnlyTrimmed(){
        String id= "id";
        final Range trimRange = Range.buildRangeOfLength(2, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithNoGaps, trimRange);
        assertEquals(String.format("id_%d_%d", 
                trimRange.getLocalStart(),
                trimRange.getLocalEnd()), 
                newId);
    }
    
    @Test
    public void newContigIdRightOnlyTrimmed(){
        String id= "id";
        final Range trimRange = Range.buildRangeOfLength(0, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithNoGaps, trimRange);
        assertEquals(String.format("id_%d_%d", 
                trimRange.getLocalStart(),
                trimRange.getLocalEnd()), 
                newId);
    }
    @Test
    public void newContigIdBothSidesTrimmed(){
        String id= "id";
        final Range trimRange = Range.buildRangeOfLength(2, consensusLength-4).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithNoGaps, trimRange);
        assertEquals(String.format("id_%d_%d", 
                trimRange.getLocalStart(),
                trimRange.getLocalEnd()), 
                newId);
    }
    @Test
    public void newContigId0xLeftOnlyAdditionalTrim(){
        String id= "id_10_22";
        final Range trimRange = Range.buildRangeOfLength(2, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithNoGaps, trimRange);
        assertEquals("id_12_22",
                newId);
    }
    @Test
    public void newContigId0xRightOnlyAdditionalTrim(){
        String id= "id_10_22";
        final Range trimRange = Range.buildRangeOfLength(0, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithNoGaps, trimRange);
        assertEquals(String.format("id_%d_%d", 
                10+trimRange.getStart(),
                10+trimRange.getLength()), 
                newId);
    }
    
    @Test
    public void newContigId0xBothSidesAdditionalTrim(){
        String id= "id_10_22";
        final Range trimRange = Range.buildRangeOfLength(0, consensusLength-4).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithNoGaps, trimRange);
        assertEquals(String.format("id_%d_%d", 
                10+trimRange.getStart(),
                10+trimRange.getLength()), 
                newId);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    @Test
    public void newContigIdLeftOnlyTrimmedWithGaps(){
        String id= "id";
        final Range trimRange = Range.buildRangeOfLength(2, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithGaps, trimRange);
        assertEquals("id_2_9",                 
                newId);
    }
    
    @Test
    public void newContigIdRightOnlyTrimmedWithGaps(){
        String id= "id";
        final Range trimRange = Range.buildRangeOfLength(0, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithGaps, trimRange);
        assertEquals("id_1_8", 
                newId);
    }
    
    @Test
    public void newContigIdBothSidesTrimmedWithGaps(){
        String id= "id";
        final Range trimRange = Range.buildRangeOfLength(2, consensusLength-4).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithGaps, trimRange);
        assertEquals("id_2_8", 
                 
                newId);
    }
    
    @Test
    public void newContigId0xLeftOnlyAdditionalTrimWithGaps(){
        String id= "id_10_19";
        final Range trimRange = Range.buildRangeOfLength(2, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithGaps, trimRange);
        assertEquals("id_11_19", 
                 
                newId);
    }
    
    @Test
    public void newContigId0xRightOnlyAdditionalTrimWithGaps(){
        String id= "id_10_19";
        final Range trimRange = Range.buildRangeOfLength(0, consensusLength-2).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithGaps, trimRange);
        assertEquals("id_10_18", 
                newId);
    }
    
    @Test
    public void newContigId0xBothSidesAdditionalTrimWithGaps(){
        String id= "id_10_19";
        final Range trimRange = Range.buildRangeOfLength(2, consensusLength-4).convertRange(CoordinateSystem.RESIDUE_BASED);
        String newId =sut.createNewContigId(id, consensusWithGaps, trimRange);
        assertEquals("id_11_18" ,
                newId);
    }
    
}
