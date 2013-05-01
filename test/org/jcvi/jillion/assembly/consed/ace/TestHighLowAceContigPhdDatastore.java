/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.assembly.consed.ace.HighLowAceContigPhdDatastore;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestHighLowAceContigPhdDatastore {

	PhdDataStore sut;
    ResourceHelper rs = new ResourceHelper(TestHighLowAceContigPhdDatastore.class);
    @Before
    public void setup() throws IOException{
        File ace = rs.getFile("files/sample.ace");
        sut = HighLowAceContigPhdDatastore.create(ace, "Contig1");
    }
    
    @After
    public void tearDown() throws IOException{
        sut.close();
    }
    @Test
    public void forwardRead() throws DataStoreException{
        QualitySequenceBuilder expectedQualities = new QualitySequenceBuilder();
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
        
        
        Phd expected = new PhdBuilder("K26-217c", 
                new NucleotideSequenceBuilder("tcccCgtgagatcatcctgaAGTGGAGGGCATGGGGCTTGGCTGGGCTTA" +
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
                "cttggcccccctc").build(),
                
                expectedQualities.build())
        			.fakePeaks()
        			.build();

        Phd actual = sut.get("K26-217c");
        assertEquals(expected.getNucleotideSequence(),actual.getNucleotideSequence());
        assertEquals(expected.getQualitySequence(),actual.getQualitySequence());
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
        
        NucleotideSequence reverseComplimented = new NucleotideSequenceBuilder(basecalls)
        											.reverseComplement()
        											.build();
    
        QualitySequenceBuilder expectedQualities = new QualitySequenceBuilder();
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
        
        expectedQualities.reverse();
        Phd expected = new PhdBuilder(id,                
                reverseComplimented,expectedQualities.build())
                .fakePeaks()
    			.build();
        Phd actual = sut.get(id);
        assertEquals(expected.getNucleotideSequence(),actual.getNucleotideSequence());
        assertEquals(expected.getQualitySequence(),actual.getQualitySequence());
    }

    private void addLowQualities(QualitySequenceBuilder builder, int numberOfQualities) {
    	byte[] array = new byte[numberOfQualities];
    	Arrays.fill(array, HighLowAceContigPhdDatastore.DEFAULT_LOW_QUALITY.getQualityScore());
        builder.append(array);       
    }
    private void addLowQuality(QualitySequenceBuilder builder){
       builder.append(HighLowAceContigPhdDatastore.DEFAULT_LOW_QUALITY.getQualityScore());
    }
    private void addHighQualities(QualitySequenceBuilder builder, int numberOfQualities) {
    	byte[] array = new byte[numberOfQualities];
    	Arrays.fill(array, HighLowAceContigPhdDatastore.DEFAULT_HIGH_QUALITY.getQualityScore());
        builder.append(array); 
    }
    
    private void addHighQuality(QualitySequenceBuilder builder){
    	builder.append(HighLowAceContigPhdDatastore.DEFAULT_HIGH_QUALITY.getQualityScore());
    }
}
