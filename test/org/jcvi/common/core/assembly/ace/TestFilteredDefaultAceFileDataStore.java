package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;

public class TestFilteredDefaultAceFileDataStore extends AbstractTestFilteredAceDataStore{

	@Override
	protected AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException {
		return DefaultAceFileDataStore.create(aceFile, filter);
	}

}
