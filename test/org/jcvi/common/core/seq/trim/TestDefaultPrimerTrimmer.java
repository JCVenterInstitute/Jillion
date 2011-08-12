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

package org.jcvi.common.core.seq.trim;


import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.trim.DefaultPrimerTrimmer;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultPrimerTrimmer {

    private final DefaultPrimerTrimmer sut = new DefaultPrimerTrimmer(5,.9f);
    @Test
    public void onlyPrimerShouldGetCompletelyTrimmedOff(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("AAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(sequence);
        
        Range expectedRange= Range.buildEmptyRange();
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimLeft(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("AAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("AAACG"));
        
        Range expectedRange= Range.buildRange(5, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimLeft_doesNotStartWithPrimer(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("TTAAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("AAACG"));
        
        Range expectedRange= Range.buildRange(7, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimLeftComplimented(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("AAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("CGTTT"));
        
        Range expectedRange= Range.buildRange(5, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimLeftComplimented_doesNotStartWithPrimer(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("TTAAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("CGTTT"));
        
        Range expectedRange= Range.buildRange(7, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRight(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("ACGTACGTACGTAAACG");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("AAACG"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-1-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRight_doesNotEndWithPrimer(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("ACGTACGTACGTAAACGTT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("AAACG"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-3-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRightComplimented(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("ACGTACGTACGTAAACG");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("CGTTT"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-1-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRightComplimented_doesNotEndWithPrimer(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("ACGTACGTACGTAAACGCC");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("CGTTT"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-3-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void primerTooSmallShouldNotTrim(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("ACGTACGTACGTAAACGCC");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("A"));
        
        assertEquals(Range.buildRangeOfLength(sequence.getLength()), sut.trim(sequence, datastore));
        
    }
    
    
    @Test
    public void primerInMiddleShouldTakeLargerSide(){
        NucleotideSequence sequence = NucleotideSequenceFactory.create("AAATTTACGTACGTGGGAAAAAATATA");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(NucleotideSequenceFactory.create("ACGTACGTG"));
        
        Range expectedRange= Range.buildRange(15, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    
    
    
}
