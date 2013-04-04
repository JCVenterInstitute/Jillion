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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.qual;

import java.nio.ByteBuffer;

import org.jcvi.jillion.internal.core.util.JillionUtil;

/**
 * {@code PhredQuality} is a representation of
 * a Phred quality score.
 * <p/>
 * PhredQuality uses the flyweight pattern to reuse the same
 * objects for the same quality score.  So  
 * @author dkatzel
 *
 *
 */
public final class PhredQuality implements Comparable<PhredQuality>{
    //127 should be good enough for anybody
	/**
	 * Max allowed Phred Quality score, currently set to
	 * {@value}.
	 */
    public static final byte MAX_VALUE = Byte.MAX_VALUE;
    /**
     * Minimum allowd Phred Quality score, currently set to
     * {@value}.
     */
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
        return JillionUtil.compare(value, o.value);
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
     * @return the {@link PhredQuality} instance that represents
     * the given qualityScore.
     * @throws IllegalArgumentException if qualityScore < 0 or > {@link Byte#MAX_VALUE}.
     */
    public static PhredQuality valueOf(int qualityScore){
    	 if(qualityScore < MIN_VALUE || qualityScore > MAX_VALUE){
             throw new IllegalArgumentException("qualityScore of our range "+qualityScore);
         }
         return CACHE[qualityScore];
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}
	/**
	 * Two {@link PhredQuality} values are equal
	 * if and only if they both have the same quality score.
	 * @return {@code true} if the given {@link PhredQuality}
	 * has the same quality score as this; {@code false}
	 * otherwise.
	 */
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
