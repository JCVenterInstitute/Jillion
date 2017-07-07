/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.trace.fastq.ParsedFastqRecord;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAvgQualityFastqRecord {
    QualitySequence quals = new QualitySequenceBuilder(new byte[]{20,30,40,50,60,70,80,90,100}).build();
    NucleotideSequence seq = NucleotideSequenceTestUtil.create("ACGTACGTT");
    
    private static double DELTA = 0.0001D;
    
    @Test
    public void defaultImplDelegatesToQualitySequence(){
        FastqRecord fastq = FastqRecordBuilder.create("id", seq, quals).build();
        
        assertAvgQualityCorrect(fastq);
    }

    private void assertAvgQualityCorrect(FastqRecord fastq) {
        assertEquals(60, fastq.getQualitySequence().getAvgQuality(),DELTA);
        assertEquals(60, fastq.getAvgQuality(), DELTA);
    }
    
    @Test
    public void parsedSangerEncodedQualitySequence(){
        ParsedFastqRecord record = createParsedFastqFor(FastqQualityCodec.SANGER);
        
        assertAvgQualityCorrect(record);
    }
    
    @Test
    public void parsedIlluminaEncodedQualitySequence(){
        ParsedFastqRecord record = createParsedFastqFor(FastqQualityCodec.ILLUMINA);
        
        assertAvgQualityCorrect(record);
    }

    private ParsedFastqRecord createParsedFastqFor(FastqQualityCodec codec) {
        return new ParsedFastqRecord("id", seq.toString(), codec.encode(quals), codec, false);
    }
}
