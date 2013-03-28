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
/*
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.core;

import java.util.Collection;

/**
 * {@code GlyphCodec} is an interface
 * that allows Glyphs to be encoded and decoded
 * into byte arrays.  
 * @author dkatzel
 *
 * @param <T> the Type of Glyph to be
 * encoded and or decoded.
 */
public interface GlyphCodec<T> {
	/**
	 * Encode the given collection of glyphs
	 * into a byte array.
	 * @param glyphs the glyphs to encode.
	 * @return a byte array (never null).
	 */
    byte[] encode(Collection<T> glyphs);
    
    /**
     * Get a single Gyph from the encoded
     * byte array at the given index.
     * @param encodedGlyphs the byte array of 
     * encoded glyphs.
     * @param index the index of the glyph 
     * to get.
     * @return the decoded glyph
     * at the given index.
     * @throws IndexOutOfBoundsException if index is out of bounds
     * of the index.
     */
    T decode(byte[] encodedGlyphs, long index);
    /**
     * Get the number of glyphs
     * represented by the encoded byte
     * array.
     * @param encodedGlyphs the byte array of
     * glyphs.
     * @return an int >=0.
     */
    int decodedLengthOf(byte[] encodedGlyphs);
}
