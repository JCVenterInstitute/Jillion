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

package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
/**
 * {@code AbstractZipByteGlyphCodec} is a 
 * ByteGlyphCodec which uses ZIP compression
 * to encode {@link ByteSymbol}s.  ZIP should greatly 
 * reduce the size of the data being encoded at a slight
 * cost of extra computation time to uncompress the data
 * to decode it.
 * @author dkatzel
 *
 * @param <G> the {@link ByteSymbol} implementation
 * which is being encoded.
 */
public abstract class AbstractZipByteGlyphCodec<G extends ByteSymbol> implements ByteGlyphCodec<G>{


	/**
	 * Currently not very efficient, must
	 * decode the entire thing before we can
	 * fetch any values.  This is the same as
	 * {@code decode(encodedGlyphs).get(index)}.
	 */
	@Override
	public G decode(byte[] encodedGlyphs, int index) {
		//can we optimize this?
		return decode(encodedGlyphs).get(index);
	}

	/* (non-Javadoc)
	 * @see org.jcvi.glyph.GlyphCodec#decode(byte[])
	 */
	@Override
	public List<G> decode(byte[] encodedGlyphs) {
		if(encodedGlyphs.length ==0){
			return Collections.emptyList();
		}
		ByteBuffer buffer =ByteBuffer.wrap(encodedGlyphs);
		int size = buffer.getInt();
		
		Inflater decompresser = new Inflater();
		byte[] temp = new byte[buffer.remaining()];
		buffer.get(temp);
		decompresser.setInput(temp);
		byte[] result = new byte[size];
		try {
			int decompressedSize =decompresser.inflate(result);
			if(decompressedSize != size){
				throw new IllegalStateException(
						String.format("invalid decompressed data; expected %d bytes but got %d", decompressedSize,size));
			}
		} catch (DataFormatException e) {
			throw new IllegalStateException("error decompressing data",e);
		}
		return getGlyphsFor(result);
	}

	protected abstract List<G> getGlyphsFor(byte[] decodedBytes);

	/* (non-Javadoc)
	 * @see org.jcvi.glyph.GlyphCodec#decodedLengthOf(byte[])
	 */
	@Override
	public int decodedLengthOf(byte[] encodedGlyphs) {
		return ByteBuffer.wrap(encodedGlyphs).getInt();
	}

	/* (non-Javadoc)
	 * @see org.jcvi.glyph.GlyphCodec#encode(java.util.List)
	 */
	@Override
	public byte[] encode(Collection<G> glyphs) {
		if(glyphs.isEmpty()){
			return new byte[0];
		}
		byte[] data = new byte[glyphs.size()];
		int i=0;
		for(G glyph : glyphs){
		    data[i]= glyph.getNumber();
		    i++;
		}
		
		 Deflater compresser = new Deflater();
	     compresser.setInput(data);
	     compresser.finish();
	     //initialize temp array to original length
	     //compressed version can't be bigger than
	     //uncompressed
	     byte[] temp = new byte[data.length+100];
	     
	     int length = compresser.deflate(temp);
	     
	     //initial 4 bytes is uncompressed size
	     ByteBuffer result = ByteBuffer.allocate(4+length);
	     result.putInt(data.length);
	     
	     result.put(Arrays.copyOfRange(temp, 0, length));
	     
	     return result.array();
	}



	
}
