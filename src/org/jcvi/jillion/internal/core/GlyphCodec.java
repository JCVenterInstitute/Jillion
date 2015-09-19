/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
