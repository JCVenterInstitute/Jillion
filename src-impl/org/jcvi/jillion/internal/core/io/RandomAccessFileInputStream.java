package org.jcvi.jillion.internal.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
/**
 * {@code RandomAccessFileInputStream} wraps
 * the given {@link RandomAccessFile} in an {@link InputStream}.
 * The RandomAccessFile <strong>may</strong> have its
 * position adjusted via {@link RandomAccessFile#seek(long)}
 * by external clients.
 * @author dkatzel
 *
 */
public class RandomAccessFileInputStream extends InputStream{

	private final RandomAccessFile randomAcessFile;
	
	public RandomAccessFileInputStream(RandomAccessFile file){
		if(file ==null){
			throw new NullPointerException("file can not be null");
		}
		this.randomAcessFile = file;
	}
	
	@Override
	public synchronized int read() throws IOException {
		return randomAcessFile.read();

	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		return randomAcessFile.read(b, off, len);
	}
	/**
	 * This method does nothing, the RandomAccessFile
	 * will still be open.
	 */
	@Override
	public void close() throws IOException {
		//no-op
	}

	
}
