package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
public class TestNucleotideSequenceStdIterator {

    @Test
    public void fwdAndReverseCompOfRandomSequencesMatchEvenLength(){
        assertStdIteratorForLength(100);
    }
    @Test
    public void fwdAndReverseCompOfRandomSequencesMatchOddLength(){
        assertStdIteratorForLength(101);
    }

    @Test
    public void allNs(){

        NucleotideSequence s = new NucleotideSequenceBuilder("NNNNNNNNNN").build();

        List<Nucleotide> fwd = fromIter(s.computeStandardizedIterator());
        List<Nucleotide> rev = fromIter(s.reverseComplement().computeStandardizedIterator());

        assertEquals(fwd, rev);
    }
    @Test
    public void matchesRevCompAsAndTs(){

        NucleotideSequence s = new NucleotideSequenceBuilder("AAATTT").build();

        List<Nucleotide> fwd = fromIter(s.computeStandardizedIterator());
        List<Nucleotide> rev = fromIter(s.reverseComplement().computeStandardizedIterator());

        assertEquals(fwd, rev);
    }

    @Test
    public void basicImpl(){

        NucleotideSequence s = new NucleotideSequenceBuilder("CCCGGG")
                .turnOffDataCompression(true)
                .build();

        List<Nucleotide> fwd = fromIter(s.computeStandardizedIterator());
        List<Nucleotide> rev = fromIter(s.reverseComplement().computeStandardizedIterator());

        assertEquals(fwd, rev);
    }
    private static void assertStdIteratorForLength(int length) {
        for(int i=0; i< 50; i++){
            NucleotideSequence s = NucleotideSequenceTestUtil.createRandom(length);
            List<Nucleotide> fwd = fromIter(s.computeStandardizedIterator());
            List<Nucleotide> rev = fromIter(s.reverseComplement().computeStandardizedIterator());

            assertEquals(fwd, rev);

        }
    }

    @Test
    public void emptySeq(){
        Iterator<Nucleotide> iter = NucleotideSequenceTestUtil.emptySeq().computeStandardizedIterator();
        assertFalse(iter.hasNext());
    }

    private static List<Nucleotide> fromIter(Iterator<Nucleotide> iter){
        List<Nucleotide> l = new ArrayList<>();
        while(iter.hasNext()){
            l.add(iter.next());
        }
        return l;
    }
}
