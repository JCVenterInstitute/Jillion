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

import java.io.File;
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

	private final RandomAccessFile randomAccessFile;
	private long bytesRead=0;
	private Long length;
	private final boolean ownFile;
	/**
	 * Creates a new {@link RandomAccessFileInputStream}
	 * of the given file starting at the given position.
	 * Internally, a new {@link RandomAccessFile} is created
	 * and is seek'ed to the given startOffset
	 * before reading any bytes.  The internal 
	 * {@link RandomAccessFile} instance is managed by this
	 * class and will be closed when {@link #close()} is called.
	 * @param file the {@link File} to read.
	 * @param startOffset the start offset to start reading
	 * bytes from.
	 * @throws IOException if the given file does not exist 
	 * @throws IllegalArgumentException if the startOffset is less than 0.
	 */
	public RandomAccessFileInputStream(File file, long startOffset) throws IOException{
		assertStartOffValid(file, startOffset);
		this.randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.seek(startOffset);
		this.length = null;
		ownFile =true;
	}
	/**
	 * Creates a new {@link RandomAccessFileInputStream}
	 * of the given file starting at the given position
	 * but will only read the given length.
	 * Internally, a new {@link RandomAccessFile} is created
	 * and is seek'ed to the given startOffset
	 * before reading any bytes.  The internal 
	 * {@link RandomAccessFile} instance is managed by this
	 * class and will be closed when {@link #close()} is called.
	 * @param file the {@link File} to read.
	 * @param startOffset the start offset to start reading
	 * bytes from.
	 * @param length the maximum number of bytes to read from the file.
	 *  this inputStream will only as many bytes are in the file.
	 * @throws IOException if the given file does not exist
	 * @throws IllegalArgumentException if either startOffset or length are less than 0
	 * or if startOffset < file.length().
	 */
	public RandomAccessFileInputStream(File file, long startOffset, long length) throws IOException{
		assertStartOffValid(file, startOffset);
		if(length < 0){
			throw new IllegalArgumentException("length can not be less than 0");
		}
		this.randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.seek(startOffset);
		this.length = length;
		ownFile =true;
	}
	private void assertStartOffValid(File file, long startOffset) {
		if(startOffset < 0){
			throw new IllegalArgumentException("start offset can not be less than 0");
		}
		
		if(file.length() < startOffset){
			throw new IllegalArgumentException(
					String.format("invalid startOffset %d: file is only %d bytes" ,
							startOffset,
							file.length()));
		}
	}
	/**
	 * Creates a new RandomAccessFileInputStream that reads
	 * bytes from the given {@link RandomAccessFile}.
	 * Any external changes to the file pointer
	 * via {@link RandomAccessFile#seek(long)} or similar
	 * methods will also alter the subsequent bytes read
	 * by this {@link InputStream}.
	 * Closing the inputStream returned by this constructor
	 * DOES NOT close the {@link RandomAccessFile} which 
	 * must be closed separately by the caller.
	 * @param file the {@link RandomAccessFile} instance 
	 * to read as an {@link InputStream}; can not be null.
	 * @throws NullPointerException if file is null.
	 */
	public RandomAccessFileInputStream(RandomAccessFile file){
		if(file ==null){
			throw new NullPointerException("file can not be null");
		}
		this.randomAccessFile = file;
		length = null;
		ownFile =false;
	}
	
	@Override
	public synchronized int read() throws IOException {
		if(length !=null && bytesRead >=length){
			return -1;
		}
		int value = randomAccessFile.read();
		if(value !=-1){
			bytesRead++;
		}
		return value;

	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if(length != null && bytesRead >=length){
			return -1;
		}
		final int reducedLength;
		if(length !=null){
			reducedLength = Math.min(len, (int)(length - bytesRead));
		}else{
			reducedLength=len;
		}
		int numberOfBytesRead = randomAccessFile.read(b, off, reducedLength);
		bytesRead+=numberOfBytesRead;
		return numberOfBytesRead;
	}
	/**
	 * If this instance was creating
	 * using the {@link #RandomAccessFileInputStream(RandomAccessFile)}
	 * constructor, then this method does nothing- the RandomAccessFile
	 * will still be open.
	 * If constructed using {@link #RandomAccessFileInputStream(File, long)}
	 * or {@link #RandomAccessFileInputStream(File, long, long)},
	 * then the internal {@link RandomAccessFile} will be closed.
	 */
	@Override
	public void close() throws IOException {
		//if we created this randomaccessfile
		//then its our job to close it.
		if(ownFile){
			randomAccessFile.close();
		}
	}

	
}
