package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
public class TestNucleotideRangesOfNs {

    @Test
    public void noNsShouldReturnEmpty(){
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("ACGTACGT");
    
        assertTrue(seq.getRangesOfNs().isEmpty());
    }
    
    @Test
    public void noNsButGapsShouldReturnEmpty(){
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("AC-GTAC-GT");
    
        assertTrue(seq.getRangesOfNs().isEmpty());
    }
    
    
    @Test
    public void singleN(){
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("ACGTNACGT");
    
        List<Range> expected = Arrays.asList(Range.of(4));
        assertEquals(expected, seq.getRangesOfNs());
    }
    
    @Test
    public void oneIslandOfNs(){
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("ACGTNNNNNNACGT");
    
        List<Range> expected = Arrays.asList(Range.of(4,9));
        assertEquals(expected, seq.getRangesOfNs());
    }
    
    @Test
    public void multipleIslands(){
        NucleotideSequence seq = NucleotideSequenceTestUtil.create("ANNCGTNNNNNNACGT");
    
        List<Range> expected = Arrays.asList(Range.of(1,2),
                                            Range.of(6, 11));
        assertEquals(expected, seq.getRangesOfNs());
    }
    
    @Test
    public void referencedBasedSequenceNoNs(){
        NucleotideSequence ref = NucleotideSequenceTestUtil.create("ACGTACGT");
        
        NucleotideSequence seq = ref.toBuilder().setReferenceHint(ref, 0).build();
        

        assertTrue(seq.getRangesOfNs().isEmpty());
        
    }
    
    @Test
    public void referencedBasedSequenceWithNsSameAsRef(){
        NucleotideSequence ref = NucleotideSequenceTestUtil.create("ACGNNTACGT");
        
        NucleotideSequence seq = ref.toBuilder().setReferenceHint(ref, 0).build();
        

        assertEquals(Arrays.asList(Range.of(3,4)),seq.getRangesOfNs());
        
    }
    
    
    @Test
    public void referencedBasedSequenceWithExtraNs(){
        NucleotideSequence ref = NucleotideSequenceTestUtil.create("ACGNNTACGT");
        
        NucleotideSequence seq = ref.toBuilder()
                .setReferenceHint(ref, 0)
                .replace(7, Nucleotide.Unknown)
                .build();
        

        assertEquals(Arrays.asList(Range.of(3,4), Range.of(7)),
                
                seq.getRangesOfNs());
        
    }
    @Test
    public void referencedBasedSequenceWithBaseInsteadOfN(){
        NucleotideSequence ref = NucleotideSequenceTestUtil.create("ACGNNTACGT");
        
        NucleotideSequence seq = ref.toBuilder()
                .setReferenceHint(ref, 0)
                .replace(Range.of(3,4), "AA")
                .build();
        



        assertTrue(seq.getRangesOfNs().isEmpty());
    }
}