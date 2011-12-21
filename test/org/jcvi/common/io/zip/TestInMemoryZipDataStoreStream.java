package org.jcvi.common.io.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestInMemoryZipDataStoreStream extends AbstractTestZipDataStore{

	@Override
	protected ZipDataStore createZipDataStore(File file) throws IOException {
		return InMemoryZipDataStore.createInMemoryZipDataStoreFrom(new FileInputStream(file));
	}

}
