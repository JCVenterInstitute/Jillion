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
package org.jcvi.common.core.symbol.pos;

import java.nio.ShortBuffer;
import java.util.List;

import org.jcvi.common.core.symbol.DefaultShortGlyphCodec;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.jcvi.common.core.util.CommonUtil;


/**
 * <code>Peaks</code> contains the position
 * data of each peak in a <code>Trace</code>.
 *
 * @author dkatzel
 *
 *
 */
public class SangerPeak{
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private static final DefaultShortGlyphCodec CODEC = DefaultShortGlyphCodec.getInstance();
    private Sequence<ShortSymbol> data;

    public SangerPeak(short[] data){
        this(FACTORY.getGlyphsFor(data));
       
    }
    public SangerPeak(List<ShortSymbol> data){
        this.data = new EncodedSequence<ShortSymbol>(CODEC, data);
       
    }
    public SangerPeak(Sequence<ShortSymbol> data){
        if(data==null){
            throw new NullPointerException("encoded data can not be null");
        }
        this.data = data;
       
    }
    /**
     * @param data
     */
    public SangerPeak(ShortBuffer data) {
        this(data.array());
    }

    /**
     * @return the data
     */
    public Sequence<ShortSymbol> getData() {
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
       if(!(obj instanceof SangerPeak)){
           return false;
       }
       SangerPeak other = (SangerPeak) obj;
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
