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

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAcePlacedReadReAbacus {
    String readId = "readId";
    Range validRange= Range.buildRange(15,25);
    int ungappedFullLength =30;
    Direction dir = Direction.FORWARD;
    PhdInfo phdInfo = createMock(PhdInfo.class);
    
    int originalStartOffset=5;
    NucleotideSequence consensus = NucleotideSequenceFactory.create(   
                            "NNNNACGTTACGTTT");
    String originalSequence =   "ACGT-ACG-T";
    AcePlacedReadBuilder sut = DefaultAcePlacedRead.createBuilder(
            consensus, readId, 
            originalSequence, 
            originalStartOffset, dir, validRange, phdInfo, ungappedFullLength);

    @Before
    public void createBuilder(){
        sut = DefaultAcePlacedRead.createBuilder(
                consensus, readId, 
                originalSequence, 
                originalStartOffset, dir, validRange, phdInfo, ungappedFullLength);
    }
    @Test
    public void confirmInitialValues(){
        assertEquals(readId, sut.getId());
        assertEquals(originalStartOffset, sut.getStart());
        assertEquals(dir, sut.getDirection());
        assertEquals(phdInfo, sut.getPhdInfo());
        assertEquals(ungappedFullLength, sut.getUngappedFullLength());
        assertEquals(originalSequence, sut.getBasesBuilder().toString());
        assertEquals(validRange, sut.getClearRange());
    }
    
    @Test
    public void shiftBases(){
        sut.setStartOffset(originalStartOffset+5);
        assertEquals(originalStartOffset+5, sut.getStart());
        
        assertEquals(readId, sut.getId());
        assertEquals(dir, sut.getDirection());
        assertEquals(phdInfo, sut.getPhdInfo());
        assertEquals(ungappedFullLength, sut.getUngappedFullLength());
        assertEquals(originalSequence, sut.getBasesBuilder().toString());
        assertEquals(validRange, sut.getClearRange());
    }
    
    @Test
    public void reAbacus(){
        sut.reAbacus(Range.buildRange(3,9), Nucleotides.parse("TACGT"));
        
        assertEquals(readId, sut.getId());       
        assertEquals(8, sut.getLength());
        assertEquals(originalStartOffset+7, sut.getEnd());
        assertEquals("ACGTACGT", sut.getBasesBuilder().toString());
        assertEquals(validRange, sut.getClearRange());
        
    }
    @Test
    public void reAbacusDifferentNonGapBasesShouldThrowException(){
        try{
            sut.reAbacus(Range.buildRange(3,9), Nucleotides.parse("TRCGT"));
            fail("should throw Exception");
        }catch(IllegalArgumentException expected){
            assertEquals("reAbacusing must retain same ungapped basecalls! 'TACGT' vs 'TRCGT'",
                    expected.getMessage());
        }
    }
}
