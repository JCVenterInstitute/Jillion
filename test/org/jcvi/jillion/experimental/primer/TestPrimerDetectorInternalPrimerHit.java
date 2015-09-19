/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.primer;

import java.util.List;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
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
