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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.qual;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.symbol.Symbol;
/**
 * {@code PhredQuality} is a {@link Symbol} representation of
 * a Phred quality score.
 * @author dkatzel
 *
 *
 */
public final class PhredQuality implements Symbol, Comparable<PhredQuality>{
    //127 should be good enough for anybody
    public static final byte MAX_VALUE = Byte.MAX_VALUE;
    public static final byte MIN_VALUE = 0;
    private static final double TEN = 10D;
    private static final PhredQuality[] CACHE;
    
    /**
     * Our quality value.
     */
    private final byte value;
    /**
     * Initialize the cache for flyweight pattern.
     */
    static{
        CACHE = new PhredQuality[MAX_VALUE+1];
        //need to add the negative check since max ++ will
        //overflow into negative values of b
        for(byte b=MIN_VALUE; b>=MIN_VALUE && b<=MAX_VALUE; b++){
            CACHE[b] =new PhredQuality(b);
        }
    }
    private PhredQuality(byte b) {
    	this.value = b;
    }
    
    @Override
    public int compareTo(PhredQuality o) {
        return value - o.value;
    }
    /**
     * Get the {@link PhredQuality} with the given
     * associated error probability.
     * @param errorProbability a double between 0 and 1 exclusive
     * @return a {@link PhredQuality} instance, never null.
     * @throws IllegalArgumentException if the given probability
     *  is not between 0 and 1 exclusive
     */
    public static PhredQuality withErrorProbability(double errorProbability){
        return PhredQuality.valueOf(computeQualityScore(errorProbability));
    }
    
    /**
     * Get the quality score with the given
     * associated error probability.
     * @param errorProbability a double between 0 and 1 exclusive
     * @return an int instance, never negative
     * @throws IllegalArgumentException if the given probability
     *  is not between 0 and 1 exclusive
     */
    public static int computeQualityScore(double errorProbability){
        if(errorProbability<=0){
            throw new IllegalArgumentException("probability must be > 0 : "+ errorProbability);
        }
        if(errorProbability>=1){
            throw new IllegalArgumentException("probability must be < 1: "+ errorProbability);
        }
        return (int)Math.round(-TEN * Math.log10(errorProbability));
    }
    /**
     * Get the error probability that a basecall
     * of this phred value is incorrectly called.
     * @return {@literal 10^(-q/10)}
     */
    public double getErrorProbability(){
        return Math.pow(TEN, value/-TEN);       
    }
    /**
     * Get this {@link PhredQuality}'s 
     * quality score as a byte.
     * @return a positive byte value.
     */
    public byte getQualityScore(){
        return value;
    }
    /**
     * Get the corresponding {@link PhredQuality} instance
     * with the given quality score.
     * @param qualityScore the quality score
     * @return
     * @throws IllegalArgumentException if qualityScore < 0 or > {@link Byte#MAX_VALUE}.
     */
    public static PhredQuality valueOf(int qualityScore){
    	 if(qualityScore < MIN_VALUE || qualityScore > MAX_VALUE){
             throw new IllegalArgumentException("qualityScore of our range "+qualityScore);
         }
         return CACHE[qualityScore];
    }
   
    /**
     * 
     * @param bytes
     * @return
     * @throws IllegalArgumentException if  any of the qualityScores are < 0 or > {@link Byte#MAX_VALUE}.
     */
    public static List<PhredQuality> valueOf(byte[] bytes){
        List<PhredQuality> list = new ArrayList<PhredQuality>(bytes.length);
        for(int i=0; i<bytes.length; i++){
            list.add(valueOf(bytes[i]));
        }
        return list;
    }
    /**
     * Create an array of bytes which correspond to the 
     * input collection of {@link PhredQuality}s.
     * @param qualities a collection of PhredQuality values.
     * @return a new byte array, never null.
     */
    public static byte[] toArray(Collection<PhredQuality> qualities){
        ByteBuffer buf = ByteBuffer.allocate(qualities.size());
        for(PhredQuality quality : qualities){
            buf.put(quality.value);
        }
        return buf.array();
    }
    /**
     * Create an array of bytes which correspond to the 
     * input {@link QualitySequence}.
     * @param qualities a {@link QualitySequence}.
     * @return a new byte array, never null.
     * @throws NullPointerException if qualities are null
     */
    public static byte[] toArray(QualitySequence qualities){
        ByteBuffer buf = ByteBuffer.allocate((int)qualities.getLength());
        for(PhredQuality quality : qualities){
            buf.put(quality.value);
        }
        return buf.array();
    }

    @Override
    public String toString() {        
        return String.format("Q%02d",value);
    }

	@Override
	public String getName() {
		return Byte.toString(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PhredQuality)) {
			return false;
		}
		PhredQuality other = (PhredQuality) obj;
		return value == other.value;
	}
    
    
    
}
