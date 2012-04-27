package org.jcvi.common.core.assembly.ace;

import java.io.File;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;

public class TestLargeAceFileDataStore extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{

	public TestLargeAceFileDataStore() throws IOException, DataStoreException {
		super();
	}




	@Override
	protected AceContigDataStore createDataStoreFor(File aceFile)
			throws IOException {
		return LargeAceFileDataStore.create(getAceFile());
	}
}
