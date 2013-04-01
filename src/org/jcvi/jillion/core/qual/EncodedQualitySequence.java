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
package org.jcvi.jillion.core.qual;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.jcvi.jillion.internal.core.EncodedSequence;
import org.jcvi.jillion.internal.core.GlyphCodec;

/**
 * {@code DefaultEncodedQualitySequence} 
 * decorates an {@link EncodedSequence} to allow  
 * it to implement the {@link QualitySequence}
 * interface.
 * @author dkatzel
 */
final class EncodedQualitySequence extends EncodedSequence<PhredQuality> implements QualitySequence{

   
   

	public EncodedQualitySequence(GlyphCodec<PhredQuality> codec, byte[] data) {
		super(codec, data);
	}

	public EncodedQualitySequence(GlyphCodec<PhredQuality> codec,
			Collection<PhredQuality> glyphsToEncode) {
		super(codec, glyphsToEncode);
	}

	@Override
	public int hashCode(){
		return super.hashCode();
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof QualitySequence)){
        	return false;
        }
        return super.equals(obj);
    }
    
    /**
	 * {@inheritDoc}
	 */
	public byte[] toArray(){
        ByteBuffer buf = ByteBuffer.allocate((int)getLength());
        for(PhredQuality quality : this){
            buf.put(quality.getQualityScore());
        }
        return buf.array();
    }
}
