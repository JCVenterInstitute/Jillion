/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.jcvi.jillion.assembly.ace.AceAssembledReadBuilder;
import org.jcvi.jillion.assembly.ace.DefaultAceAssembledRead;
import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAcePlacedReadReAbacus {
    String readId = "readId";
    Range validRange= Range.of(15,25);
    int ungappedFullLength =30;
    Direction dir = Direction.FORWARD;
    PhdInfo phdInfo = new PhdInfo("traceName","phdName",new Date());
    
    int originalStartOffset=5;
    NucleotideSequence consensus = new NucleotideSequenceBuilder("NNNNACGTTACGTTT").build();
    NucleotideSequence originalSequence =   new NucleotideSequenceBuilder("ACGT-ACG-T").build();
    AceAssembledReadBuilder sut = DefaultAceAssembledRead.createBuilder(
            consensus, readId, 
            originalSequence, 
            originalStartOffset, dir, validRange, phdInfo, ungappedFullLength);

    @Before
    public void createBuilder(){
        sut = DefaultAceAssembledRead.createBuilder(
                consensus, readId, 
                originalSequence, 
                originalStartOffset, dir, validRange, phdInfo, ungappedFullLength);
    }
    @Test
    public void confirmInitialValues(){
        assertEquals(readId, sut.getId());
        assertEquals(originalStartOffset, sut.getBegin());
        assertEquals(dir, sut.getDirection());
        assertEquals(phdInfo, sut.getDefaultPhdInfo());
        assertEquals(ungappedFullLength, sut.getUngappedFullLength());
        assertEquals(originalSequence, sut.getNucleotideSequenceBuilder().build());
        assertEquals(validRange, sut.getClearRange());
    }
    
    @Test
    public void shiftBases(){
        sut.setStartOffset(originalStartOffset+5);
        assertEquals(originalStartOffset+5, sut.getBegin());
        
        assertEquals(readId, sut.getId());
        assertEquals(dir, sut.getDirection());
        assertEquals(phdInfo, sut.getDefaultPhdInfo());
        assertEquals(ungappedFullLength, sut.getUngappedFullLength());
        assertEquals(originalSequence, sut.getNucleotideSequenceBuilder().build());
        assertEquals(validRange, sut.getClearRange());
    }
    
    @Test
    public void reAbacus(){
        sut.reAbacus(Range.of(3,9), parse("TACGT"));
        
        assertEquals(readId, sut.getId());       
        assertEquals(8, sut.getLength());
        assertEquals(originalStartOffset+7, sut.getEnd());
        assertEquals("ACGTACGT", sut.getNucleotideSequenceBuilder().toString());
        assertEquals(validRange, sut.getClearRange());
        
    }
    @Test
    public void reAbacusDifferentNonGapBasesShouldThrowException(){
        try{
            sut.reAbacus(Range.of(3,9), parse("TRCGT"));
            fail("should throw Exception");
        }catch(IllegalArgumentException expected){
            assertEquals("reAbacusing must retain same ungapped basecalls! 'TACGT' vs 'TRCGT'",
                    expected.getMessage());
        }
    }
    
    static NucleotideSequence parse(String nucleotides){
        return new NucleotideSequenceBuilder(nucleotides).build();
        
    }
}
