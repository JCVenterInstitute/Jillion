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
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.qual;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * {@code DefaultQualitySymbolCodec} stores 
 * all the quality values in a byte array
 * one byte per quality value.
 * @author dkatzel
 *
 */
enum DefaultQualitySymbolCodec implements QualitySymbolCodec{

	INSTANCE
	;
	

    @Override
    public PhredQuality decode(byte[] encodedGlyphs, long index) {
        return PhredQuality.valueOf(encodedGlyphs[(int)index]);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length;
    }

    @Override
    public byte[] encode(Collection<PhredQuality> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size());
        for(PhredQuality g : glyphs){
            buf.put(g.getQualityScore());
        }
        return buf.array();
    }
}
