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

package org.jcvi.jillion.trace.sanger.phd;

import java.io.FileNotFoundException;
import java.io.IOException;


import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.jillion.trace.sanger.phd.Phd;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStore;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStoreBuilder;
import org.jcvi.jillion.trace.sanger.phd.PhdParser;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestPhdDataStoreBuilderWith2InputFiles extends AbstractTestPhd{
    private static final ResourceHelper RESOURCE = new ResourceHelper(TestPhdDataStoreBuilderWith2InputFiles.class);
    
    @Test
    public void testCanReadMultiplePhdFiles() throws FileNotFoundException, IOException, DataStoreException{
        PhdDataStoreBuilder sut = DefaultPhdFileDataStore.createBuilder();
        PhdParser.parsePhd(RESOURCE.getFile(PHD_FILE), sut);
        PhdParser.parsePhd(RESOURCE.getFile("files/fake.phd"), sut);
        
        PhdDataStore datastore = sut.build();
        assertEquals(4, datastore.getNumberOfRecords());

        phdRecordMatchesExpected(datastore.get("1095595674585"));
        
        Phd realPhd = datastore.get("FTF2AAH02G7TE3.6-91");
        assertEquals(
                "TCAGCGCGTAGTCGACGCAGCTGTCGTGTGCAGCAAAAGCAGGTAGATATTGAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCGTCCCGTCAGGCCCCCTCCAAGACCGCGATCGCGCAGAGACTTGTAAGAATGTGTTTGTCAGGGAAAAACGAAACCGACTCTTGTAGGCGGCTCATGGAAGTAGGGTCCGTAAAAGAACAAGAACCAACTCCTCGTTCACCTCCTGACTAAGGGGTAAGTTTTAGGTTAGTTTGTTGGTTCTACGCTCACCGTCGCCACGTGAGCGAGGACGTGCGACGCGTAGGTAACGGCCGTTTGTTCCGAAAACTAAGCCCGTTAACTTAGGGAAGTAGGGGTAGGTCCAACCAACATGGACGAGAGCGGTCGAACTACGTACAACGAAGGACTTAAAAGGGTAAAAGTAAACAATTACCTACTAGGGGCGGAAAAGAAGGTGGCGACCTACCTAGTTAAGTTTACTAACCTAGGTTGGCACTTAGTCACGCTGCGACTGGGTCCGTCCTATGTTACAACAGGAGTAGGGACGGTGTGACCACTGAGTAGGCGATTGGTCCCGAACGACGGACAGCGTGCGTACG" 
                , 
                realPhd.getNucleotideSequence().toString());
    }

    protected void phdRecordMatchesExpected(Phd actual) {
        assertEquals(expectedQualities, actual.getQualitySequence());        
        assertEquals(expectedPositions, actual.getPositionSequence());      
        assertEquals(expectedBasecalls, actual.getNucleotideSequence().toString());
        assertEquals(expectedProperties, actual.getComments());
    }
    
}
