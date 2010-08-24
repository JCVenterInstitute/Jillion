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
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.fileServer.FileServer;
import org.jcvi.io.fileServer.ResourceFileServer;

public abstract class AbstractTestExampleSffFile{
    protected static NucleotideGlyphFactory glyphFactory = NucleotideGlyphFactory.getInstance();
    protected static final RunLengthEncodedGlyphCodec runLengthQualityCodec = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);short[] encodedValues = new short[]{213,0,2, 97, 120};
    protected static final FileServer RESOURCES = new ResourceFileServer(AbstractTestExampleSffFile.class);
    protected static File SFF_FILE ; 
        static{
        try {
            SFF_FILE =RESOURCES.getFile("files/5readExample.sff");
        } catch (IOException e) {
           throw new IllegalStateException("could not read sff file");
        }
    }
    final SFFFlowgram FF585OX02HCMO2 = new SFFFlowgram(
        new DefaultNucleotideEncodedGlyphs(
                glyphFactory.getGlyphsFor(
                  "TCAGCTGGGCTCAAGTGATCTGCCCACCTCAGCTTCCCAAAGTGTTGGGATTACAGGCACGAACCACTGTGCTCGGTCAGCTCTTTTTTTGTTTTTTGGTTTTTTTCCAGGATCCAGTCAAAGTTTGGTTGGAACCGTCCGGGTTTTTAAAAACCCGGAATTCAAACCCTTTCGGTTCCAACACTCAGACCTCACCCTGAGCGGGCTGGCAAGGC")),
                  new DefaultEncodedGlyphs<PhredQuality>(runLengthQualityCodec,
                          PhredQuality.valueOf(new byte[]{37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 20, 20, 20, 20, 20, 20, 20, 38, 25, 25, 25, 25, 25, 25, 37, 37, 29, 29, 29, 29, 29, 29, 29, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 40, 40, 40, 40, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 33, 33, 30, 29, 29, 29, 26, 33, 33, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 33, 33, 26, 29, 29, 33, 38, 37, 37, 37, 38, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 33, 33, 33, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37})),
            Arrays.<Short>asList((short) 104, (short) 101, (short) 100, (short) 103, (short) 106, (short) 100, (short) 301, (short) 106, (short) 101, (short) 100, (short) 204, (short) 104, (short) 104, (short) 101, (short) 105, (short) 100, (short) 102, (short) 100, (short) 103, (short) 305, (short) 109, (short) 199, (short) 104, (short) 99, (short) 106, (short) 101, (short) 103, (short) 202, (short) 302, (short) 304, (short) 101, (short) 104, (short) 101, (short) 200, (short) 304, (short) 105, (short) 205, (short) 101, (short) 107, (short) 104, (short) 207, (short) 104, (short) 104, (short) 103, (short) 109, (short) 206, (short) 208, (short) 98, (short) 99, (short) 97, (short) 105, (short) 104, (short) 103, (short) 106, (short) 104, (short) 101, (short) 211, (short) 99, (short) 100, (short) 107, (short) 102, (short) 108, (short) 104, (short) 101, (short) 731, (short) 102, (short) 590, (short) 196, (short) 710, (short) 201, (short) 100, (short) 190, (short) 101, (short) 107, (short) 206, (short) 101, (short) 105, (short) 108, (short) 102, (short) 293, (short) 104, (short) 300, (short) 197, (short) 200, (short) 211, (short) 218, (short) 211, (short) 99, (short) 98, (short) 202, (short) 298, (short) 494, (short) 470, (short) 295, (short) 192, (short) 206, (short) 190, (short) 95, (short) 294, (short) 285, (short) 268, (short) 104, (short) 195, (short) 203, (short) 182, (short) 186, (short) 104, (short) 106, (short) 101, (short) 98, (short) 102, (short) 94, (short) 102, (short) 101, (short) 193, (short) 104, (short) 89, (short) 93, (short) 287, (short) 97, (short) 101, (short) 93, (short) 86, (short) 101, (short) 279, (short) 102, (short) 101, (short) 181, (short) 101, (short) 208, (short) 194, (short) 101),
            Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,194),
            Range.buildRange(CoordinateSystem.RESIDUE_BASED, 0,0)
    );
    
    final SFFFlowgram FF585OX02HCD8G = new SFFFlowgram(
            new DefaultNucleotideEncodedGlyphs(
                    glyphFactory.getGlyphsFor(
                      "TCAGGGGGGGTCTTCTCCTGTGTGGAGAAATGGTGGCAGAAGCCTGGGGCCAGGCAGAGGAGAGGGAAAAGGTCAAAATTAACTTCTCTCCCCAGTCCCAAACCAACGTTGGAACCGAAAGGGTTTGAATTCAAACCCTTTCGGTTCCAACATGGTGAGACTCTGGGCCACAGGCCGGTTAGCAGTCTGAGCGGGCTGGCAAGGC")),
                      new DefaultEncodedGlyphs<PhredQuality>(runLengthQualityCodec,
                              PhredQuality.valueOf(new byte[]{37, 37, 37, 30, 30, 30, 30, 30, 30, 30, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 40, 40, 40, 39, 38, 38, 38, 38, 40, 40, 40, 39, 38, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 38, 38, 38, 38, 38, 38, 34, 34, 34, 34, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 33, 33, 33, 33, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 33, 33, 34, 34, 38, 38, 32, 20, 20, 20, 20, 20, 20, 20, 20, 31, 28, 31, 31, 31, 31, 31, 20, 20, 20, 20, 20, 20, 20, 20, 32, 32, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37})),
                Arrays.<Short>asList((short) 101, (short) 102, (short) 102, (short) 688, (short) 111, (short) 95, (short) 206, (short) 102, (short) 109, (short) 193, (short) 99, (short) 98, (short) 111, (short) 99, (short) 99, (short) 195, (short) 111, (short) 97, (short) 285, (short) 97, (short) 204, (short) 103, (short) 195, (short) 101, (short) 107, (short) 101, (short) 198, (short) 100, (short) 202, (short) 100, (short) 399, (short) 194, (short) 98, (short) 202, (short) 96, (short) 105, (short) 108, (short) 96, (short) 201, (short) 106, (short) 108, (short) 107, (short) 286, (short) 389, (short) 185, (short) 98, (short) 87, (short) 382, (short) 207, (short) 204, (short) 108, (short) 195, (short) 99, (short) 100, (short) 113, (short) 103, (short) 380, (short) 97, (short) 91, (short) 100, (short) 297, (short) 296, (short) 200, (short) 199, (short) 105, (short) 105, (short) 188, (short) 177, (short) 186, (short) 186, (short) 97, (short) 273, (short) 269, (short) 282, (short) 96, (short) 173, (short) 180, (short) 104, (short) 270, (short) 271, (short) 284, (short) 102, (short) 182, (short) 195, (short) 185, (short) 204, (short) 103, (short) 101, (short) 102, (short) 198, (short) 105, (short) 101, (short) 98, (short) 108, (short) 95, (short) 108, (short) 106, (short) 97, (short) 101, (short) 291, (short) 196, (short) 109, (short) 101, (short) 93, (short) 192, (short) 203, (short) 191, (short) 201, (short) 102, (short) 103, (short) 106, (short) 104, (short) 106, (short) 102, (short) 104, (short) 90, (short) 104, (short) 94, (short) 102, (short) 103, (short) 306, (short) 102, (short) 96, (short) 200, (short) 104, (short) 194, (short) 195, (short) 93),
                
                Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,186),
                Range.buildRange(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
    
    final SFFFlowgram FF585OX02FNE4N = new SFFFlowgram(
            new DefaultNucleotideEncodedGlyphs(
                   glyphFactory.getGlyphsFor(
                      "TCAGGGGGGCACCATTTACAAGGATGATGCCTCCTAAATGTGGTGCAGCATGGTGGCCCCAGGTGTTTACTTCTATACTAAGGCCACACAGATGAATGGTCAAAAATTTGGTGACAGAAATCAATGTTAGATTCTTTAGCTTCTGTTTCCTTCCTCCTTTATTGCCACTGCCTCCAAGTTGGAACCGAAGGGTTTGAATTCAAACCCCTGAGCGGGCTGGCAAGGC")),
                      new DefaultEncodedGlyphs<PhredQuality>(runLengthQualityCodec,
                              PhredQuality.valueOf(new byte[]{35, 35, 35, 31, 31, 31, 31, 31, 31, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 40, 38, 38, 38, 39, 39, 39, 37, 35, 35, 32, 32, 32, 33, 35, 35, 35, 34, 34, 34, 34, 35, 32, 33, 33, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 32, 32, 32, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 32, 32, 32, 32, 32, 31, 34, 34, 34, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 34, 34, 34, 34, 34, 35, 35, 35, 35, 33, 34, 34, 32, 21, 20, 20, 20, 18, 18, 18, 18, 17, 17, 18, 26, 31, 32, 32, 32, 32, 26, 26, 21, 21, 21, 21, 29, 35, 35, 35, 35, 29, 29, 29, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35})),
                Arrays.<Short>asList(
                        (short) 96, (short) 105, (short) 103, (short) 583, (short) 99, (short) 105, (short) 192, (short) 105, (short) 298, (short) 111, (short) 94, (short) 207, (short) 196, (short) 112, (short) 97, (short) 101, (short) 105, (short) 100, (short) 101, (short) 206, (short) 101, (short) 227, (short) 118, (short) 311, (short) 105, (short) 82, (short) 87, (short) 175, (short) 109, (short) 114, (short) 81, (short) 101, (short) 108, (short) 99, (short) 106, (short) 86, (short) 196, (short) 101, (short) 195, (short) 389, (short) 106, (short) 205, (short) 95, (short) 106, (short) 318, (short) 111, (short) 101, (short) 194, (short) 102, (short) 107, (short) 90, (short) 95, (short) 91, (short) 107, (short) 115, (short) 205, (short) 219, (short) 204, (short) 103, (short) 94, (short) 100, (short) 107, (short) 92, (short) 119, (short) 102, (short) 100, (short) 105, (short) 196, (short) 90, (short) 200, (short) 104, (short) 101, (short) 511, (short) 307, (short) 216, (short) 106, (short) 101, (short) 84, (short) 109, (short) 81, (short) 107, (short) 308, (short) 116, (short) 105, (short) 220, (short) 94, (short) 105, (short) 206, (short) 101, (short) 99, (short) 95, (short) 193, (short) 97, (short) 303, (short) 113, (short) 105, (short) 93, (short) 202, (short) 113, (short) 99, (short) 89, (short) 293, (short) 186, (short) 169, (short) 180, (short) 111, (short) 187, (short) 306, (short) 94, (short) 182, (short) 95, (short) 205, (short) 115, (short) 98, (short) 96, (short) 102, (short) 202, (short) 102, (short) 174, (short) 180, (short) 102, (short) 184, (short) 188, (short) 202, (short) 183, (short) 114, (short) 249, (short) 264, (short) 261, (short) 75, (short) 183, (short) 192, (short) 124, (short) 289, (short) 368, (short) 100, (short) 86, (short) 99, (short) 107, (short) 104, (short) 281, (short) 100, (short) 99, (short) 189, (short) 102, (short) 196, (short) 211, (short) 97),
                        Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,204),
                Range.buildRange(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
    
    final SFFFlowgram FF585OX02GMGGN = new SFFFlowgram(
            new DefaultNucleotideEncodedGlyphs(
                   glyphFactory.getGlyphsFor(
                      "TCAGAATTCAAACCCTTTCGGTTCCAACTTTAAAATTAATAATTATTTTTCCCATGTTGTCTAAACAGCTTTAAATATATTTTAATGGGATGTCTCAAAGCTTAGATAAGAATGCTCAGCAAACATATCCAATTTTTAAAATGATATTCATTTATTTTGTATGATTATTGCAACATCACCTCTTCTGTATTATGTGGCCATGTGGAAGAGAATGAGAATGTCACATTCACTTACCTTTGAATAGCAGGCTACTTTGGTATGCATTTTTA")),
                      new DefaultEncodedGlyphs<PhredQuality>(runLengthQualityCodec,
                              PhredQuality.valueOf(new byte[]{35, 35, 35, 35, 35, 35, 35, 35, 33, 24, 24, 24, 33, 33, 35, 21, 21, 21, 26, 33, 33, 31, 31, 30, 30, 30, 30, 32, 30, 16, 16, 15, 15, 15, 15, 31, 31, 35, 35, 35, 33, 33, 34, 35, 33, 23, 23, 20, 20, 20, 30, 30, 35, 35, 33, 33, 35, 35, 35, 35, 35, 29, 24, 24, 24, 29, 35, 35, 33, 30, 30, 30, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 33, 33, 33, 35, 35, 35, 35, 35, 35, 33, 24, 24, 24, 35, 35, 35, 35, 35, 35, 35, 23, 22, 24, 30, 29, 33, 35, 35, 35, 35, 35, 35, 35, 35, 29, 29, 29, 29, 26, 35, 35, 33, 27, 27, 27, 27, 14, 14, 14, 14, 14, 15, 15, 15, 15, 11, 31, 33, 36, 35, 33, 35, 35, 35, 23, 24, 24, 24, 33, 35, 35, 33, 29, 20, 29, 35, 35, 35, 35, 35, 35, 35, 35, 33, 29, 29, 29, 33, 35, 35, 35, 35, 35, 35, 35, 35, 29, 29, 29, 33, 35, 35, 35, 33, 33, 33, 33, 35, 35, 35, 35, 35, 35, 35, 20, 20, 20, 27, 27, 35, 27, 20, 20, 20, 20, 33, 33, 35, 35, 27, 27, 27, 33, 35, 34, 34, 34, 34, 34, 34, 34, 34, 24, 24, 24, 24, 24, 24, 24, 20, 20, 20, 29, 24, 24, 24, 24, 32, 32, 32, 32, 32, 34, 27, 21, 18, 18, 16, 16, 13, 13, 14, 23, 25, 25, 20, 20, 13, 13, 13, 13, 13, 13})),
                Arrays.<Short>asList((short) 102, (short) 102, (short) 102, (short) 107, (short) 197, (short) 200, (short) 92, (short) 269, (short) 302, (short) 273, (short) 109, (short) 190, (short) 185, (short) 191, (short) 179, (short) 101, (short) 279, (short) 354, (short) 201, (short) 196, (short) 98, (short) 192, (short) 198, (short) 98, (short) 540, (short) 298, (short) 108, (short) 102, (short) 100, (short) 179, (short) 100, (short) 101, (short) 101, (short) 103, (short) 268, (short) 104, (short) 102, (short) 102, (short) 119, (short) 282, (short) 294, (short) 101, (short) 93, (short) 94, (short) 99, (short) 401, (short) 221, (short) 95, (short) 316, (short) 101, (short) 103, (short) 110, (short) 101, (short) 93, (short) 103, (short) 103, (short) 271, (short) 98, (short) 108, (short) 186, (short) 104, (short) 103, (short) 92, (short) 113, (short) 159, (short) 97, (short) 164, (short) 103, (short) 100, (short) 88, (short) 114, (short) 95, (short) 105, (short) 100, (short) 101, (short) 282, (short) 105, (short) 63, (short) 112, (short) 100, (short) 105, (short) 223, (short) 178, (short) 471, (short) 366, (short) 66, (short) 109, (short) 104, (short) 94, (short) 100, (short) 190, (short) 110, (short) 100, (short) 338, (short) 89, (short) 399, (short) 107, (short) 64, (short) 113, (short) 105, (short) 107, (short) 86, (short) 182, (short) 96, (short) 199, (short) 107, (short) 92, (short) 177, (short) 108, (short) 97, (short) 122, (short) 84, (short) 116, (short) 191, (short) 90, (short) 86, (short) 204, (short) 119, (short) 79, (short) 118, (short) 96, (short) 102, (short) 183, (short) 114, (short) 90, (short) 85, (short) 109, (short) 198, (short) 217, (short) 116, (short) 113, (short) 90, (short) 149, (short) 188, (short) 189, (short) 78, (short) 62, (short) 96, (short) 181, (short) 126, (short) 93, (short) 104, (short) 109, (short) 169, (short) 86, (short) 119, (short) 103, (short) 105, (short) 99, (short) 98, (short) 102, (short) 203, (short) 99, (short) 112, (short) 93, (short) 171, (short) 88, (short) 204, (short) 266, (short) 99, (short) 164, (short) 110, (short) 103, (short) 101, (short) 110, (short) 109, (short) 197, (short) 98, (short) 95, (short) 109, (short) 106, (short) 258, (short) 179, (short) 85, (short) 110, (short) 89, (short) 100, (short) 109, (short) 93, (short) 459, (short) 118),
                Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,269),
                Range.buildRange(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
    
    final SFFFlowgram FF585OX02FHO5X = new SFFFlowgram(
            new DefaultNucleotideEncodedGlyphs(
                    glyphFactory.getGlyphsFor(
                      "TCAGGGGGGCTTTGGATGTTGGAACCGAAAGGGTTTGAATTCAAACCCTTTCGGTTCCAACGATTTAAACCTACATCCATTTAACTCCAAATCCTGAACGGTTTCCAATAAACAACTTTACATTTGTGTAGCAAATTCCAGGCTATGTAAGAAAGGAGGACTCCACGGTGCACTGAGCGGGCTGGCAAGGC")),
                      new DefaultEncodedGlyphs<PhredQuality>(runLengthQualityCodec,
                              PhredQuality.valueOf(new byte[]{37, 37, 34, 15, 15, 22, 22, 22, 22, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 39, 39, 39, 39, 37, 38, 38, 38, 38, 38, 38, 39, 39, 39, 39, 39, 39, 39, 39, 39, 34, 36, 36, 40, 40, 40, 39, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 37, 34, 34, 34, 38, 38, 37, 37, 37, 37, 37, 38, 38, 38, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37})),
                        Arrays.<Short>asList((short) 104, (short) 104, (short) 96, (short) 568, (short) 104, (short) 291, (short) 203, (short) 101, (short) 98, (short) 102, (short) 197, (short) 202, (short) 186, (short) 192, (short) 105, (short) 286, (short) 285, (short) 283, (short) 107, (short) 183, (short) 186, (short) 92, (short) 282, (short) 301, (short) 290, (short) 105, (short) 201, (short) 208, (short) 198, (short) 199, (short) 101, (short) 103, (short) 100, (short) 295, (short) 292, (short) 198, (short) 104, (short) 105, (short) 103, (short) 101, (short) 100, (short) 190, (short) 98, (short) 291, (short) 204, (short) 102, (short) 107, (short) 202, (short) 297, (short) 96, (short) 201, (short) 100, (short) 100, (short) 201, (short) 102, (short) 203, (short) 299, (short) 201, (short) 185, (short) 101, (short) 292, (short) 100, (short) 202, (short) 106, (short) 284, (short) 106, (short) 102, (short) 101, (short) 279, (short) 90, (short) 109, (short) 101, (short) 103, (short) 104, (short) 105, (short) 101, (short) 286, (short) 207, (short) 198, (short) 98, (short) 205, (short) 107, (short) 100, (short) 104, (short) 99, (short) 103, (short) 104, (short) 193, (short) 100, (short) 296, (short) 201, (short) 102, (short) 209, (short) 102, (short) 99, (short) 101, (short) 196, (short) 104, (short) 105, (short) 199, (short) 101, (short) 104, (short) 94, (short) 104, (short) 107, (short) 100, (short) 108, (short) 98, (short) 95, (short) 102, (short) 300, (short) 101, (short) 104, (short) 202, (short) 103, (short) 193, (short) 203, (short) 98),
                        Range.buildRange(CoordinateSystem.RESIDUE_BASED, 5,172),
                        Range.buildRange(CoordinateSystem.RESIDUE_BASED, 0,0)
        );
}
