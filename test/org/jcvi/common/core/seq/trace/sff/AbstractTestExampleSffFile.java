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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.internal.ResourceHelper;

public abstract class AbstractTestExampleSffFile{
    protected static final ResourceHelper RESOURCES = new ResourceHelper(AbstractTestExampleSffFile.class);
    protected static File SFF_FILE ; 
    protected static File SFF_FILE_NO_XML ; 
    protected static File SFF_FILE_NO_INDEX ; 
        static{
        try {
            SFF_FILE =RESOURCES.getFile("files/5readExample.sff");
            SFF_FILE_NO_XML =RESOURCES.getFile("files/5readExample_noXML.sff");
            SFF_FILE_NO_INDEX =RESOURCES.getFile("files/5readExample_noIndex.sff");
        } catch (IOException e) {
           throw new IllegalStateException("could not read sff files",e);
        }
    }
    final SffFlowgram FF585OX02HCMO2 = new SffFlowgram("FF585OX02HCMO2",
    		new NucleotideSequenceBuilder(
                  "TCAGCTGGGCTCAAGTGATCTGCCCACCTCAGCTTCCCAAAGTGTTGGGATTACAGGCACGAACCACTGTGCTCGGTCAGCTCTTTTTTTGTTTTTTGGTTTTTTTCCAGGATCCAGTCAAAGTTTGGTTGGAACCGTCCGGGTTTTTAAAAACCCGGAATTCAAACCCTTTCGGTTCCAACACTCAGACCTCACCCTGAGCGGGCTGGCAAGGC").build(),
                  new QualitySequenceBuilder(new byte[]{37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 20, 20, 20, 20, 20, 20, 20, 38, 25, 25, 25, 25, 25, 25, 37, 37, 29, 29, 29, 29, 29, 29, 29, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 40, 40, 40, 40, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 33, 33, 30, 29, 29, 29, 26, 33, 33, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 33, 33, 26, 29, 29, 33, 38, 37, 37, 37, 38, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 33, 33, 33, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37}).build(),
            new short[]{ 104,  101,  100,  103,  106,  100,  301,  106,  101,  100,  204,  104,  104,  101,  105,  100,  102,  100,  103,  305,  109,  199,  104,  99,  106,  101,  103,  202,  302,  304,  101,  104,  101,  200,  304,  105,  205,  101,  107,  104,  207,  104,  104,  103,  109,  206,  208,  98,  99,  97,  105,  104,  103,  106,  104,  101,  211,  99,  100,  107,  102,  108,  104,  101,  731,  102,  590,  196,  710,  201,  100,  190,  101,  107,  206,  101,  105,  108,  102,  293,  104,  300,  197,  200,  211,  218,  211,  99,  98,  202,  298,  494,  470,  295,  192,  206,  190,  95,  294,  285,  268,  104,  195,  203,  182,  186,  104,  106,  101,  98,  102,  94,  102,  101,  193,  104,  89,  93,  287,  97,  101,  93,  86,  101,  279,  102,  101,  181,  101,  208,  194,  101},
            Range.of(CoordinateSystem.RESIDUE_BASED, 5,194),
            Range.of(CoordinateSystem.RESIDUE_BASED, 0,0)
    );
    
    final SffFlowgram FF585OX02HCD8G = new SffFlowgram("FF585OX02HCD8G",
    		new NucleotideSequenceBuilder(
                      "TCAGGGGGGGTCTTCTCCTGTGTGGAGAAATGGTGGCAGAAGCCTGGGGCCAGGCAGAGGAGAGGGAAAAGGTCAAAATTAACTTCTCTCCCCAGTCCCAAACCAACGTTGGAACCGAAAGGGTTTGAATTCAAACCCTTTCGGTTCCAACATGGTGAGACTCTGGGCCACAGGCCGGTTAGCAGTCTGAGCGGGCTGGCAAGGC").build(),
                      new QualitySequenceBuilder(new byte[]{37, 37, 37, 30, 30, 30, 30, 30, 30, 30, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 40, 40, 40, 39, 38, 38, 38, 38, 40, 40, 40, 39, 38, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 38, 38, 38, 38, 38, 38, 34, 34, 34, 34, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 33, 33, 33, 33, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 33, 33, 34, 34, 38, 38, 32, 20, 20, 20, 20, 20, 20, 20, 20, 31, 28, 31, 31, 31, 31, 31, 20, 20, 20, 20, 20, 20, 20, 20, 32, 32, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37}).build(),
                new short[]{ 101,  102,  102,  688,  111,  95,  206,  102,  109,  193,  99,  98,  111,  99,  99,  195,  111,  97,  285,  97,  204,  103,  195,  101,  107,  101,  198,  100,  202,  100,  399,  194,  98,  202,  96,  105,  108,  96,  201,  106,  108,  107,  286,  389,  185,  98,  87,  382,  207,  204,  108,  195,  99,  100,  113,  103,  380,  97,  91,  100,  297,  296,  200,  199,  105,  105,  188,  177,  186,  186,  97,  273,  269,  282,  96,  173,  180,  104,  270,  271,  284,  102,  182,  195,  185,  204,  103,  101,  102,  198,  105,  101,  98,  108,  95,  108,  106,  97,  101,  291,  196,  109,  101,  93,  192,  203,  191,  201,  102,  103,  106,  104,  106,  102,  104,  90,  104,  94,  102,  103,  306,  102,  96,  200,  104,  194,  195,  93},
                
                Range.of(CoordinateSystem.RESIDUE_BASED, 5,186),
                Range.of(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
    
    final SffFlowgram FF585OX02FNE4N = new SffFlowgram("FF585OX02FNE4N",
    		new NucleotideSequenceBuilder(
                      "TCAGGGGGGCACCATTTACAAGGATGATGCCTCCTAAATGTGGTGCAGCATGGTGGCCCCAGGTGTTTACTTCTATACTAAGGCCACACAGATGAATGGTCAAAAATTTGGTGACAGAAATCAATGTTAGATTCTTTAGCTTCTGTTTCCTTCCTCCTTTATTGCCACTGCCTCCAAGTTGGAACCGAAGGGTTTGAATTCAAACCCCTGAGCGGGCTGGCAAGGC").build(),
                      new QualitySequenceBuilder(new byte[]{35, 35, 35, 31, 31, 31, 31, 31, 31, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 40, 38, 38, 38, 39, 39, 39, 37, 35, 35, 32, 32, 32, 33, 35, 35, 35, 34, 34, 34, 34, 35, 32, 33, 33, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 32, 32, 32, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 32, 32, 32, 32, 32, 31, 34, 34, 34, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 34, 34, 34, 34, 34, 35, 35, 35, 35, 33, 34, 34, 32, 21, 20, 20, 20, 18, 18, 18, 18, 17, 17, 18, 26, 31, 32, 32, 32, 32, 26, 26, 21, 21, 21, 21, 29, 35, 35, 35, 35, 29, 29, 29, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35}).build(),
                new short[]{
                         96,  105,  103,  583,  99,  105,  192,  105,  298,  111,  94,  207,  196,  112,  97,  101,  105,  100,  101,  206,  101,  227,  118,  311,  105,  82,  87,  175,  109,  114,  81,  101,  108,  99,  106,  86,  196,  101,  195,  389,  106,  205,  95,  106,  318,  111,  101,  194,  102,  107,  90,  95,  91,  107,  115,  205,  219,  204,  103,  94,  100,  107,  92,  119,  102,  100,  105,  196,  90,  200,  104,  101,  511,  307,  216,  106,  101,  84,  109,  81,  107,  308,  116,  105,  220,  94,  105,  206,  101,  99,  95,  193,  97,  303,  113,  105,  93,  202,  113,  99,  89,  293,  186,  169,  180,  111,  187,  306,  94,  182,  95,  205,  115,  98,  96,  102,  202,  102,  174,  180,  102,  184,  188,  202,  183,  114,  249,  264,  261,  75,  183,  192,  124,  289,  368,  100,  86,  99,  107,  104,  281,  100,  99,  189,  102,  196,  211,  97},
                        Range.of(CoordinateSystem.RESIDUE_BASED, 5,204),
                Range.of(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
    
    final SffFlowgram FF585OX02GMGGN = new SffFlowgram("FF585OX02GMGGN",
    		new NucleotideSequenceBuilder(
                      "TCAGAATTCAAACCCTTTCGGTTCCAACTTTAAAATTAATAATTATTTTTCCCATGTTGTCTAAACAGCTTTAAATATATTTTAATGGGATGTCTCAAAGCTTAGATAAGAATGCTCAGCAAACATATCCAATTTTTAAAATGATATTCATTTATTTTGTATGATTATTGCAACATCACCTCTTCTGTATTATGTGGCCATGTGGAAGAGAATGAGAATGTCACATTCACTTACCTTTGAATAGCAGGCTACTTTGGTATGCATTTTTA").build(),
                      new QualitySequenceBuilder(new byte[]{35, 35, 35, 35, 35, 35, 35, 35, 33, 24, 24, 24, 33, 33, 35, 21, 21, 21, 26, 33, 33, 31, 31, 30, 30, 30, 30, 32, 30, 16, 16, 15, 15, 15, 15, 31, 31, 35, 35, 35, 33, 33, 34, 35, 33, 23, 23, 20, 20, 20, 30, 30, 35, 35, 33, 33, 35, 35, 35, 35, 35, 29, 24, 24, 24, 29, 35, 35, 33, 30, 30, 30, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 33, 33, 33, 35, 35, 35, 35, 35, 35, 33, 24, 24, 24, 35, 35, 35, 35, 35, 35, 35, 23, 22, 24, 30, 29, 33, 35, 35, 35, 35, 35, 35, 35, 35, 29, 29, 29, 29, 26, 35, 35, 33, 27, 27, 27, 27, 14, 14, 14, 14, 14, 15, 15, 15, 15, 11, 31, 33, 36, 35, 33, 35, 35, 35, 23, 24, 24, 24, 33, 35, 35, 33, 29, 20, 29, 35, 35, 35, 35, 35, 35, 35, 35, 33, 29, 29, 29, 33, 35, 35, 35, 35, 35, 35, 35, 35, 29, 29, 29, 33, 35, 35, 35, 33, 33, 33, 33, 35, 35, 35, 35, 35, 35, 35, 20, 20, 20, 27, 27, 35, 27, 20, 20, 20, 20, 33, 33, 35, 35, 27, 27, 27, 33, 35, 34, 34, 34, 34, 34, 34, 34, 34, 24, 24, 24, 24, 24, 24, 24, 20, 20, 20, 29, 24, 24, 24, 24, 32, 32, 32, 32, 32, 34, 27, 21, 18, 18, 16, 16, 13, 13, 14, 23, 25, 25, 20, 20, 13, 13, 13, 13, 13, 13}).build(),
                new short[]{ 102,  102,  102,  107,  197,  200,  92,  269,  302,  273,  109,  190,  185,  191,  179,  101,  279,  354,  201,  196,  98,  192,  198,  98,  540,  298,  108,  102,  100,  179,  100,  101,  101,  103,  268,  104,  102,  102,  119,  282,  294,  101,  93,  94,  99,  401,  221,  95,  316,  101,  103,  110,  101,  93,  103,  103,  271,  98,  108,  186,  104,  103,  92,  113,  159,  97,  164,  103,  100,  88,  114,  95,  105,  100,  101,  282,  105,  63,  112,  100,  105,  223,  178,  471,  366,  66,  109,  104,  94,  100,  190,  110,  100,  338,  89,  399,  107,  64,  113,  105,  107,  86,  182,  96,  199,  107,  92,  177,  108,  97,  122,  84,  116,  191,  90,  86,  204,  119,  79,  118,  96,  102,  183,  114,  90,  85,  109,  198,  217,  116,  113,  90,  149,  188,  189,  78,  62,  96,  181,  126,  93,  104,  109,  169,  86,  119,  103,  105,  99,  98,  102,  203,  99,  112,  93,  171,  88,  204,  266,  99,  164,  110,  103,  101,  110,  109,  197,  98,  95,  109,  106,  258,  179,  85,  110,  89,  100,  109,  93,  459,  118},
                Range.of(CoordinateSystem.RESIDUE_BASED, 5,269),
                Range.of(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
    
    final SffFlowgram FF585OX02FHO5X = new SffFlowgram("FF585OX02FHO5X",
    		new NucleotideSequenceBuilder(
                      "TCAGGGGGGCTTTGGATGTTGGAACCGAAAGGGTTTGAATTCAAACCCTTTCGGTTCCAACGATTTAAACCTACATCCATTTAACTCCAAATCCTGAACGGTTTCCAATAAACAACTTTACATTTGTGTAGCAAATTCCAGGCTATGTAAGAAAGGAGGACTCCACGGTGCACTGAGCGGGCTGGCAAGGC").build(),
                      new QualitySequenceBuilder(new byte[]{37, 37, 34, 15, 15, 22, 22, 22, 22, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 39, 39, 39, 39, 37, 38, 38, 38, 38, 38, 38, 39, 39, 39, 39, 39, 39, 39, 39, 39, 34, 36, 36, 40, 40, 40, 39, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 37, 34, 34, 34, 38, 38, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37}).build(),
                        new short[]{ 104,  104,  96,  568,  104,  291,  203,  101,  98,  102,  197,  202,  186,  192,  105,  286,  285,  283,  107,  183,  186,  92,  282,  301,  290,  105,  201,  208,  198,  199,  101,  103,  100,  295,  292,  198,  104,  105,  103,  101,  100,  190,  98,  291,  204,  102,  107,  202,  297,  96,  201,  100,  100,  201,  102,  203,  299,  201,  185,  101,  292,  100,  202,  106,  284,  106,  102,  101,  279,  90,  109,  101,  103,  104,  105,  101,  286,  207,  198,  98,  205,  107,  100,  104,  99,  103,  104,  193,  100,  296,  201,  102,  209,  102,  99,  101,  196,  104,  105,  199,  101,  104,  94,  104,  107,  100,  108,  98,  95,  102,  300,  101,  104,  202,  103,  193,  203,  98},
                        Range.of(CoordinateSystem.RESIDUE_BASED, 5,172),
                        Range.of(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
}
