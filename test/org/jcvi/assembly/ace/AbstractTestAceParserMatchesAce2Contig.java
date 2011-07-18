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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;

public abstract class  AbstractTestAceParserMatchesAce2Contig {
    List<AceContig> actualContigs;
    DefaultContigFileDataStore expectedContigDataStore;
    ResourceFileServer RESOURCES = new ResourceFileServer(AbstractTestAceParserMatchesAce2Contig.class);
    
    AbstractTestAceParserMatchesAce2Contig(String aceFile, String contigFile) throws IOException{
        this.expectedContigDataStore = new DefaultContigFileDataStore(
        		RESOURCES.getFileAsStream(contigFile));
        
        this.actualContigs = getContigList(
        		RESOURCES.getFile(aceFile));       
    }
    protected abstract List<AceContig> getContigList(File aceFile) throws IOException;
    
    @Test
    public void assertParsedAceFileMatchedParsedContigFile() throws DataStoreException{
        assertContigsParsedCorrectly(actualContigs);
    }
   

    private void assertContigsParsedCorrectly(List<AceContig> actual) throws DataStoreException {
        assertEquals(expectedContigDataStore.size(), actual.size());
        for(AceContig actualAceContig : actual){
            Contig<PlacedRead> expectedContig = expectedContigDataStore.get(actualAceContig.getId());
            assertContigParsedCorrectly(expectedContig, actualAceContig);
        }
        
    }

    
    private void assertContigParsedCorrectly(Contig<PlacedRead> expected, Contig<? extends PlacedRead> actual) {
        assertEquals(expected.getId(), actual.getId()); 
        assertEquals(expected.getConsensus().decode(), actual.getConsensus().decode());
        assertEquals(expected.getId(),expected.getNumberOfReads(), actual.getNumberOfReads());
        for(PlacedRead expectedRead : expected.getPlacedReads()){
            assertPlacedReadParsedCorrectly(expectedRead, actual.getPlacedReadById(expectedRead.getId()));
        }
        
    }

    private void assertPlacedReadParsedCorrectly(PlacedRead expected,
            PlacedRead actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStart(), actual.getStart());
        assertEquals(expected.getEnd(), actual.getEnd());
        assertEquals(expected.getLength(), actual.getLength());
        assertEquals(expected.getId(),expected.getValidRange(), actual.getValidRange());
        assertEquals(expected.getEncodedGlyphs().decode(), actual.getEncodedGlyphs().decode());
        
    }
}
