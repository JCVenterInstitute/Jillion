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
package org.jcvi.common.core.seq.fastx.fastq;

import java.nio.ByteBuffer;

import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySymbolCodec;
import org.jcvi.common.core.symbol.qual.QualitySequence;
/**
 * {@code FastQQualityCodec} is a can encode and decode
 * The different ways a FASTQ file can be encode
 * quality data.
 * @author dkatzel
 *
 */
public enum FastQQualityCodec {
	/**
	 * {@code ILLUMINA} supports Illumina 1.3+
	 * FastQ encoded qualities.
	 */
	ILLUMINA{
		 @Override
		    protected PhredQuality decode(char encodedQuality) {
		        return PhredQuality.valueOf(encodedQuality -64);
		    }

		    @Override
		    protected char encode(PhredQuality quality) {
		        return (char)(quality.getValue().intValue()+64);
		    }
	},
	/**
	 * {@code SANGER} supports Sanger encoded qualities.
	 */
	SANGER{
		 @Override
		    protected PhredQuality decode(char encodedQuality) {
		        return PhredQuality.valueOf(encodedQuality -33);
		    }

		    @Override
		    protected char encode(PhredQuality quality) {
		        return (char)(quality.getValue().intValue()+33);
		    }
	},
	/**
	 * {@code SOLEXA} is a FastQQualityCodec
	 * that supports qualities not only encoded in
	 * FASTQ solexa/Illumina format,
	 * but <strong>also</strong> the quality scores are
	 * in Solexa scale and not the Phred scale.
	 */
	SOLEXA{
		    @Override
		    protected PhredQuality decode(char encodedQuality) {
		        int solexaQuality =encodedQuality -64;
		        return SolexaUtil.convertSolexaQualityToPhredQuality(solexaQuality);
		    }

		    @Override
		    protected char encode(PhredQuality quality) {
		        int solexaQuality = SolexaUtil.convertPhredQualityToSolexaQuality(quality);
		        return (char)(solexaQuality +64);
		    }
	}
	;
	private final QualitySymbolCodec qualityCodec = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
	
	/**
	 * Decode the given FASTQ quality encoded String
	 * into the equivalent Encoded Qualities.
	 * @param fastqQualities
	 * @return a new EncodedGlyphs representing
	 * the decoded FASTQ quality values.
	 */
    public QualitySequence decode(String fastqQualities) {
        ByteBuffer buffer = ByteBuffer.allocate(fastqQualities.length());
        for(int i=0; i<fastqQualities.length(); i++){
            buffer.put(decode(fastqQualities.charAt(i)).getValue());
        }
        return new EncodedQualitySequence(
                                    qualityCodec,
                                    PhredQuality.valueOf(buffer.array()));
    }

    /**
     * Encode the given qualities into 
     * a FASTQ quality encoded String.
     * @param qualities the qualities to encode
     * @return a String representing these
     * quality values in the desired String encoding.
     */
    public String encode(Sequence<PhredQuality> qualities) {
        StringBuilder builder= new StringBuilder();
        for(PhredQuality quality : qualities.asList()){
            builder.append(encode(quality));
        }
        return builder.toString();
    }
    
    protected abstract PhredQuality decode(char encodedQuality);
    protected abstract char encode(PhredQuality quality);
    
}
