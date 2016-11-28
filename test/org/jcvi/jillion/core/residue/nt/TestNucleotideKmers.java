package org.jcvi.jillion.core.residue.nt;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.residue.Kmer;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestNucleotideKmers {

    
    @Test
    public void emptySequenceShouldHaveNoKmers(){
        assertEquals(0, NucleotideSequenceTestUtil.create("").kmers(3).count());
    }
    
    @Test
    public void lessThanKmerShouldHaveNoKmers(){
        assertEquals(0, NucleotideSequenceTestUtil.create("AA").kmers(3).count());
    }
    
    @Test
    public void kmerSizeOfSeqShouldHave1Kmer(){
        NucleotideSequence s = NucleotideSequenceTestUtil.create("ATG");
        
        assertEquals(Arrays.asList(new Kmer<>(0,s)), s.kmers(3).collect(Collectors.toList()));
    }
    
    @Test
    public void twoKmers(){
        NucleotideSequence s = NucleotideSequenceTestUtil.create("ATGC");
        
        List<Kmer<NucleotideSequence>> expected = Arrays.asList(new Kmer<>(0,NucleotideSequenceTestUtil.create("ATG")),
                                                                    new Kmer<>(1,NucleotideSequenceTestUtil.create("TGC")));
        
        assertEquals(expected, s.kmers(3).collect(Collectors.toList()));
    }
    
    @Test
    public void threeMers(){
        NucleotideSequence s = NucleotideSequenceTestUtil.create("AGATCGAGTG");
        
        List<Kmer<NucleotideSequence>> expected = Arrays.asList(new Kmer<>(0,NucleotideSequenceTestUtil.create("AGA")),
                                                                    new Kmer<>(1,NucleotideSequenceTestUtil.create("GAT")),
                                                                     new Kmer<>(2,NucleotideSequenceTestUtil.create("ATC")),
                                                                      new Kmer<>(3,NucleotideSequenceTestUtil.create("TCG")),
                                                                       new Kmer<>(4,NucleotideSequenceTestUtil.create("CGA")),
                                                                        new Kmer<>(5,NucleotideSequenceTestUtil.create("GAG")),
                                                                         new Kmer<>(6,NucleotideSequenceTestUtil.create("AGT")),
                                                                          new Kmer<>(7,NucleotideSequenceTestUtil.create("GTG"))
                                                                    
                
                );
        
        assertEquals(expected, s.kmers(3).collect(Collectors.toList()));
    }
    
    @Test
    public void fiveMers(){
        NucleotideSequence s = NucleotideSequenceTestUtil.create("GTAGAGCTGT");
        
        List<Kmer<NucleotideSequence>> expected = Arrays.asList(new Kmer<>(0,NucleotideSequenceTestUtil.create("GTAGA")),
                                                                    new Kmer<>(1,NucleotideSequenceTestUtil.create("TAGAG")),
                                                                     new Kmer<>(2,NucleotideSequenceTestUtil.create("AGAGC")),
                                                                      new Kmer<>(3,NucleotideSequenceTestUtil.create("GAGCT")),
                                                                       new Kmer<>(4,NucleotideSequenceTestUtil.create("AGCTG")),
                                                                        new Kmer<>(5,NucleotideSequenceTestUtil.create("GCTGT"))
                                                                    
                
                );
        
        assertEquals(expected, s.kmers(5).collect(Collectors.toList()));
    }
}
