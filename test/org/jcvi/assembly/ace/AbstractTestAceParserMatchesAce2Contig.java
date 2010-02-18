/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.junit.Test;

public abstract class  AbstractTestAceParserMatchesAce2Contig {
    List<AceContig> actualContigs;
    DefaultContigFileDataStore expectedContigDataStore;
    AbstractTestAceParserMatchesAce2Contig(String aceFile, String contigFile) throws IOException{
        this.expectedContigDataStore = new DefaultContigFileDataStore(AbstractTestAceParserMatchesAce2Contig.class.getResourceAsStream(contigFile));
        
        this.actualContigs = getContigList(new File(AbstractTestAceParserMatchesAce2Contig.class.getResource(aceFile).getFile()));

    }
    protected List<AceContig> getContigList(File aceFile) throws IOException {
        return new AceParser(new FileInputStream(aceFile)).parseContigsFrom();
    }
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
