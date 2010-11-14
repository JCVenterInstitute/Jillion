/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.DataFormatException;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.TraceEncoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRUtil;

/**
 * <code>ZLibData</code> Data format uses a zipped algorithm
 * to compress the data.  Most common form of compression is
 * <code> HUFFMAN</code>
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public enum ZLibData implements Data {
    /**
     * Singleton instance of ZLibData.
     */
    INSTANCE;
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data) throws TraceDecoderException {

        try{
            ByteBuffer size = ByteBuffer.allocate(4);
            size.put(data,1,4);    
            int uncompressedLength =(int) ZTRUtil.readInt(IOUtil.switchEndian(size.array()));            
            ByteBuffer compressedData = ByteBuffer.allocate(data.length-5);
            compressedData.put(data,5,data.length-5);
            //decompress the zipped data
            Inflater decompresser = new Inflater();
            decompresser.setInput(compressedData.array());
            ByteBuffer byteBuffer = ByteBuffer.allocate(uncompressedLength);
            decompresser.inflate(byteBuffer.array());
            
            decompresser.end();
            return byteBuffer.array();
        }catch(DataFormatException dfEx){
            throw new TraceDecoderException("could not parse ZLibData",dfEx);
        }
    }

	@Override
	public byte[] encodeData(byte[] data) throws TraceEncoderException {
		
		Deflater zlibCompresser= new Deflater();
		zlibCompresser.setInput(data);
		zlibCompresser.finish();
		byte[] compressedData = new byte[data.length*2];
		int numberOfCompressedBytes =zlibCompresser.deflate(compressedData);
		//5 = header length + uncompressed size as int
		ByteBuffer encodedBuffer = ByteBuffer.allocate(numberOfCompressedBytes+5);
		encodedBuffer.put(DataHeader.ZLIB_ENCODED);
		//for some reason length is different endian...
		byte size[] =IOUtil.switchEndian(IOUtil.convertUnsignedIntToByteArray(data.length));
		encodedBuffer.put(size);
		encodedBuffer.put(compressedData,0, numberOfCompressedBytes);
		encodedBuffer.flip();
		return Arrays.copyOfRange(encodedBuffer.array(), 0, encodedBuffer.limit());
	}
	/**
	 * This returns the same result as {@link #encodeData(byte[])}
	 * the optional parameter is ignored. 
	 */
	@Override
	public byte[] encodeData(byte[] data, byte ignored)
			throws TraceEncoderException {
		return encodeData(data);
	}
}
