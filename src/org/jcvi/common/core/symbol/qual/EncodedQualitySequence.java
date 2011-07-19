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

package org.jcvi.common.core.symbol.qual;

import java.util.Collection;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.common.core.symbol.EncodedSequence;

/**
 * {@code DefaultEncodedQualitySequence} 
 * decorates an {@link EncodedSequence} to allow  
 * it to implement the {@link QualitySequence}
 * interface.
 * @author dkatzel
 */
public class EncodedQualitySequence implements QualitySequence{

    private final EncodedSequence<PhredQuality>  delegate;
    /**
     * @param codec
     * @param data
     */
    public EncodedQualitySequence(QualityGlyphCodec codec,
            byte[] data) {
        delegate = new EncodedSequence<PhredQuality>(codec, data);
    }

    /**
     * @param codec
     * @param glyphsToEncode
     */
    public EncodedQualitySequence(QualityGlyphCodec codec,
            Collection<PhredQuality> glyphsToEncode) {
        delegate = new EncodedSequence<PhredQuality>(codec, glyphsToEncode);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<PhredQuality> decode() {
        return delegate.decode();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public PhredQuality get(int index) {
        return delegate.get(index);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getLength() {
        return delegate.getLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<PhredQuality> decode(Range range) {
        return delegate.decode(range);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((delegate == null) ? 0 : delegate.hashCode());
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EncodedQualitySequence other = (EncodedQualitySequence) obj;
        if (delegate == null) {
            if (other.delegate != null)
                return false;
        } else if (!delegate.equals(other.delegate))
            return false;
        return true;
    }
    
    

}
