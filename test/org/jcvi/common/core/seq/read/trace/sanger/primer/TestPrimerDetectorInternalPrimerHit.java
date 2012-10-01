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

package org.jcvi.common.core.seq.read.trace.sanger.primer;

import java.util.List;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.trace.sanger.primer.PrimerDetector;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestPrimerDetectorInternalPrimerHit {
																																																																																																																																					
    private final NucleotideSequence sequence = new NucleotideSequenceBuilder(
            "AGGAAAAATTTTTGATTGGATGTCATCCGACTTTACTTTTCTTGAAGTTCCAGCGCAAAATGCCATAAGCACCACATTCCCATATACTGGAGATCCTCCATACAGCCATGGAACAGGAACAGGATACACCATGGACACAGTTAACAGAACACATCAATATTCAGAAAAGGGGAAATGGACAACAAACTCAGAGACTGGAGCCCCCCAACTTAACCCAATTGATGGACCACTGCCCGAGGACAATGAGCCAAGTGGATATGCACAAACGGACTGTGTCCTTGAAGCAATGGCTTTCCTTGAAGAGTCCCACCCAGGAATCTTTGAAAACTCGTGTCTTGAAACGATGGAAGTTGTCCAACAAACAAGAGTGGACAAGTTGACCCAAGGCCGTCAGACCTATGATTGGACACTAAACAGGAACCAGCCGGCTGCAACTGCATTAGCTAATACTATAGAGGTCTTCAGATCGAACGGTCTGACAGCTAATGAATCAGGGAGACTAATAGATTTTCTCAAGGATGTGATGGAATCAATGGATAAAGAGGAAATGGAAATAACAACACACTTCCAGGTCATAGCTGTTTCCTAAACA").build();

    private final NucleotideSequence forwardPrimer = new NucleotideSequenceBuilder("TGTAAAACGACGGCCAGTCRAAAGCAGGCAAACCAT").build();
    private final NucleotideSequence reversePrimer = new NucleotideSequenceBuilder("CAGGAAACAGCTATGACCTGGAARTGYGTTGTKATTTCCATY").build();

    
    @Test
    public void detect(){
        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(forwardPrimer, reversePrimer);
        PrimerDetector detector = new PrimerDetector(13, .9F);
        
        Range expectedRange = Range.of(546, 586);
        List<DirectedRange> actualRanges = detector.detect(sequence, datastore);
        assertEquals(1, actualRanges.size());
        assertEquals(expectedRange, actualRanges.get(0).asRange());
        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
    }
}
