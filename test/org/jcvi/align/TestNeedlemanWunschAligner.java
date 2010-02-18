/*
 * Created on Nov 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.jcvi.glyph.nuc.NucleotideGlyph.*;
public class TestNeedlemanWunschAligner{

    /** The substitution matrix to use in the alignment. */
    private final NucleotideSimilarityMatrix matrix = new NucleotideSimilarityMatrix.Builder("default", 0)
                                                            .setScore(Adenine, Adenine, 10)
                                                            .setScore(Adenine, Guanine, -1)
                                                            .setScore(Adenine, Cytosine, -3)
                                                            .setScore(Adenine, Thymine, -4)
                                                            .setScore(Guanine, Guanine, 7)
                                                            .setScore(Guanine, Cytosine, -5)                                                            
                                                            .setScore(Cytosine, Cytosine, 9)
                                                            .setScore(Thymine, Thymine, 8)
                                                            .setScore(Thymine, Guanine, -3)
                                                            .setScore(Thymine, Cytosine, 0)
                                                            .build();
                                                            

    private final Aligner sut=new NeedlemanWunschAligner(matrix,new ConstantGapPenalty(-5));
    
   @Test
   public void alignAgainstSelf(){
     final String basecalls = "CCATATTGATCACGCATTTCCCAGGGATTGAC";
    final NucleotideEncodedGlyphs reference = new DefaultNucleotideEncodedGlyphs(basecalls);
     Alignment alignment = sut.alignSequence(reference, reference);
     assertEquals(1.0F,alignment.getIdentity(),0F);
     SequenceAlignment queryAlignment =alignment.getQueryAlignment();
     assertEquals(basecalls,
             queryAlignment.getGappedSequence(basecalls).toString());     
   }

   @Test
   public void gapped(){
       System.out.println(matrix);
       final String referenceBases = "AGACTAGTTAC";
       final String queryBases =     "CGA"+"GACGT";
       final NucleotideEncodedGlyphs reference = new DefaultNucleotideEncodedGlyphs(referenceBases);
       final NucleotideEncodedGlyphs query = new DefaultNucleotideEncodedGlyphs(queryBases);
       
       Alignment alignment = sut.alignSequence(reference, query);
     //  assertEquals(1, (int)alignment.getScore());
       SequenceAlignment queryAlignment =alignment.getQueryAlignment();
       assertEquals("CGA---GACGT",
               queryAlignment.getGappedSequence(queryBases).toString());
   }
}
