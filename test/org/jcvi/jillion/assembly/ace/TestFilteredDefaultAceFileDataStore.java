package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AceFileContigDataStore;
import org.jcvi.jillion.assembly.ace.DefaultAceFileDataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;

public class TestFilteredDefaultAceFileDataStore extends AbstractTestFilteredAceDataStore{

	@Override
	protected AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException {
		return DefaultAceFileDataStore.create(aceFile, filter);
	}

}
