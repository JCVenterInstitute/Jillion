/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.IOException;
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
