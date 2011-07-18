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
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.IOException;

import org.jcvi.common.core.seq.read.trace.sanger.phd.DefaultPhdFileDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdParser;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdTag;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.fileServer.FileServer;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFakePhdReadParser {

    private static final String PHD_FILE = "files/fake.phd";
    
    private static FileServer RESOURCES = new ResourceFileServer(TestFakePhdReadParser.class);

    @Test
    public void parseFakeReads() throws IOException, DataStoreException{
        DefaultPhdFileDataStore dataStore = new DefaultPhdFileDataStore();
        PhdParser.parsePhd(RESOURCES.getFileAsStream(PHD_FILE), dataStore);
        Phd fakePhd = dataStore.get("HA");
        assertIsFake(fakePhd);
        assertEquals(1738, fakePhd.getBasecalls().getLength());
        
        assertIsFake(dataStore.get("contig00001"));
        
        Phd realPhd = dataStore.get("FTF2AAH02G7TE3.6-91");
        assertEquals(
                "TCAGCGCGTAGTCGACGCAGCTGTCGTGTGCAGCAAAAGCAGGTAGATATTGAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCGTCCCGTCAGGCCCCCTCCAAGACCGCGATCGCGCAGAGACTTGTAAGAATGTGTTTGTCAGGGAAAAACGAAACCGACTCTTGTAGGCGGCTCATGGAAGTAGGGTCCGTAAAAGAACAAGAACCAACTCCTCGTTCACCTCCTGACTAAGGGGTAAGTTTTAGGTTAGTTTGTTGGTTCTACGCTCACCGTCGCCACGTGAGCGAGGACGTGCGACGCGTAGGTAACGGCCGTTTGTTCCGAAAACTAAGCCCGTTAACTTAGGGAAGTAGGGGTAGGTCCAACCAACATGGACGAGAGCGGTCGAACTACGTACAACGAAGGACTTAAAAGGGTAAAAGTAAACAATTACCTACTAGGGGCGGAAAAGAAGGTGGCGACCTACCTAGTTAAGTTTACTAACCTAGGTTGGCACTTAGTCACGCTGCGACTGGGTCCGTCCTATGTTACAACAGGAGTAGGGACGGTGTGACCACTGAGTAGGCGATTGGTCCCGAACGACGGACAGCGTGCGTACG" 
        		, 
        		NucleotideGlyph.convertToString(realPhd.getBasecalls().decode()));
    }

    private void assertIsFake(Phd fakePhd) {
        boolean isFake=false;
       for(PhdTag tag :fakePhd.getTags()){
           if("WR".equals(tag.getTagName())){
               if(tag.getTagValue().contains("type: fake")){
                   isFake=true;
                   break;
               }
           }
       }
       assertTrue(isFake);
    }
}
