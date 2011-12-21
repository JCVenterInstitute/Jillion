package org.jcvi.common.io.zip;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class TestDefaultZipDataStore extends AbstractTestZipDataStore{

	@Override
	protected ZipDataStore createZipDataStore(File file) throws IOException {
		return new DefaultZipDataStore(new ZipFile(file));
	}

}
