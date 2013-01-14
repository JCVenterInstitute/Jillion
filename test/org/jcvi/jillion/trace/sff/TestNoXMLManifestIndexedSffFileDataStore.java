package org.jcvi.jillion.trace.sff;

import java.io.File;

import org.jcvi.jillion.trace.sff.Flowgram;
import org.jcvi.jillion.trace.sff.FlowgramDataStore;
import org.jcvi.jillion.trace.sff.Indexed454SffFileDataStore;

public class TestNoXMLManifestIndexedSffFileDataStore extends TestReadExampleSffFile{

    @Override
	protected File sffFileToUse() {
		return SFF_FILE_NO_XML;
	}

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


}
