package org.jcvi.jillion.trace.sff;

import java.io.File;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.trace.sff.Flowgram;
import org.jcvi.jillion.trace.sff.FlowgramDataStore;
import org.jcvi.jillion.trace.sff.IndexedSffFileDataStore;

public class TestFullyParsedIndexedSffFileDataStore extends TestReadExampleSffFile{

    private FlowgramDataStore dataStore;
    @Override
    protected Flowgram getFlowgram(String id) throws Exception {
        return dataStore.get(id);
    }

    @Override
    protected long getNumberOfFlowgrams() throws Exception {
        return dataStore.getNumberOfRecords();
    }

    @Override
    protected void parseSff(File f) throws Exception {
    	dataStore = IndexedSffFileDataStore.createByFullyParsing(f,DataStoreFilters.alwaysAccept());
    }
}
