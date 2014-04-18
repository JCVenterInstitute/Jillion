/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.sam.IndexerCallback;
/**
 * {@code BgzfOutputStream} is an {@link OutputStream}
 * implementation that writes out data as
 * a set of concatenated GZIP blocks with the 
 * appropriate Extra fields set in the header
 * to conform to the BGZF format
 * specified in the BAM file format specification.
 * <p>
 * NOT THREAD SAFE
 * </p>
 * @author dkatzel
 *
 */
final class BgzfOutputStream extends OutputStream{
	
	/**
	 * Max compressed block size should never be > max uncompressed block size.
	 * Since the maximum size that an uncompressed GZIP block
	 * can be in a BGZF file is imposed
	 * by the virtual file offsets  when indexing
	 * BAM files whose largest offset is (1<<16) -1
	 * we can make the max length 1<<16.
	 */
	private static final int MAX_COMPRESSED_BLOCK_SIZE = 1 <<16;
	/**
	 * Size of BGZF block not counting the actual compressed data
	 */
	private static final int BGZF_BLOCK_FULL_HEADER_LENGTH = 26;
	/**
	 * Maximum size that an uncompressed GZIP block
	 * can be in a BGZF file. This limit is imposed
	 * by the virtual file offsets when indexing
	 * BAM files to at most (1<<16) -1.
	 */
	private static final int MAX_UNCOMPRESSED_BLOCK_SIZE = MAX_COMPRESSED_BLOCK_SIZE - BGZF_BLOCK_FULL_HEADER_LENGTH;
	
	/**
	 * Use the compression level SAMTool's Picard uses,
	 * not sure why they use {@value} instead of the default.
	 */
	private static final int GZIP_COMPRESSION_LEVEL = 5;
	
	/**
	 * Common header to all BGZF encoded blocks
	 * that contains most of the GZIP header specified
	 * in RFC1952 as well as most of the extra fields
	 * required by BGZF encoded data.
	 * To make a valid BGZF block, append to this header:
	 * <ol>
	 * <li>The total length of this block minus 1</li>
	 * <li> the GZIP compressed data</li>
	 * <li> the GZIP compressed data which should be the total length of this block minus 20</li>
	 * <li>the CRC-32 value of the compressed data as an uint32</li>
	 * <li>The length of the uncompressed data as an uint32</li>
	 * </ol>
	 */
	private static final byte[] BGZF_BLOCK_HEADER = new byte[]{
		
		0x1F , (byte)0x8B , 		//gzip ID
		0x08 ,						//compressionMode
		0x04 ,						//Flag bits set indicating Extra fields present
		0x00 , 0x00 ,0x00 ,0x00 ,	//unknown timestamp
		0x00 ,						//no extra flags
		(byte)0xff ,				//unknown OS
		0x06 ,	0x00 ,				//Extra field length = 6 bytes (little Endian)
		0x42 ,  0x43 ,				//BGZF id
		0x02 ,	0x00 ,				//subfield length = 2 bytes (little Endian)
	};
	/**
	 * An End of File Trailer block written to the end
	 * of BAM files so that unintended file truncation can be easily detected.
	 */
	private static final byte[] EOF_MARKER;	
	
	private final CRC32 currentCrc32 = new CRC32();
	/**
	 * The number of bytes written to our uncompressedBuffer
	 * so far that have not yet been flushed.
	 * This value is updated on calls to write().
	 */
	private int currentUsedBufferLength=0;
	/**
	 * The amount of bytes written out so far to the 
	 * wrapped OutputStream in the form of BGZF blocks.
	 * This value is updated on calls to {@link #flush()}.
	 */
	private long compressedBytesWrittenSoFar=0;
	/**
	 * Our buffer storing the bytes to be flushed to our
	 * wrapped outputStream.
	 */
	private final byte[] uncompressedBuffer = new byte[MAX_UNCOMPRESSED_BLOCK_SIZE];
	/**
	 * Temparary buffer to store our compressed version of the 
	 * uncompressed data when we are flushing.
	 * This can be made into a local variable of {@link #flush()}
	 * but this way we don't have to worry about
	 * extra allocations.
	 */
	private final byte[] compressedBuffer = new byte[MAX_COMPRESSED_BLOCK_SIZE];
	/**
	 * The {@link OutputStream} we flush
	 * the compressed data to.
	 */
	private final OutputStream out;
	/**
	 * Reference to a {@link IndexerCallback}
	 * if an observer is interested in where
	 * each call to write() is written in the wrapped
	 * outputStream.  May be {@code null}
	 * if there is no callback.
	 */
	private final IndexerCallback callback;
	/**
	 * Used by {@link #write(int)}
	 * to reduce extra variable creation
	 * by always reusing the same array.
	 * @see #write(int)
	 */
	private final byte[] singleByteArray = new byte[1];
	

	static{
		EOF_MARKER = new byte[28];
		//start with same BGZF block header
		System.arraycopy(BGZF_BLOCK_HEADER, 0, EOF_MARKER,  0, BGZF_BLOCK_HEADER.length);
		//the rest of the block is all zeros except for
		//these two bytes
		EOF_MARKER[16] = 0x1b;  	//BSIZE - 1 = EOF_MARKER.length - 1
		EOF_MARKER[18] = 0x03;		//first byte of compressed data ?
	}
	
	/**
	 * Create a new {@link BgzfOutputStream}
	 * that will write BGZF encoded data to the given
	 * outputStream.
	 * @param outputBam the {@link File} to write to;
	 * can not be null.  If the file, or any parent directories
	 * do not exist, then they will be created.
	 * @param callback the {@link IndexerCallback} to call back to
	 * on during when writing to this {@link BgzfOutputStream};
	 * if {@code null} then no callbacks will be called.
	 * 
	 * @throws NullPointerException if out is null.
	 * @throws IOException if there is a problem creating the output file.
	 */
	public BgzfOutputStream(File outputBam, IndexerCallback callback) throws IOException {
		if(outputBam ==null){
			throw new NullPointerException("output can not be null");
		}
		IOUtil.mkdirs(outputBam.getParentFile());
		this.out = new BufferedOutputStream(new FileOutputStream(outputBam), MAX_COMPRESSED_BLOCK_SIZE);
		this.callback = callback;
	}
	
	/**
	 * {@inheritDoc}.
	 */
	@SuppressWarnings("PMD.SingularField")
	@Override
	public void write(int b) throws IOException {
		singleByteArray[0] = (byte)b;
		handleWrite(singleByteArray, 0, 1);
		
	}
	
	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void write(byte[] b) throws IOException {
		handleWrite(b, 0, b.length);
	}
	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		handleWrite(b, off, len);
	}
	/**
	 * Take the given bytes to be written and
	 * try to write them to our in memory buffer.
	 * If the in memory buffer fills up, 
	 * then compress in memory buffer and write
	 * the compressed data to the wrapped
	 * OutputStream as one or more BGZF blocks
	 * and call the callback if there is one.
	 * 
	 * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
	 * @throws IOException if there is a problem encoding or writing out the data.
	 */
	private void handleWrite(byte[] b, int off, int bytesToWriteLength) throws IOException{
		if(bytesToWriteLength <0){
			//follow OutputStream spec and throw Exception
			throw new IndexOutOfBoundsException("length can not be negative : "+ bytesToWriteLength);
		}
		if(callback ==null){
			handleWriteBody(b, off, bytesToWriteLength);
		}else{
			//get before and after values
			//for our callback
			VirtualFileOffset start = getVirtualFileOffset();
			
			handleWriteBody(b, off, bytesToWriteLength);
			
			VirtualFileOffset end = getVirtualFileOffset();
			
			
			callback.encodedIndex(start, end);
		}
		
	}
	
	
	public VirtualFileOffset getVirtualFileOffset(){
		return VirtualFileOffset.create(compressedBytesWrittenSoFar, currentUsedBufferLength);
	}
	/**
	 * Take the given bytes to be written and
	 * try to write them to our in memory buffer.
	 * If the in memory buffer fills up, 
	 * then compress in memory buffer and write
	 * the compressed data to the wrapped
	 * OutputStream as one or more BGZF blocks.
	 * 
	 * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
	 * @throws IOException if there is a problem encoding or writing out the data.
	 */
	private void handleWriteBody(byte[] b, int off, int bytesToWriteLength) throws IOException{
		//this method is only called by handleWrite()
		//which has done all the range checks already
		//so we don't have to.
		
		
		//loop through the data in chunks that fit
		//into the current uncompressedBuffer
		//and write potentially multiple concatenated blocks.
		//There isn't anything in the BGZF spec
		//that says
		//a record can't span multiple blocks
		//so I think we can arbitrarily 
		//break blocks whenever we need.
		int currentOffset = off;
		int bytesLeftToWrite = bytesToWriteLength;
		while(bytesLeftToWrite > 0){
			int bytesFreeInBuffer = MAX_UNCOMPRESSED_BLOCK_SIZE - currentUsedBufferLength;
			int bytesToWriteIntoCurrentBuffer = Math.min(bytesLeftToWrite,bytesFreeInBuffer);
			
			System.arraycopy(b, currentOffset, uncompressedBuffer, currentUsedBufferLength, bytesToWriteIntoCurrentBuffer);
			
			currentUsedBufferLength += bytesToWriteIntoCurrentBuffer;
			currentOffset += bytesToWriteIntoCurrentBuffer;
			bytesLeftToWrite -= bytesToWriteIntoCurrentBuffer;
			
			if(currentUsedBufferLength == MAX_UNCOMPRESSED_BLOCK_SIZE){
				//we have filled our uncompressedBuffer
				//write one block to the wrapped outputStream
				flush();
			}
		}
		
	}
	
	/**
	 * Flush the current buffer out to the wrapped {@link OutputStream}
	 * as concatenated BGZF blocks - 
	 * <strong>This method should not be called directly.</strong>
	 */
	@Override
	public void flush() throws IOException {
		if(currentUsedBufferLength >0){
			//create a new Deflater each time.
			//There were problems caused by re-using
			//the deflater across blocks
			//probably because we call finish() ?
			Deflater currentDeflater = new Deflater(GZIP_COMPRESSION_LEVEL, true);
			
			currentDeflater.setInput(uncompressedBuffer, 0, currentUsedBufferLength);
			currentDeflater.finish();
			
			int compressedLength =currentDeflater.deflate(compressedBuffer);
			if(!currentDeflater.finished()){
				Deflater noCompresessionDeflater = new Deflater(Deflater.NO_COMPRESSION, true);
				noCompresessionDeflater.setInput(uncompressedBuffer, 0, currentUsedBufferLength);
				noCompresessionDeflater.finish();
				
				compressedLength =noCompresessionDeflater.deflate(compressedBuffer);
				if(!noCompresessionDeflater.finished()){
					//shouldn't happen
					throw new IOException("could not compress block to fit max size");
				}
			}
			currentCrc32.reset();
			//CRC is the check sum of the UNCOMPRESSED data
			currentCrc32.update(uncompressedBuffer, 0, currentUsedBufferLength);

			ByteBuffer bgzfBlockBuffer = ByteBuffer.allocate(compressedLength + BGZF_BLOCK_FULL_HEADER_LENGTH);
			bgzfBlockBuffer.order(ByteOrder.LITTLE_ENDIAN);
			bgzfBlockBuffer.put(BGZF_BLOCK_HEADER);
			//spec says write BSIZE -1
			//I guess to make sure the 
			//size will always fit in unsigned short.
			//
			//since we don't write out empty blocks
			//in this format subtracting 1 will never make a negative number.
			bgzfBlockBuffer.putShort((short)(bgzfBlockBuffer.capacity() -1));
			bgzfBlockBuffer.put(compressedBuffer,0,compressedLength);
			bgzfBlockBuffer.putInt((int)currentCrc32.getValue());
			bgzfBlockBuffer.putInt(currentUsedBufferLength);
			bgzfBlockBuffer.flip();
			byte[] asBytes = new byte[bgzfBlockBuffer.remaining()];
			bgzfBlockBuffer.get(asBytes);
			out.write(asBytes);
			//out.flush();
			//update counters
			compressedBytesWrittenSoFar += asBytes.length;
			//reset buffer
			currentUsedBufferLength = 0;
		}
	}

	@Override
	public void close() throws IOException {
		flush();
		out.write(EOF_MARKER);
		out.close();
	}

	
}
