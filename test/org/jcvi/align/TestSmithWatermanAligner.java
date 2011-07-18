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
/**
 * TestSmithWatermanAligner.java
 *
 * Created: Aug 12, 2009 - 1:51:59 PM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.align;

import org.jcvi.common.experimental.align.Aligner;
import org.jcvi.common.experimental.align.Alignment;
import org.jcvi.common.experimental.align.NucleotideSubstitutionMatrix;
import org.jcvi.common.experimental.align.SmithWatermanAligner;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class contains JUnit tests for {@link SmithWatermanAligner}.
 *
 * @author jsitz@jcvi.org
 */
public class TestSmithWatermanAligner
{
    /** The substitution matrix to use in the alignment. */
    private final NucleotideSubstitutionMatrix matrix = new NucleotideSubstitutionMatrix.Builder("default")
                                                        .defaultScore(-4)
                                                        .identityScore(4)
                                                        .gapScore(0)
                                                        .unspecifiedMatchScore(0)
                                                        .ambiguityScore(2)
                                                        .build();
    
    
    
    Aligner sut;
    
    @Before
    public void setupSut(){
        sut= createAligner(matrix);
    }
    protected Aligner createAligner(NucleotideSubstitutionMatrix matrix) {
        return new SmithWatermanAligner(matrix);
    }
    /**
     * Test method for {@link org.jcvi.common.experimental.align.SmithWatermanAligner#alignSequence(java.lang.CharSequence, java.lang.CharSequence)}.
     */
    @Test
    public void testAlignSequences_simple()
    {
        this.testAlignSequences("CCATATTGATCACGCATTTCCCAGGGATTGAC", 
                "ATTGCATCAGGATACCCAG", 
                 new int[] { 5, 24 }, new int[] { 1, 19 }, 
                 new int[] { 8 }, new int[] { 11, 14 },
                0.888);
    }

    /**
     * Test method for {@link org.jcvi.common.experimental.align.SmithWatermanAligner#alignSequence(java.lang.CharSequence, java.lang.CharSequence)}.
     */
    @Test
    public void testAlignSequences_self()
    {
        final String reference = "CCATATTGATCACGCATTTCCCAGGGATTGAC";
        this.testAlignSequences(reference, reference, 
                new int[] { 1, reference.length() }, new int[] { 1, reference.length() }, 
                new int[] {  }, new int[] {  }, 
                1.0);
    }

    /**
     * Test method for {@link org.jcvi.common.experimental.align.SmithWatermanAligner#alignSequence(java.lang.CharSequence, java.lang.CharSequence)}.
     */
    @Test
    public void testAlignSequences_queryExtendsAfter()
    {
        final String reference = "CCATATTGATCACGCATTTCCCAGGGATTGAC";
        final String query = "TTTCCCAGGGATTGACATGA";
        this.testAlignSequences(reference, query, 
                new int[] { 17, 32 }, new int[] { 1, 16 }, 
                new int[] {  }, new int[] {  }, 
                1.0);
    }
    
    /**
     * Test method for {@link org.jcvi.common.experimental.align.SmithWatermanAligner#alignSequence(java.lang.CharSequence, java.lang.CharSequence)}.
     */
    @Test
    public void testAlignSequences_real()
    {
        final String reference = "AGCAAAAGCAGGGGTCTGATCTGTCAAAATGGAGAAAATAGTGCTTCTTCTTGCAATAAT" +
                                 "CAGTCTTGTTAAAAGTGATCAGATTTGCATTGGTTACCATGCAAACAACTCGACAGAGCA" +
                                 "GGTTGACACAATAATGGAAAAGAACGTCACTGTTACACATGCCCAAGACATACTGGAAAA" +
                                 "GACACACAACGGGAAGCTCTGCGATCTAGATGGAGTGAAGCCTCTAATTTTAAGAGATTG" +
                                 "TAGTGTAGCTGGATGGCTCCTCGGGAACCCAATGTGTGACGAATTCATCAATGTGCCGGA" +
                                 "ATGGTCTTACATAGTGGAGAAGGCCAATCCAGCTAATGACCTCTGTTACCCAGGGAATTT" +
                                 "CAACGACTATGAAGAACTGAAACACCTATTGAGCAGAATAAACCATTTTGAGAAAATTCA" +
                                 "GATCATCCCCAAAAGTTCTTGGTCCGATCATGAAGCCTCATCAGGGGTGAGCTCAGCATG" +
                                 "TCCATACCAGGGAAGGTCCTCCTTTTTCAGAAATGTGGTATGGCTTATCAAAAAGAACAG" +
                                 "TGCATACCCAATAATAAAGAGAAGCTACAATAATACCAACCAAGAAGATCTTTTGGTACT" +
                                 "ATGGGGGATTCACCACCCAAATGATGCGGCAGAGCAGACAAGGCTCTATCAAAACCCAAC" +
                                 "CACCTATATTTCCGTTGGGACATCAACACTAAACCAGAGATTGGTACCAAAAATAGCTAC" +
                                 "TAGATCCAAAGTAAACGGGCAAAGTGGAAGGATGGGGTTCTTCTGGACAATTTTAAAACC" +
                                 "GAATGATGCAATCAACTTTGAGAGTAATGGAAATTTCATTGCTCCAGAATATGCATACAA" +
                                 "AATTGTCAAGAAAGGGGACTCAGCAATTATGAAAAGTGAATTGGAATATGGTAACTGCAG" +
                                 "CACCAAGTGTCAAACTCCAATGGGGGCGATAAACTCTAGTATGCCATTCCACAACATACA" +
                                 "CCCTCTCACCATCGGGGAATGCCCCAAATATGTGAAATCAAGCAGATTAGTCCTTGCGAC" +
                                 "TGGGCTCAGAAATAGCCCTCAAAGAGAGAGAAGAAGAAAAAAGAGAGGACTATTTGGAGC" +
                                 "TATAGCAGGTTTTATAGAGGGAGGATGGCAGGGAATGGTAGATGGTTGGTATGGGTACCA" +
                                 "CCATAGCAATGAGCAGGGGAGTGGGTACGCTGCAGACAAAGAATCCACTCAAAAGGCAAT" +
                                 "AGATGGAGTCACCAATAAGGTCAACTCGATCATTGACAAAATGAACACTCAGTTTGAGGC" +
                                 "CGTTGGAAGGGAATTTAATAACTTAGAAAGGAGAATAGAAAATTTGAACAAGAAGATGGA" +
                                 "AGACGGATTCCTAGATGTCTGGACTTATAATGCTGAACTTCTGGTTCTCATGGAAAATGA" +
                                 "GAGAACTCTAGACTTTCATGACTCAAATGTCAAGAACCTTTACGACAAGGTCCGACTACA" +
                                 "GCTTAGGGATAATGCAAAGGAGCTGGGTAACGGTTGTTTCGAGTTCTATCACAGATGTGA" +
                                 "TAATGAATGTATGGAAAGTGTAAGAAACGGAACGTATGACTACCCGCAGTATTCAGAAGA" +
                                 "AGCAAGATTAAAAGGAGAGGAAATAAGTGGAGTAAAATTGGAGTCAATAGGAACTTACCA" +
                                 "AATACTGTCAATTTATTCAACAGTGGCGAGTTCCCTAGCACTGGCAATCATGGTGGCTGG" +
                                 "TCTATCTTTATGGATGTGCTCCAATGGATCGTTACAATGCAGAATTTGCATTTAAATTTG" +
                                 "TGAGTTCAGATTGTAGTTAAAAACACCCTTGTTTCTACT";
        final String query = "GGATTCACCACCCAAATGATGCGGCAGAGCAGACAAGCTCTATCAAAACCCAACCACCTATATT" +
        		             "TCCGTTGGGACATCATCACTAAACCAGAGATTGGT";
        this.testAlignSequences(reference, query, 
                new int[] { 606, 705 },  new int[] { 1, 99 },
                new int[] {  }, new int[] { 37 }, 
                0.989);
    }

    /**
     * Local test driver for testing alignments.
     */
    public void testAlignSequences(String reference, String query, 
                                   int[] queryAlign, int[] referenceAlign, 
                                   int[] queryGaps, int[] referenceGaps,
                                   double identity)
    {
        
        
        final Alignment alignment = sut.alignSequence(
                new DefaultNucleotideSequence(reference),
                new DefaultNucleotideSequence(query));
        
        Assert.assertEquals(queryAlign[0], alignment.getQueryAlignment().getStart());
        Assert.assertEquals(queryAlign[1], alignment.getQueryAlignment().getStop());
        Assert.assertArrayEquals(queryGaps, alignment.getQueryAlignment().getGaps());

        Assert.assertEquals(referenceAlign[0], alignment.getReferenceAlignment().getStart());
        Assert.assertEquals(referenceAlign[1], alignment.getReferenceAlignment().getStop());
        Assert.assertArrayEquals(referenceGaps, alignment.getReferenceAlignment().getGaps());
        
        Assert.assertEquals(identity, alignment.getIdentity(), 0.002);
    }

    @Test
    public void testAlignSelfWithGaps()
    {
        final String querySeq = "AACTGCATGGGATA";
        this.testAlignSequences("AACTTGCATGGGATA", 
                querySeq, 
                new int[] { 1, querySeq.length()+1 }, new int[] { 1, querySeq.length() }, 
                new int[] {  }, new int[] { 4 }, 
                1.0);
    }
    
    @Test
    public void testAlignRefLongerthanQuery()
    {
        final String querySeq = "AACTTGCATGGGATA";
        this.testAlignSequences("AACTTGCATGGGATANNNNNNNNNN", 
                querySeq, 
                new int[] { 1, querySeq.length() }, new int[] { 1, querySeq.length() }, 
                new int[] {  }, new int[] {  }, 
                1.0);
    }
}
