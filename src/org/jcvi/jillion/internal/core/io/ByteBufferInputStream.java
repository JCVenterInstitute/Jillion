/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;
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
