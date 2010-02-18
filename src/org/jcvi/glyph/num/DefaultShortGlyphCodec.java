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
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;

public final class DefaultShortGlyphCodec implements GlyphCodec<ShortGlyph>{

    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    
    private static final DefaultShortGlyphCodec INSTANCE = new DefaultShortGlyphCodec();
    private DefaultShortGlyphCodec(){
        
    }
    public static DefaultShortGlyphCodec getInstance(){
        return INSTANCE;
    }
    @Override
    public List<ShortGlyph> decode(byte[] encodedGlyphs) {
        List<ShortGlyph> glyphs = new ArrayList<ShortGlyph>();
        ShortBuffer buf = ByteBuffer.wrap(encodedGlyphs).asShortBuffer();
        while(buf.hasRemaining()){
            glyphs.add(FACTORY.getGlyphFor(buf.get()));
        }
        return glyphs;
    }

    @Override
    public ShortGlyph decode(byte[] encodedGlyphs, int index) {
        int indexIntoShortAray = index*2;
        final int hi = encodedGlyphs[indexIntoShortAray]<<8;
        final byte low = encodedGlyphs[indexIntoShortAray+1];
        int value = hi | (low & 0xFF);
        return FACTORY.getGlyphFor((short)value);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length/2;
    }

    @Override
    public byte[] encode(List<ShortGlyph> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size()*2);
        for(ShortGlyph g : glyphs){
            buf.putShort(g.getNumber());
        }
        return buf.array();
    }

}
