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

package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.FileNotFoundException;
import java.io.IOException;


import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhdDataStoreBuilderWith2InputFiles extends AbstractTestPhd{
    private static final ResourceFileServer RESOURCE = new ResourceFileServer(TestPhdDataStoreBuilderWith2InputFiles.class);
    
    @Test
    public void testCanReadMultiplePhdFiles() throws FileNotFoundException, IOException, DataStoreException{
        PhdDataStoreBuilder sut = DefaultPhdFileDataStore.createBuilder();
        PhdParser.parsePhd(RESOURCE.getFile(PHD_FILE), sut);
        PhdParser.parsePhd(RESOURCE.getFile("files/fake.phd"), sut);
        
        PhdDataStore datastore = sut.build();
        assertEquals(4, datastore.size());

        phdRecordMatchesExpected(datastore.get("1095595674585"));
        
        Phd realPhd = datastore.get("FTF2AAH02G7TE3.6-91");
        assertEquals(
                "TCAGCGCGTAGTCGACGCAGCTGTCGTGTGCAGCAAAAGCAGGTAGATATTGAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCGTCCCGTCAGGCCCCCTCCAAGACCGCGATCGCGCAGAGACTTGTAAGAATGTGTTTGTCAGGGAAAAACGAAACCGACTCTTGTAGGCGGCTCATGGAAGTAGGGTCCGTAAAAGAACAAGAACCAACTCCTCGTTCACCTCCTGACTAAGGGGTAAGTTTTAGGTTAGTTTGTTGGTTCTACGCTCACCGTCGCCACGTGAGCGAGGACGTGCGACGCGTAGGTAACGGCCGTTTGTTCCGAAAACTAAGCCCGTTAACTTAGGGAAGTAGGGGTAGGTCCAACCAACATGGACGAGAGCGGTCGAACTACGTACAACGAAGGACTTAAAAGGGTAAAAGTAAACAATTACCTACTAGGGGCGGAAAAGAAGGTGGCGACCTACCTAGTTAAGTTTACTAACCTAGGTTGGCACTTAGTCACGCTGCGACTGGGTCCGTCCTATGTTACAACAGGAGTAGGGACGGTGTGACCACTGAGTAGGCGATTGGTCCCGAACGACGGACAGCGTGCGTACG" 
                , 
                Nucleotides.convertToString(realPhd.getBasecalls().asList()));
    }

    protected void phdRecordMatchesExpected(Phd actual) {
        assertEquals(expectedQualities, actual.getQualities().asList());        
        assertEquals(expectedPositions, actual.getPeaks().getData().asList());      
        assertEquals(expectedBasecalls, Nucleotides.convertToString(actual.getBasecalls().asList()));
        assertEquals(expectedProperties, actual.getComments());
    }
    
}
