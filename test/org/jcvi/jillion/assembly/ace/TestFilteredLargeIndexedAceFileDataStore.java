package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AceFileContigDataStore;
import org.jcvi.jillion.assembly.ace.LargeAceFileDataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;

public class TestFilteredLargeIndexedAceFileDataStore extends AbstractTestFilteredAceDataStore{

	@Override
	protected AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException {
		return LargeAceFileDataStore.create(aceFile, filter);
	}

}
