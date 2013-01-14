package org.jcvi.common.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_toByteArray {

	@Test
	public void inputStream() throws IOException{
		byte[] bytes = new byte[]{1,2,3,4,5,6,7,8,9};
		InputStream in = new ByteArrayInputStream(bytes);
		byte[] actual =IOUtil.toByteArray(in);
		assertArrayEquals(bytes, actual);
	}
	@Test(expected = NullPointerException.class)
	public void nullInputStreamShouldThrowNPE() throws IOException{
		IOUtil.toByteArray((InputStream)null);
	}
}
