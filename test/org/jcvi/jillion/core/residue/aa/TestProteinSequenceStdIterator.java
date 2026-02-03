package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.testutils.ProteinSequenceTestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
public class TestProteinSequenceStdIterator {

    @Test
    public void empty(){
        Iterator<AminoAcid> iter= ProteinSequenceTestUtil.empty().iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    public void stdMatchesForwardButNotReversed(){
        for(int i=0; i<50; i++){
            ProteinSequence s = ProteinSequenceTestUtil.randomSequence(100);
            List<AminoAcid> fwd = fromIter(s.iterator());
            List<AminoAcid> rev = fromIter(s.reverseIterator());
            List<AminoAcid> std = fromIter(s.computeStandardizedIterator());

            assertEquals(fwd, std);
            assertNotEquals(rev, std);
        }
    }

    private static List<AminoAcid> fromIter(Iterator<AminoAcid> iter){
        List<AminoAcid> l = new ArrayList<>();
        while(iter.hasNext()){
            l.add(iter.next());
        }
        return l;
    }


}
