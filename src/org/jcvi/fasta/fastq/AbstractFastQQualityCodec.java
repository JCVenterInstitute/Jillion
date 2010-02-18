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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.nio.ByteBuffer;

import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractFastQQualityCodec implements FastQQualityCodec{
    private final GlyphCodec<PhredQuality> qualityCodec;
    public AbstractFastQQualityCodec(GlyphCodec<PhredQuality> qualityCodec){
        this.qualityCodec = qualityCodec;
    }
    @Override
    public EncodedGlyphs<PhredQuality> decode(String fastqQualities) {
        ByteBuffer buffer = ByteBuffer.allocate(fastqQualities.length());
        for(int i=0; i<fastqQualities.length(); i++){
            buffer.put(decode(fastqQualities.charAt(i)).getNumber());
        }
        return new DefaultEncodedGlyphs<PhredQuality>(
                                    qualityCodec,
                                    PhredQuality.valueOf(buffer.array()));
    }

    @Override
    public String encode(EncodedGlyphs<PhredQuality> qualities) {
        StringBuilder builder= new StringBuilder();
        for(PhredQuality quality : qualities.decode()){
            builder.append(encode(quality));
        }
        return builder.toString();
    }
    
    protected abstract PhredQuality decode(char encodedQuality);
    protected abstract char encode(PhredQuality quality);
}
