package org.jcvi.common.core.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.common.core.io.IOUtil;
/**
 * {@code ByteBufferInputStream} is an {@link InputStream}
 * that delegates reads to a {@link ByteBuffer}.
 * @author dkatzel
 *
 */
public final class ByteBufferInputStream extends InputStream{

	private final ByteBuffer buffer;
	
	public ByteBufferInputStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public synchronized int read() throws IOException {
		if(!buffer.hasRemaining()){
			return -1;
		}
		//need to return byte as unsigned
		return IOUtil.toUnsignedByte(buffer.get());

	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if(!buffer.hasRemaining()){
			return -1;
		}
		int bytesRead = Math.min(len, buffer.remaining());
		buffer.get(b,off,bytesRead);
		return bytesRead;
	}

}
