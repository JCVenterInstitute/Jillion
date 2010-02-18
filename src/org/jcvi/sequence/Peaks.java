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
 * Created on Oct 24, 2007
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.nio.ShortBuffer;
import java.util.List;

import org.jcvi.CommonUtil;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.DefaultShortGlyphCodec;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;


/**
 * <code>Peaks</code> contains the position
 * data of each peak in a <code>Trace</code>.
 *
 * @author dkatzel
 *
 *
 */
public class Peaks{
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private static final DefaultShortGlyphCodec CODEC = DefaultShortGlyphCodec.getInstance();
    private EncodedGlyphs<ShortGlyph> data;

    public Peaks(short[] data){
        this(FACTORY.getGlyphsFor(data));
       
    }
    public Peaks(List<ShortGlyph> data){
        this.data = new DefaultEncodedGlyphs<ShortGlyph>(CODEC, data);
       
    }
    /**
     * @param data
     */
    public Peaks(ShortBuffer data) {
        this(data.array());
    }

    /**
     * @return the data
     */
    public EncodedGlyphs<ShortGlyph> getData() {
        return data;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
       if(obj == this){
           return true;
       }
       if(!(obj instanceof Peaks)){
           return false;
       }
       Peaks other = (Peaks) obj;
       return CommonUtil.bothNull(getData(), other.getData())  
                   ||        
            (!CommonUtil.onlyOneIsNull(getData(), other.getData()) 
                   && 
            CommonUtil.similarTo(getData(), other.getData()));
       
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getData() == null) ? 0 :getData().hashCode());

        return result;
    }

}
