package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;

import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;

public class TestNoXMLManifestIndexedSffFileDataStore extends TestReadExampleSffFile{

    @Override
	protected File sffFileToUse() {
		return SFF_FILE_NO_XML;
	}

	private SffDataStore dataStore;
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
    	dataStore = Indexed454SffFileDataStore.create(f);
    }


}
