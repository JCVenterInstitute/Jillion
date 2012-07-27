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
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util;


import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestAssemblyUtil_gappedfullRange {

    List<Nucleotide> gappedValidRange = new NucleotideSequenceBuilder("ACGT-ACGT").asList();
    AssembledRead mockPlacedRead;
    /**
     * Creates a new list of {@link Nucleotide}s which is the
     * same as the input list except all the {@link Nucleotide#Gap}
     * objects have been removed.
     * @param gapped a List of nucleotides which may contain gaps.
     * @return a new list of {@link Nucleotide}s which may be empty
     * but will never be null.
     * @throws NullPointerException if gapped is null.
     */
    private static List<Nucleotide> ungap(List<Nucleotide> gapped){
        List<Nucleotide> ungapped = new ArrayList<Nucleotide>(gapped.size());
        for(Nucleotide possibleGap : gapped){
            if(!possibleGap.isGap()){
                ungapped.add(possibleGap);
            }
        }
        return ungapped;
    }
    @Before
    public void setup(){
        mockPlacedRead = createMock(AssembledRead.class);
    }
    @Test
    public void entireSequenceIsValid(){
        
        List<Nucleotide> ungappedUnComplimentedFullRange = ungap(gappedValidRange);
        Range validRange = Range.create(0, ungappedUnComplimentedFullRange.size()-1);
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(readSequence.iterator()).andReturn(gappedValidRange.iterator());
       ReadInfo readInfo = new ReadInfo(validRange, ungappedUnComplimentedFullRange.size());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);
        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.FORWARD);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead,readSequence);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void entireSequenceIsValidButComplimented(){
        
        List<Nucleotide> ungappedUnComplimentedFullRange = new NucleotideSequenceBuilder(gappedValidRange)
        													.ungap()
        													.reverseComplement()
        													.asList();
        Range validRange = Range.create(0, ungappedUnComplimentedFullRange.size()-1);
        
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(readSequence.iterator()).andReturn(gappedValidRange.iterator());
        ReadInfo readInfo = new ReadInfo(validRange, ungappedUnComplimentedFullRange.size());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);

        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.REVERSE);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead, readSequence);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void hasBeyondValidRange(){
        List<Nucleotide> ungappedUnComplimentedFullRange = new NucleotideSequenceBuilder("RRACGTACGTKKK").asList();
        Range validRange = Range.create(2, 9);
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(readSequence.iterator()).andReturn(new NucleotideSequenceBuilder("ACGT-ACGT").build().iterator());
        
        ReadInfo readInfo = new ReadInfo(validRange, ungappedUnComplimentedFullRange.size());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);
        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.FORWARD);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead,readSequence);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        List<Nucleotide> expectedGappedComplimentedFullRange = new NucleotideSequenceBuilder("RRACGT-ACGTKKK").asList();
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
    @Test
    public void hasBeyondValidRangeAndUngapped(){
        List<Nucleotide> ungappedUnComplimentedFullRange = new NucleotideSequenceBuilder("RRACGTACGTKKK").asList();
        Range validRange = Range.create(3, 10);
        ReferenceMappedNucleotideSequence readSequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(readSequence.iterator()).andReturn(new NucleotideSequenceBuilder("MACGTACG").build().iterator());
        
        ReadInfo readInfo = new ReadInfo(validRange, ungappedUnComplimentedFullRange.size());
        expect(mockPlacedRead.getReadInfo()).andStubReturn(readInfo);
        expect(mockPlacedRead.getDirection()).andStubReturn(Direction.REVERSE);
        expect(mockPlacedRead.getNucleotideSequence()).andReturn(readSequence);
        replay(mockPlacedRead,readSequence);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplementedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        List<Nucleotide> expectedGappedComplimentedFullRange = new NucleotideSequenceBuilder("MMMACGTACGTYY").asList();
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
}
