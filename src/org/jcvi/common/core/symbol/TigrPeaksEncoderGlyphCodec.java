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
 * Created on Sep 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;


public enum TigrPeaksEncoderGlyphCodec implements GlyphCodec<ShortGlyph>{

    INSTANCE;
    
    private static final ShortGlyphFactory GLYPH_FACTORY = ShortGlyphFactory.getInstance();
    private static final Charset CHARSET = Charset.forName("US-ASCII");
    
    

    @Override
    public List<ShortGlyph> decode(byte[] encodedGlyphs) {
        String encodedString = new String(encodedGlyphs);
        return GLYPH_FACTORY.getGlyphsFor(
                TigrPeaksEncoder.decode(encodedString));
    }

    @Override
    public ShortGlyph decode(byte[] encodedGlyphs, int index) {
        return GLYPH_FACTORY.getGlyphFor(
                TigrPeaksEncoder.decode(new String(encodedGlyphs,CHARSET), index+1)[index]);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return decode(encodedGlyphs).size();
    }

    @Override
    public byte[] encode(Collection<ShortGlyph> glyphs) {
        return TigrPeaksEncoder.encode(ShortGlyph.toArray(glyphs)).getBytes(CHARSET);
    }

   
}
