package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.trace.sff.Flowgram;
import org.jcvi.jillion.trace.sff.FlowgramDataStore;
import org.jcvi.jillion.trace.sff.Indexed454SffFileDataStore;
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
