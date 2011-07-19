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
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
/**
 * {@code EncodedShortGlyph} encodes a {@link Sequence}
 * of {@link ShortGlyph}s.
 * @author dkatzel
 */
public class EncodedShortGlyph implements Sequence<ShortGlyph>{

    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private final short[] data;
    public EncodedShortGlyph(List<ShortGlyph> shorts){
        this.data = encode(shorts);
    }
    private short[] encode(List<ShortGlyph> shorts) {
        ShortBuffer buffer = ShortBuffer.allocate(shorts.size());
        for(ShortGlyph byteGlyph : shorts){
            buffer.put(byteGlyph.getNumber().shortValue());
        }
        return buffer.array();
    }
    
    
    @Override
    public List<ShortGlyph> decode() {
         return FACTORY.getGlyphsFor(data);
    }

    @Override
    public ShortGlyph get(int index) {
        return FACTORY.getGlyphFor(data[index]);
    }

    @Override
    public long getLength() {
        return data.length;
    }
    @Override
    public List<ShortGlyph> decode(Range range) {
        if(range==null){
            return decode();
        }
        List<ShortGlyph> result = new ArrayList<ShortGlyph>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

}
