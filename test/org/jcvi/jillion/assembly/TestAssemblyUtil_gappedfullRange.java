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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.junit.Before;
import org.junit.Test;
public class TestAssemblyUtil_gappedfullRange {

	NucleotideSequence gappedValidRange = new NucleotideSequenceBuilder("ACGT-ACGT").build();
    AssembledRead mockPlacedRead;

    @Before
    public void setup(){
        mockPlacedRead = createMock(AssembledRead.class);
    }
    @Test
    public void entireSequenceIsValid(){
        
    	NucleotideSequence ungappedUnComplimentedFullRange = new NucleotideSequenceBuilder(gappedValidRange)
																.ungap()
																.build();
        Range validRange = new Range.Builder(ungappedUnComplimentedFullRange.getLength()).build();
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(readSequence.getNumberOfGaps()).andReturn(gappedValidRange.getNumberOfGaps());
        expect(readSequence.getLength()).andReturn(gappedValidRange.getLength());
        expect(readSequence.iterator()).andReturn(gappedValidRange.iterator());
       ReadInfo readInfo = new ReadInfo(validRange, (int)ungappedUnComplimentedFullRange.getLength());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);
        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.FORWARD);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead,readSequence);
        NucleotideSequence actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void entireSequenceIsValidButComplimented(){
        
    	NucleotideSequence ungappedUnComplimentedFullRange = new NucleotideSequenceBuilder(gappedValidRange)
        													.ungap()
        													.reverseComplement()
        													.build();
        Range validRange = new Range.Builder(ungappedUnComplimentedFullRange.getLength()).build();
        
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(readSequence.getNumberOfGaps()).andReturn(gappedValidRange.getNumberOfGaps());
        expect(readSequence.getLength()).andReturn(gappedValidRange.getLength());
     
        
        expect(readSequence.iterator()).andReturn(gappedValidRange.iterator());
        ReadInfo readInfo = new ReadInfo(validRange, (int)ungappedUnComplimentedFullRange.getLength());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);

        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.REVERSE);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead, readSequence);
        NucleotideSequence actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void hasBeyondValidRange(){
    	NucleotideSequence ungappedUnComplimentedFullRange = new NucleotideSequenceBuilder("RRACGTACGTKKK").build();
        Range validRange = Range.of(2, 9);
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        NucleotideSequence actualSeq = new NucleotideSequenceBuilder("ACGT-ACGT").build();
		
        expect(readSequence.getNumberOfGaps()).andReturn(actualSeq.getNumberOfGaps());
        expect(readSequence.getLength()).andReturn(actualSeq.getLength());
        expect(readSequence.iterator()).andReturn(actualSeq.iterator());
        
        ReadInfo readInfo = new ReadInfo(validRange, (int)ungappedUnComplimentedFullRange.getLength());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);
        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.FORWARD);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead,readSequence);
        NucleotideSequence actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        NucleotideSequence expectedGappedComplimentedFullRange = new NucleotideSequenceBuilder("RRACGT-ACGTKKK").build();
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
    @Test
    public void hasBeyondValidRangeAndUngapped(){
    	NucleotideSequence ungappedUnComplimentedFullRange = new NucleotideSequenceBuilder("RRACGTACGTKKK").build();
        Range validRange = Range.of(3, 10);
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        NucleotideSequence actualSeq = new NucleotideSequenceBuilder("MACGTACG").build();
        expect(readSequence.getNumberOfGaps()).andReturn(actualSeq.getNumberOfGaps());
        expect(readSequence.getLength()).andReturn(actualSeq.getLength());
        expect(readSequence.iterator()).andReturn(actualSeq.iterator());
        
        ReadInfo readInfo = new ReadInfo(validRange, (int)ungappedUnComplimentedFullRange.getLength());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);
        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.REVERSE);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead,readSequence);
        NucleotideSequence actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        NucleotideSequence expectedGappedComplimentedFullRange = new NucleotideSequenceBuilder("MMMACGTACGTYY").build();
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
}
