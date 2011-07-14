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

package org.jcvi.assembly.trim;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestInternalPrimerHit {

    private final DefaultPrimerTrimmer sut = new DefaultPrimerTrimmer(13, .9f);
    
    private final NucleotideSequence sequence = new DefaultNucleotideSequence(
            "AGGAAAAATTTTTGATTGGATGTCATCCGACTTTACTTTTCTTGAAGTTCCAGCGCAAAATGCCATAAGCACCACATTCCCATATACTGGAGATCCTCCATACAGCCATGGAACAGGAACAGGATACACCATGGACACAGTTAACAGAACACATCAATATTCAGAAAAGGGGAAATGGACAACAAACTCAGAGACTGGAGCCCCCCAACTTAACCCAATTGATGGACCACTGCCCGAGGACAATGAGCCAAGTGGATATGCACAAACGGACTGTGTCCTTGAAGCAATGGCTTTCCTTGAAGAGTCCCACCCAGGAATCTTTGAAAACTCGTGTCTTGAAACGATGGAAGTTGTCCAACAAACAAGAGTGGACAAGTTGACCCAAGGCCGTCAGACCTATGATTGGACACTAAACAGGAACCAGCCGGCTGCAACTGCATTAGCTAATACTATAGAGGTCTTCAGATCGAACGGTCTGACAGCTAATGAATCAGGGAGACTAATAGATTTTCTCAAGGATGTGATGGAATCAATGGATAAAGAGGAAATGGAAATAACAACACACTTCCAGGTCATAGCTGTTTCCTAAACA");

    private final NucleotideSequence forwardPrimer = new DefaultNucleotideSequence("TGTAAAACGACGGCCAGTCRAAAGCAGGCAAACCAT");
    private final NucleotideSequence reversePrimer = new DefaultNucleotideSequence("CAGGAAACAGCTATGACCTGGAARTGYGTTGTKATTTCCATY");


    @Test
    public void trim(){
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(forwardPrimer, reversePrimer);
    
        Range expectedRange = Range.buildRange(0, 586);
        Range actualRange = sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
}
