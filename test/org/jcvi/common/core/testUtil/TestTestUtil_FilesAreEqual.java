package org.jcvi.common.core.testUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.jcvi.common.core.io.IOUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
public class TestTestUtil_FilesAreEqual {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	@Test
	public void twoEmptyFilesAreEqual() throws IOException{
		File tmp1 = tmpDir.newFile();
		File tmp2 = tmpDir.newFile();
		assertTrue(TestUtil.contentsAreEqual(tmp1, tmp2));
	}
	
	@Test
	public void sameFileIsEqualToItself() throws IOException{
		File tmp = tmpDir.newFile();
		PrintWriter writer = new PrintWriter(tmp);
		writer.println("this is a test...");
		writer.close();
		assertTrue(TestUtil.contentsAreEqual(tmp, tmp));
	}
	
	@Test
	public void copyOfFileIsEqualToItself() throws IOException{
		File tmp = tmpDir.newFile();
		PrintWriter writer = new PrintWriter(tmp);
		writer.println("this is a test...");
		writer.close();
		
		File copy = tmpDir.newFile();
		OutputStream out=null;
		InputStream in=null;
		try{
		 out = new FileOutputStream(copy);
		 in = new FileInputStream(tmp);
		 IOUtil.copy(in, out);
		}finally{
			IOUtil.closeAndIgnoreErrors(in, out);
		}
		assertTrue(TestUtil.contentsAreEqual(tmp, tmp));
	}
}
