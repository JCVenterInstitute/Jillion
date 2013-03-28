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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
/**
 * {@code FastqQualityCodec} is a can encode and decode
 * The different ways a FASTQ file can be encode
 * quality data.
 * @author dkatzel
 *
 */
public enum FastqQualityCodec {
	/**
	 * {@code ILLUMINA} supports Illumina 1.3+
	 * Fastq encoded qualities.
	 */
	ILLUMINA{
		 @Override
		    protected PhredQuality decode(char encodedQuality) {
		        return PhredQuality.valueOf(encodedQuality -64);
		    }

		    @Override
		    protected char encode(PhredQuality quality) {
		        return (char)(quality.getQualityScore()+64);
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
		        return (char)(quality.getQualityScore()+33);
		    }
	},
	/**
	 * {@code SOLEXA} is a {@link FastqQualityCodec}
	 * that supports qualities not only encoded in
	 * FASTq solexa/Illumina format,
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
            buffer.put(decode(fastqQualities.charAt(i)).getQualityScore());
        }
        return new QualitySequenceBuilder(buffer.array())
        		.build();
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
        for(PhredQuality quality : qualities){
            builder.append(encode(quality));
        }
        return builder.toString();
    }
    
    protected abstract PhredQuality decode(char encodedQuality);
    protected abstract char encode(PhredQuality quality);
    
}
