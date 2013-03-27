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
package org.jcvi.jillion_experimental.primer;

import java.util.List;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion_experimental.primer.PrimerDetector;
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
