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
package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.Kmer;
import org.jcvi.jillion.testutils.ProteinSequenceTestUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestProteinKmers {

    
    @Test
    public void emptySequenceShouldHaveNoKmers(){
        assertEquals(0, ProteinSequenceTestUtil.create("").kmers(3).count());
    }
    
    @Test
    public void lessThanKmerShouldHaveNoKmers(){
        assertEquals(0, ProteinSequenceTestUtil.create("AA").kmers(3).count());
    }
    
    @Test
    public void kmerSizeOfSeqShouldHave1Kmer(){
        ProteinSequence s = ProteinSequenceTestUtil.create("ATG");
        
        assertEquals(Arrays.asList(new Kmer<>(0,s)), s.kmers(3).collect(Collectors.toList()));
    }
    
    @Test
    public void twoKmers(){
        ProteinSequence s = ProteinSequenceTestUtil.create("ATGC");
        
        List<Kmer<ProteinSequence>> expected = Arrays.asList(new Kmer<>(0,ProteinSequenceTestUtil.create("ATG")),
                                                                    new Kmer<>(1,ProteinSequenceTestUtil.create("TGC")));
        
        assertEquals(expected, s.kmers(3).collect(Collectors.toList()));
    }
    
    @Test
    public void threeMers(){
        ProteinSequence s = ProteinSequenceTestUtil.create("AGATCGAGTG");
        
        List<Kmer<ProteinSequence>> expected = Arrays.asList(new Kmer<>(0,ProteinSequenceTestUtil.create("AGA")),
                                                                    new Kmer<>(1,ProteinSequenceTestUtil.create("GAT")),
                                                                     new Kmer<>(2,ProteinSequenceTestUtil.create("ATC")),
                                                                      new Kmer<>(3,ProteinSequenceTestUtil.create("TCG")),
                                                                       new Kmer<>(4,ProteinSequenceTestUtil.create("CGA")),
                                                                        new Kmer<>(5,ProteinSequenceTestUtil.create("GAG")),
                                                                         new Kmer<>(6,ProteinSequenceTestUtil.create("AGT")),
                                                                          new Kmer<>(7,ProteinSequenceTestUtil.create("GTG"))
                                                                    
                
                );
        
        assertEquals(expected, s.kmers(3).collect(Collectors.toList()));
    }
    
    @Test
    public void fiveMers(){
        ProteinSequence s = ProteinSequenceTestUtil.create("GTAGAGCTGT");
        
        List<Kmer<ProteinSequence>> expected = Arrays.asList(new Kmer<>(0,ProteinSequenceTestUtil.create("GTAGA")),
                                                                    new Kmer<>(1,ProteinSequenceTestUtil.create("TAGAG")),
                                                                     new Kmer<>(2,ProteinSequenceTestUtil.create("AGAGC")),
                                                                      new Kmer<>(3,ProteinSequenceTestUtil.create("GAGCT")),
                                                                       new Kmer<>(4,ProteinSequenceTestUtil.create("AGCTG")),
                                                                        new Kmer<>(5,ProteinSequenceTestUtil.create("GCTGT"))
                                                                    
                
                );
        
        assertEquals(expected, s.kmers(5).collect(Collectors.toList()));
    }
}
