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
