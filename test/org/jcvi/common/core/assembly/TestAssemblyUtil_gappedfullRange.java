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
package org.jcvi.common.core.assembly;


import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestAssemblyUtil_gappedfullRange {

    List<Nucleotide> gappedValidRange = Nucleotides.getNucleotidesFor("ACGT-ACGT");
    PlacedRead mockPlacedRead;
    
    @Before
    public void setup(){
        mockPlacedRead = createMock(PlacedRead.class);
    }
    @Test
    public void entireSequenceIsValid(){
        
        List<Nucleotide> ungappedUnComplimentedFullRange = Nucleotides.convertToUngapped(gappedValidRange);
        Range validRange = Range.buildRange(0, ungappedUnComplimentedFullRange.size());
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getDirection()).andReturn(Direction.FORWARD);
        expect(mockPlacedRead.getSequence()).andReturn(new DefaultNucleotideSequence(gappedValidRange));
        replay(mockPlacedRead);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void entireSequenceIsValidButComplimented(){
        
        List<Nucleotide> ungappedUnComplimentedFullRange = Nucleotides.reverseCompliment(
                                            Nucleotides.convertToUngapped(gappedValidRange));
        Range validRange = Range.buildRange(0, ungappedUnComplimentedFullRange.size());
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getDirection()).andReturn(Direction.REVERSE);
        expect(mockPlacedRead.getSequence()).andReturn(new DefaultNucleotideSequence(gappedValidRange));
        replay(mockPlacedRead);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        assertEquals(gappedValidRange, actualGappedComplimentedFullRange);        
    }
    
    @Test
    public void hasInvalidRange(){
        List<Nucleotide> ungappedUnComplimentedFullRange = Nucleotides.getNucleotidesFor("RRACGTACGTKKK");
        Range validRange = Range.buildRange(2, 9);
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getDirection()).andReturn(Direction.FORWARD);
        expect(mockPlacedRead.getSequence()).andReturn(new DefaultNucleotideSequence(gappedValidRange));
        replay(mockPlacedRead);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        List<Nucleotide> expectedGappedComplimentedFullRange = Nucleotides.getNucleotidesFor("RRACGT-ACGTKKK");
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
    @Test
    public void hasInvalidRangeAndUngapped(){
        List<Nucleotide> ungappedUnComplimentedFullRange = Nucleotides.getNucleotidesFor("RRACGTACGTKKK");
        Range validRange = Range.buildRange(3, 10);
        
        expect(mockPlacedRead.getValidRange()).andReturn(validRange);
        expect(mockPlacedRead.getDirection()).andReturn(Direction.REVERSE);
        expect(mockPlacedRead.getSequence()).andReturn(new DefaultNucleotideSequence(gappedValidRange));
        replay(mockPlacedRead);
        List<Nucleotide> actualGappedComplimentedFullRange =
            AssemblyUtil.buildGappedComplimentedFullRangeBases(mockPlacedRead, ungappedUnComplimentedFullRange);
        
        List<Nucleotide> expectedGappedComplimentedFullRange = Nucleotides.reverseCompliment(
                                                        Nucleotides.getNucleotidesFor("RRACGT-ACGTKKK"));
        assertEquals(expectedGappedComplimentedFullRange, actualGappedComplimentedFullRange);      
    }
}
