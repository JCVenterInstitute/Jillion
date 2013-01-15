package org.jcvi.jillion.assembly.ace;

import java.io.File;

import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AceFileContigDataStore;
import org.jcvi.jillion.assembly.ace.LargeAceFileDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;

public class TestLargeAceFileDataStore extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{

	public TestLargeAceFileDataStore() throws IOException, DataStoreException {
		super();
	}




	@Override
	protected AceFileContigDataStore createDataStoreFor(File aceFile)
			throws IOException {
		return LargeAceFileDataStore.create(getAceFile());
	}
}
