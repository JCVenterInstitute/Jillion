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
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.util.List;

import org.jcvi.Range;
/**
 * {@code EncodedGlyphs} is an interface for 
 * encoding or compressing a list of {@link Glyph}s
 * so that they take up less memory.
 * @author dkatzel
 *
 *
 */
public interface EncodedGlyphs<T extends Glyph> {
    /**
     * Decode the entire list of encoded glyphs into
     * a List.
     * @return a List of Glyphs.
     */
    List<T> decode();
    /**
     * Gets the specific glyph at the specified index.
     * this should return the same Glyph as
     * {@code decode().get(index)} but hopefully
     * in a more efficient manner.
     * @param index the index of the Glyph to get.
     * @return the Glyph at the specified index.
     */
    T get(int index);
    /**
     * Get the number of glyphs that are encoded.
     * This should return the same value as
     * {@code decode().size()}.
     * @return the length of the encoded glyphs will never
     * be less than {@code 0}.
     */
    long getLength();
    @Override
    int hashCode();
    
    @Override
    boolean equals(Object obj);
    /**
     * Decodes the Glyphs for the given range
     * @param range the range to trim against, if null, then decode
     * all glyphs (the same as {@link #decode()}).
     * @return
     */
    List<T> decode(Range range);

}
