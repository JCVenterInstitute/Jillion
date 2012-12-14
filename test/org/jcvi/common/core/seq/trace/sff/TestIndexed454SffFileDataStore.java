package org.jcvi.common.core.seq.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStore;
import org.jcvi.common.core.seq.trace.sff.Indexed454SffFileDataStore;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIndexed454SffFileDataStore extends TestReadExampleSffFile{

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
    	dataStore = Indexed454SffFileDataStore.create(f);
    }
    
    @Test
    public void noIndexInSffShouldMakeCreateReturnNull() throws IOException{
    	assertNull(Indexed454SffFileDataStore.create(SFF_FILE_NO_INDEX));
    }

}
