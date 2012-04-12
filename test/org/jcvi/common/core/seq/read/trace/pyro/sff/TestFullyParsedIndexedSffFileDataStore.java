package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStore;

public class TestFullyParsedIndexedSffFileDataStore extends TestReadExampleSffFile{

    private FlowgramDataStore dataStore;
    @Override
    protected Flowgram getFlowgram(String id) throws Exception {
        return dataStore.get(id);
    }

    @Override
    protected int getNumberOfFlowgrams() throws Exception {
        return dataStore.size();
    }

    @Override
    protected void parseSff(File f) throws Exception {
    	dataStore = IndexedSffFileDataStore.createByFullyParsing(f);
    }
}
