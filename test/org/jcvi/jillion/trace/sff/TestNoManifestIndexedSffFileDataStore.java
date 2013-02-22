package org.jcvi.jillion.trace.sff;

import java.io.File;

public class TestNoManifestIndexedSffFileDataStore extends AbstractTestSffFileDataStore{

    @Override
	protected File sffFileToUse() {
		return SFF_FILE_NO_INDEX;
	}

	@Override
	protected SffFileDataStore parseDataStore(File f) throws Exception {
		return CompletelyParsedIndexedSffFileDataStore.create(f);
	}


}
