/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.util.Iterator;

import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestSffNucleotideDataStore extends AbstractTestExampleSffFile{

    private final DefaultSffFileDataStore dataStore;
    
    {
        dataStore = new DefaultSffFileDataStore(runLengthQualityCodec);
        try {
            SffParser.parseSFF(SFF_FILE, dataStore);
        } catch (Exception e) {
            throw new IllegalStateException("could not parse sff file");
        } 
    }
    
    protected abstract NucleotideDataStore createSut(File sffFile) throws Exception;
    
    @Test
    public void datastoresMatch() throws Exception{
        NucleotideDataStore sut = createSut(SFF_FILE);
        assertEquals(sut.size(), dataStore.size());
        Iterator<String> ids = sut.getIds();
        while(ids.hasNext()){
            String id = ids.next();
            assertEquals(sut.get(id).decode(),
                    dataStore.get(id).getBasecalls().decode());
        }
    }
    
}
