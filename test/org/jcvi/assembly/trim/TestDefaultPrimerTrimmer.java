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
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
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
    public void trimLeft(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("AAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("AAACG"));
        
        Range expectedRange= Range.buildRange(5, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimLeft_doesNotStartWithPrimer(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("TTAAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("AAACG"));
        
        Range expectedRange= Range.buildRange(7, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimLeftComplimented(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("AAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("CGTTT"));
        
        Range expectedRange= Range.buildRange(5, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimLeftComplimented_doesNotStartWithPrimer(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("TTAAACGACGTACGTACGT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("CGTTT"));
        
        Range expectedRange= Range.buildRange(7, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRight(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("ACGTACGTACGTAAACG");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("AAACG"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-1-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRight_doesNotEndWithPrimer(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("ACGTACGTACGTAAACGTT");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("AAACG"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-3-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRightComplimented(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("ACGTACGTACGTAAACG");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("CGTTT"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-1-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void trimRightComplimented_doesNotEndWithPrimer(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("ACGTACGTACGTAAACGCC");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("CGTTT"));
        
        Range expectedRange= Range.buildRange(0, sequence.getLength()-3-5);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    @Test
    public void primerTooSmallShouldNotTrim(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("ACGTACGTACGTAAACGCC");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("A"));
        
        assertEquals(sequence.getValidRange(), sut.trim(sequence, datastore));
        
    }
    
    
    @Test
    public void primerInMiddleShouldTakeLargerSide(){
        NucleotideEncodedGlyphs sequence = new DefaultNucleotideEncodedGlyphs("AAATTTACGTACGTGGGAAAAAATATA");
        NucleotideDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(new DefaultNucleotideEncodedGlyphs("ACGTACGTG"));
        
        Range expectedRange= Range.buildRange(15, sequence.getLength()-1);
        Range actualRange= sut.trim(sequence, datastore);
        assertEquals(expectedRange, actualRange);
    }
    
    
    
}
