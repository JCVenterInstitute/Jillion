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
