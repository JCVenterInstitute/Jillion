package org.jcvi.common.io.zip;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jcvi.common.core.datastore.DataStoreException;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultZipDataStore extends AbstractTestZipDataStore{

	@Override
	protected ZipDataStore createZipDataStore(File file) throws IOException {
		return new DefaultZipDataStore(new ZipFile(file));
	}
	
	@Test(expected = NullPointerException.class)
	public void nullConstructorShouldThrowNPE(){
		new DefaultZipDataStore(null);
	}

	@Test
	public void errorWhileGettingEntryShouldThrowException() throws IOException{
		ZipFile mockFile = createMock(ZipFile.class);
		ZipEntry mockEntry = createMock(ZipEntry.class);
		expect(mockFile.getEntry("id")).andReturn(mockEntry);
		IOException expectedException = new IOException("expected");
		expect(mockFile.getInputStream(mockEntry)).andThrow(expectedException);
		replay(mockFile, mockEntry);
		
		DefaultZipDataStore datastore = new DefaultZipDataStore(mockFile);
		try {
			datastore.get("id");
			fail("should throw exception if zipEntry throws exception");
		} catch (DataStoreException e) {
			assertEquals(expectedException, e.getCause());
		}
	}
}
