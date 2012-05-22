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

package org.jcvi.common.core.seq.fastx.fastq;

import java.util.regex.Pattern;

/**
 * {@code FastqUtil} is a utility class for working with 
 * FASTQ data.
 * @author dkatzel
 *
 *
 */
public final class FastqUtil {

    private FastqUtil(){}
    /**
     * This is the {@link Pattern} to parse
     * the sequence record defline of a FASTQ record.
     * Group 1 will be the read id
     * Group 3 will be the optional comment if there is one,
     * or null if there isn't a comment.
     */
    public static final Pattern SEQ_DEFLINE_PATTERN = Pattern.compile("^@(\\S+)(\\s+)?(.+$)?");
    /**
     * This is the {@link Pattern} to parse
     * the quality record defline of a FASTQ record.
     * Group 1 will be the optional id of the read if there is one
     * or null if there isn't an id.  If the id exists,
     * then it should match the id of the seq defline.
     */
    public static final Pattern QUAL_DEFLINE_PATTERN = Pattern.compile("^\\+(.+$)?");
    
    /**
     * Attempts to guess the {@link FastqQualityCodec} used to encode
     * the given qualities.
     * @param encodedQualities a String of fastq encoded qualities
     * @return an instance of {@link FastqQualityCodec} which could have been 
     * used to encode the given qualities (will never be null).
     * @throws NullPointerException if the given qualities are null.
     * @throws IllegalArgumentException if the given quality string is empty
     * or if it contains any characters out of range of any known
     * quality encoding formats.
     */
    public static FastqQualityCodec guessQualityCodecUsed(String encodedQualities){
    	if(encodedQualities.isEmpty()){
    		throw new IllegalArgumentException("encoded qualities can not be empty");
    	}
    	//sanger uses 33 as an offset so any ascii values around there will 
    	//automatically be sanger
    	
    	//solexa and illumina encoding have offsets of 64 so they look very similar
    	//except solexa uses a different log scale and can actually have scores as low
    	//as -5 (ascii value 59) so any values from ascii 59-63 mean solexa
    	boolean hasSolexaOnlyValues=false;
    	for(int i=0; i<encodedQualities.length(); i++){
    		int asciiValue = encodedQualities.charAt(i);
    		if(asciiValue <33){
    			throw new IllegalArgumentException(
    					String.format(
    							"invalid encoded qualities has out of range ascii value %d : '%s'", 
    							asciiValue,
    							encodedQualities));
    		}
    		if(asciiValue <59){
    			return FastqQualityCodec.SANGER;
    		}
    		if(asciiValue < 64){
    			hasSolexaOnlyValues=true;
    		}
    	}
    	//if we get here then we only saw encoded values from ascii 64 +
    	//assume illumina, solexa scaling is so close at good quality anyway
    	//that I don't think it matters.
    	if(hasSolexaOnlyValues){
    		return FastqQualityCodec.SOLEXA;
    	}
    	return FastqQualityCodec.ILLUMINA;
    }
}
