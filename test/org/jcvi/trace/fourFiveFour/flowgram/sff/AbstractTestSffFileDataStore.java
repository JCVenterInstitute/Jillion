/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public abstract class AbstractTestSffFileDataStore extends TestReadExampleSffFile{  

    private SffDataStore dataStore;
    @Override
    protected void parseSff(File file) throws Exception{
        dataStore = parseDataStore(file);
    }
    
    protected abstract SffDataStore parseDataStore(File f) throws Exception;
    
    @Override
    protected Flowgram getFlowgram(String id) throws TraceDecoderException, DataStoreException {
        return dataStore.get(id);
    }
    @Override
    protected int getNumberOfFlowgrams() throws TraceDecoderException, DataStoreException {
        return dataStore.size();
    }

}
