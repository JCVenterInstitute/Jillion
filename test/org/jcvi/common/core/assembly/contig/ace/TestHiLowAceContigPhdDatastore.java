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

package org.jcvi.common.core.assembly.contig.ace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.assembly.contig.ace.HiLowAceContigPhdDatastore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificialPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.symbol.qual.DefaultEncodedPhredGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestHiLowAceContigPhdDatastore {

    HiLowAceContigPhdDatastore sut;
    ResourceFileServer rs = new ResourceFileServer(TestHiLowAceContigPhdDatastore.class);
    @Before
    public void setup() throws IOException{
        File ace = rs.getFile("files/sample.ace");
        sut = HiLowAceContigPhdDatastore.create(ace, "Contig1");
    }
    
    @After
    public void tearDown() throws IOException{
        sut.close();
    }
    @Test
    public void forwardRead() throws DataStoreException{
        List<PhredQuality> expectedQualities = new ArrayList<PhredQuality>();
        addLowQualities(expectedQualities,4);
        addHighQuality(expectedQualities);
        addLowQualities(expectedQualities,"gtgagatcatcctga".length());
        addHighQualities(expectedQualities, "AGTGGAGGGCATGGGGCTTGGCTGGGCTTAGAGCTAACATACACAGGATGCTGAAAAAGAACAACACAA".length());
        addLowQualities(expectedQualities,3);
        addHighQualities(expectedQualities, "GTGTGGAGCAAAGGAAAGGGAAATCAGCTTGAAGCTGATGTTAGTGTGCTTGGGCTGAGTACAGCCATG".length());
        addLowQualities(expectedQualities,4);
        addHighQualities(expectedQualities, "CAGTTGAGGCACGGTTGGCTCCCCATGGGCAAGATCCCTCCTGGCCCATCTCTCCTCTTATTCTCTATCCCTTCCCCAGGTCCCTGCCTTAGAGGTTTCACCAGAGCACAGCTCCTG".length());
        addLowQualities(expectedQualities,"cctgtggcca".length());
        addHighQualities(expectedQualities,"AAACAGTATTTGGCCACTCAC".length());
        addLowQuality(expectedQualities);
        addHighQualities(expectedQualities,2);
        addLowQualities(expectedQualities,5);
        addHighQualities(expectedQualities,"TGTCAGC".length());
        addLowQualities(expectedQualities, "atcca".length());
        addHighQuality(expectedQualities);
        addLowQualities(expectedQualities, 4);
        addHighQuality(expectedQualities);
        addLowQuality(expectedQualities);
        addHighQuality(expectedQualities);
        addLowQualities(expectedQualities, "ccacatctcacaaccctgggcagcagagaaggggtttaaaggccaggggggtattaagccgaaggaggttttggaaacaccaaggggggtcagaccccaacgccagtttccccaaaaaggggcattcaaatttttttctcagagattttctttccttttttgggccccgggaaccttttttaaaaaatgggggattgggcccccttggcccccctc".length());
        
        
        Phd expected = new ArtificialPhd("K26-217c", 
                NucleotideSequenceFactory.create("tcccCgtgagatcatcctgaAGTGGAGGGCATGGGGCTTGGCTGGGCTTA" +
                "GAGCTAACATACACAGGATGCTGAAAAAGAACAACACAAgntGTGTGGAG" +
                "CAAAGGAAAGGGAAATCAGCTTGAAGCTGATGTTAGTGTGCTTGGGCTGA" +
                "GTACAGCCATGctntCAGTTGAGGCACGGTTGGCTCCCCATGGGCAAGAT" +
                "CCCTCCTGGCCCATCTCTCCTCTTATTCTCTATCCCTTCCCCAGGTCCCT" +
                "GCCTTAGAGGTTTCACCAGAGCACAGCTCCTGcctgtggccaAAACAGTA" +
                "TTTGGCCACTCACcGAcccagTGTCAGCatccaGatggGtTccacatct" +
                "cacaaccctgggcagcagagaaggggtttaaaggccaggggggtatta" +
                "agccgaaggaggttttggaaacaccaaggggggtcagaccccaacgc" +
                "cagtttccccaaaaaggggcattcaaatttttttctcagagattttcttt" +
                "ccttttttgggccccgggaaccttttttaaaaaatgggggattgggcccc" +
                "cttggcccccctc"),
                
                new EncodedQualitySequence(new DefaultEncodedPhredGlyphCodec(), expectedQualities),
                19);
        Phd actual = sut.get("K26-217c");
        assertEquals(expected.getBasecalls().asList(),actual.getBasecalls().asList());
        assertEquals(expected.getQualities().asList(),actual.getQualities().asList());
    }

    
    @Test
    public void reverseRead() throws DataStoreException{
        String id= "K26-766c";
        String basecalls = 
            "gaataattggaatcacggcaaaaatttggggacaaatattatttccaaaa" +
            "ttcccccagcaatcacacaggccctcaagcccatcaactcggtcattcac" +
            "cgattttcctaaatcaagggtattagcttgctgggcttacacctaacat" +
            "acacagcatgctcaatgagaAcaatacgagctgtgtggagcacaggaagg" +
            "ggaAAtcagcctgaagctgctgttagtgtgcttggctgAGTACAGCcaT" +
            "GCTctCAGTTgaggcAcggTTGGCTCCCCATGGgCAAGATCCCTCCTggC" +
            "CCATCTCTCCTCTTaTTCTCTATCCCTTCCCCAGGTCCCTGCCTTAGagg" +
            "tttCACCAGAGCACAGCTCCTGCCTGTGGCCAAAACAGTATTTGGCCACT" +
            "CACCGACCCAGTGTCAGCATCCAGATGGGTTCCACATCTCACAACCCT" +
            "GAGCAGCAGAGAAGGGTTTGAAAGGCCAGGGGAGAATGAAGACGAAGGA" +
            "GGTGTTGGCAACAACACAGAGAGTCAGCAGCCAGAACGCCAGGTATC" +
            "CACACACATAagaCATtctaAATTTTTACTCAAacgatcCccggaaccac" +
            "acg";
        
        NucleotideSequence reverseComplimented = NucleotideSequenceFactory.create(Nucleotides.reverseCompliment(
                                                        Nucleotides.parse(basecalls)));
    
        List<PhredQuality> expectedQualities = new ArrayList<PhredQuality>();
        addLowQualities(expectedQualities, "gaataattggaatcacggcaaaaatttggggacaaatattatttccaaaattcccccagcaatcacacaggccctcaagcccatcaactcggtcattcaccgattttcctaaatcaagggtattagcttgctgggcttacacctaacatacacagcatgctcaatgaga".length());
        addHighQuality(expectedQualities);
        addLowQualities(expectedQualities, "caatacgagctgtgtggagcacaggaagggga".length());
        addHighQualities(expectedQualities, 2);
        addLowQualities(expectedQualities, "tcagcctgaagctgctgttagtgtgcttggctg".length());
        addHighQualities(expectedQualities, "AGTACAGC".length());
        addLowQualities(expectedQualities, 2);
        addHighQualities(expectedQualities, 4);
        addLowQualities(expectedQualities, 2);
        addHighQualities(expectedQualities, 5);
        addLowQualities(expectedQualities, 5);
        addHighQuality(expectedQualities);
        addLowQualities(expectedQualities, 3);
        addHighQualities(expectedQualities, "TTGGCTCCCCATGG".length());
        addLowQuality(expectedQualities);
        addHighQualities(expectedQualities, "CAAGATCCCTCCT".length());
        addLowQualities(expectedQualities, 2);
        addHighQualities(expectedQualities, "CCCATCTCTCCTCTT".length());
        addLowQuality(expectedQualities);
        addHighQualities(expectedQualities, "TTCTCTATCCCTTCCCCAGGTCCCTGCCTTAG".length());
        addLowQualities(expectedQualities, 6);
        addHighQualities(expectedQualities, "CACCAGAGCACAGCTCCTGCCTGTGGCCAAAACAGTATTTGGCCACTCACCGACCCAGTGTCAGCATCCAGATGGGTTCCACATCTCACAACCCTGAGCAGCAGAGAAGGGTTTGAAAGGCCAGGGGAGAATGAAGACGAAGGAGGTGTTGGCAACAACACAGAGAGTCAGCAGCCAGAACGCCAGGTATCCACACACATA".length());
        addLowQualities(expectedQualities, 3);
        addHighQualities(expectedQualities, 3);
        addLowQualities(expectedQualities, 4);
        addHighQualities(expectedQualities, "AATTTTTACTCAA".length());
        addLowQualities(expectedQualities, "acgatc".length());
        addHighQuality(expectedQualities);
        addLowQualities(expectedQualities, "ccggaaccacacg".length());
        
        
        Collections.reverse(expectedQualities);
        Phd expected = new ArtificialPhd(id,
                
                reverseComplimented,
                new EncodedQualitySequence(new DefaultEncodedPhredGlyphCodec(), 
                        expectedQualities),
                19);
        Phd actual = sut.get(id);
        assertEquals(expected.getBasecalls().asList(),actual.getBasecalls().asList());
        assertEquals(expected.getQualities().asList(),actual.getQualities().asList());
    }
    /**
     * @param expectedQualities
     * @param i
     */
    private void addLowQualities(List<PhredQuality> expectedQualities, int numberOfQualities) {
        for(int i=0; i<numberOfQualities; i++){
            expectedQualities.add(HiLowAceContigPhdDatastore.DEFAULT_LOW_QUALITY);
        }        
    }
    private void addLowQuality(List<PhredQuality> expectedQualities){
        addLowQualities(expectedQualities,1);
    }
    private void addHighQualities(List<PhredQuality> expectedQualities, int numberOfQualities) {
        for(int i=0; i<numberOfQualities; i++){
            expectedQualities.add(HiLowAceContigPhdDatastore.DEFAULT_HIGH_QUALITY);
        }
    }
    
    private void addHighQuality(List<PhredQuality> expectedQualities){
        addHighQualities(expectedQualities,1);
    }
}
