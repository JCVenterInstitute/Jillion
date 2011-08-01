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

package org.jcvi.common.core.seq.read.trace.nextera;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestNexteraTransposonTrimmer {

    private final NexteraTransposonTrimmer sut = new NexteraTransposonTrimmer();
    
    @Test
    public void forwardAdapterOnlyShouldGetCompletelyTrimmedOff(){
        Range clearRange= sut.trim(TransposonEndSequences.FORWARD);
        assertTrue(clearRange.isEmpty());
    }
    @Test
    public void reverseAdapterOnlyShouldGetCompletelyTrimmedOff(){
        Range clearRange= sut.trim(TransposonEndSequences.REVERSE);
        assertTrue(clearRange.isEmpty());
    }
    @Test
    public void forward(){
        List<Nucleotide> bases = new ArrayList<Nucleotide>();
        bases.addAll(TransposonEndSequences.FORWARD.decode());
        bases.addAll(Nucleotides.parse("ACGTACGTACGT"));
        
        Range expectedRange = Range.buildRangeOfLength(
                TransposonEndSequences.FORWARD.getLength(),
                12);
        Range actualRange= sut.trim(new DefaultNucleotideSequence(bases));
        assertEquals(expectedRange,actualRange);
    }
    
    @Test
    public void reverse(){
        List<Nucleotide> bases = new ArrayList<Nucleotide>();
        
        bases.addAll(TransposonEndSequences.REVERSE.decode());
        bases.addAll(Nucleotides.parse("ACGTACGTACGT"));
        
        Range expectedRange = Range.buildRangeOfLength(
                TransposonEndSequences.REVERSE.getLength(),
                12);
        Range actualRange= sut.trim(new DefaultNucleotideSequence(bases));
        assertEquals(expectedRange,actualRange);
    }
}
