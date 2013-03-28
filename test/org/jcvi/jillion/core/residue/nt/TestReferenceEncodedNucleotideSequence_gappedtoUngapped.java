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
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.residue.nt.DefaultNucleotideCodec;
import org.jcvi.jillion.core.residue.nt.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestReferenceEncodedNucleotideSequence_gappedtoUngapped {

    String reference       = "ACGTACGTTACGTTACGT";
    String gappedBasecalls = "ACGTACGT-ACGTACG-T";
    String ungappedBasecalls = "ACGTACGTACGTACGT";
    DefaultNucleotideCodec codec = DefaultNucleotideCodec.INSTANCE;
    NucleotideSequence encodedReference = new NucleotideSequenceBuilder(reference).build();
    DefaultReferenceEncodedNucleotideSequence sut = new DefaultReferenceEncodedNucleotideSequence(encodedReference, gappedBasecalls,0);
    
    
    @Test
    public void convertGappedToUngapped_beforeGapsShouldReturnSameNumber(){
        assertEquals(0,sut.getUngappedOffsetFor(0));
        assertEquals(7,sut.getUngappedOffsetFor(7));
        
        assertEquals(0, sut.getGappedOffsetFor(0));
        assertEquals(7,sut.getGappedOffsetFor(7));
    }
    
    @Test
    public void indexOfGapToUngappedIndexShouldReturnIndexMinusNumGaps(){
        final int indexOfFirstGap = gappedBasecalls.indexOf('-');
        assertEquals(indexOfFirstGap -1, sut.getUngappedOffsetFor(indexOfFirstGap));
    }
    
    @Test
    public void convertGappedToUngappedOneGapShouldReturnIndexMinusOne(){
        final int indexOfFirstGap = gappedBasecalls.indexOf('-');
        assertEquals(2, sut.getNumberOfGaps());
        assertEquals(indexOfFirstGap,sut.getUngappedOffsetFor(indexOfFirstGap+1));
        assertEquals(indexOfFirstGap-1,sut.getUngappedOffsetFor(indexOfFirstGap));
    }
    @Test
    public void convertGappedToUngappedLastIndexShouldReturnLengthMinusNumberOfGaps(){
        int numberOfGaps=2;
        int gappedLength = gappedBasecalls.length();
        int lastGappedIndex = gappedLength-1;
        final int lastUngappedIndex = lastGappedIndex-numberOfGaps;
        assertEquals(lastUngappedIndex, sut.getUngappedOffsetFor(lastGappedIndex));
        assertEquals(lastGappedIndex, sut.getGappedOffsetFor(lastUngappedIndex));
        assertEquals(numberOfGaps, sut.getNumberOfGaps());
    }
    
    
}
