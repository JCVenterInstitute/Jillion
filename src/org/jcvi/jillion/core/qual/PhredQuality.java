/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
 * <p>
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
    /**
     * Compares this quality value to the given quality.
     * 
     * @param other the other quality value.
     * 
     * @return A negative number if this quality is less than the
     * other quality, {@code 0} if the two qualities
     * are equal, and a positive value if this
     * quality value is greater than the other quality.
     */
    @Override
    public int compareTo(PhredQuality other) {
        return JillionUtil.compare(value, other.value);
    }
    /**
     * Compares this quality value to the given quality value <strong>as a double</strong>.
     * 
     * @param qualityValue the other quality value to compare to.  
     * <strong>Note:</strong> no boundary checks are done to make sure that the other quality value is
     *  a value in the valid {@link PhredQuality} range.
     *  
     * @implSpec this is the functionally the same as
     * {@link #compareTo(PhredQuality) compareTo(PhredQuality.valueOf(qualityValue))}
     * but should be computed faster since there is no creation of an extra {@link PhredQuality}
     * object.
     * 
     * @return A negative number if this quality is less than the
     * other quality, {@code 0} if the two qualities
     * are equal, and a positive value if this
     * quality value is greater than the other quality.
     * 
     * @since 5.2
     */
    public int compareTo(double qualityValue) {
        return Double.compare(value, qualityValue);
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
     * @throws IllegalArgumentException if qualityScore &lt; 0 or &gt; {@link Byte#MAX_VALUE}.
     */
    public static PhredQuality valueOf(int qualityScore){
    	 validate(qualityScore);
         return CACHE[qualityScore];
    }

	private static void validate(int qualityScore) {
		if(qualityScore < MIN_VALUE || qualityScore > MAX_VALUE){
             throw new IllegalArgumentException("qualityScore of our range "+qualityScore);
         }
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
        return toStringValidValue(value);
    }
    /**
     * Returns same String value
     * as {@code PhredQuality.valueOf(qualityValue).toString()}
     * but without the overhead of object construction.
     * @param qualityValue the quality value to print.
     * @return a String will never be null.
     * @throws IllegalArgumentException if qualityScore &lt; 0 or &gt; {@link Byte#MAX_VALUE}.
     */
    public static String toString(byte qualityValue){
    	validate(qualityValue);
    	return toStringValidValue(qualityValue);
    }
    /**
     * Print the toString value of the quality
     * value without checking to make sure
     * the quality value is valid.  This
     * method should only be called on values
     * that have already been validated.
     * @param qualityValue
     * @return
     */
	private static String toStringValidValue(byte qualityValue) {
		return String.format("Q%02d",qualityValue);
	}

	@Override
	public int hashCode() {
		return value;
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
