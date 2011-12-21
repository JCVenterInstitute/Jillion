package org.jcvi.common.io.zip;

import java.io.File;
import java.io.IOException;

public class TestInMemoryZipDataStore extends AbstractTestZipDataStore{

	@Override
	protected ZipDataStore createZipDataStore(File file) throws IOException {
		return InMemoryZipDataStore.createInMemoryZipDataStoreFrom(file);
	}

}
