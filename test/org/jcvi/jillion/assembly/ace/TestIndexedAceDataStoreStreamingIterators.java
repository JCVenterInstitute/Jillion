package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AceFileContigDataStore;
import org.jcvi.jillion.assembly.ace.IndexedAceFileDataStore;

public class TestIndexedAceDataStoreStreamingIterators extends AbstractTestAceDataStoreStreamingIterators{

	@Override
	protected AceFileContigDataStore createDataStore(File aceFile) throws IOException {
		return IndexedAceFileDataStore.create(aceFile);
	}

}
