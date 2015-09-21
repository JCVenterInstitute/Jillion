package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestInputStreamSupplierWithEmptyFile {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Test
	public void emptyFileShouldNotThrowException() throws IOException{
		File file = tmpDir.newFile();
		
		InputStreamSupplier sut = InputStreamSupplier.forFile(file);
		try(InputStream in =sut.get()){
			assertEquals(-1, in.read());
		}
		
	}
}
