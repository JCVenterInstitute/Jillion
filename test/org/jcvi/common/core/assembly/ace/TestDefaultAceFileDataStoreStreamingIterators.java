package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

public class TestDefaultAceFileDataStoreStreamingIterators extends AbstractTestAceDataStoreStreamingIterators{

	@Override
	protected AceFileContigDataStore createDataStore(File aceFile)
			throws IOException {
		return DefaultAceFileDataStore.create(aceFile);
	}

}
