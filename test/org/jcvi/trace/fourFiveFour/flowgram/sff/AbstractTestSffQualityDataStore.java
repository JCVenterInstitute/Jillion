/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;

import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.junit.Test;

public abstract class AbstractTestSffQualityDataStore extends AbstractTestExampleSffFile{

    private final DefaultSffFileDataStore dataStore;
    
    {
        dataStore = new DefaultSffFileDataStore(runLengthQualityCodec);
        try {
            SffParser.parseSFF(SFF_FILE, dataStore);
        } catch (Exception e) {
            throw new IllegalStateException("could not parse sff file");
        } 
    }
    
    protected abstract QualityDataStore createSut(File sffFile) throws Exception;
    
    @Test
    public void datastoresMatch() throws Exception{
        QualityDataStore sut = createSut(SFF_FILE);
        assertEquals(sut.size(), dataStore.size());
        Iterator<String> ids = sut.getIds();
        while(ids.hasNext()){
            String id = ids.next();
            assertEquals(
                    dataStore.get(id).getQualities().decode(),
                    sut.get(id).decode());
        }
    }
}
