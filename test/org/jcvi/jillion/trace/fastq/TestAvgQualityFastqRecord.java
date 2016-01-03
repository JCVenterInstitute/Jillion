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
        FastqRecord fastq = new FastqRecordBuilder("id", seq, quals).build();
        
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
