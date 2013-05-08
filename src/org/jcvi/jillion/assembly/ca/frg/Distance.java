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
 * Created on Mar 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;
/**
 * {@code Distance} is class that represents
 * genomic library mate distances.
 * @author dkatzel
 *
 */
public final class Distance {
    /**
     * 
     */
    private static final float SIX = 6F;

    /**
     * Celera Assembly's Gatekeeper doesn't
     * like it when the 3 standard Deviations from
     * the mean falls below zero.
     */
    private static final int CA_GATEKEEPER_STDDEV_LIMIT = 3;

    private static final float MAX_ROUNDING_ERROR = 1.015F;
    private final int min, max;
    private final float mean;
    private final float stdDev;
    /**
     * Builds a new {@link Distance} using the given mean and standard deviation.
     * @param mean the mean distance.
     * @param stdDev the standard deviation.
     * @return a new {@link Distance}.
     */
    public static Distance buildDistance(int min, int max,float mean, float stdDev){
        return new Distance(min,max,mean, stdDev);
    }
    /**
     * Builds a new {@link Distance} using the given mean and standard deviation.
     * @param mean the mean distance.
     * @param stdDev the standard deviation.
     * @return a new {@link Distance}.
     */
    public static Distance buildDistance(float mean, float stdDev){
        float delta = calculateDelta(stdDev);
        int plusDelta = (int)(mean+delta);
        int minusDelta = (int)(mean-delta);
        return new Distance(Math.min(plusDelta, minusDelta),
                Math.max(plusDelta, minusDelta),
                mean, stdDev);
    }
    private static float calculateDelta(float stdDev) {
        return  CA_GATEKEEPER_STDDEV_LIMIT*stdDev;
    }
    /**
     * Build a new {@link Distance} based on the given min and max values.
     * @param min the minimum distance
     * @param max the max distance 
     * @return a new {@link Distance}.
     */
    public static Distance buildDistance(int min, int max){       
        float mean = computeMean(min, max);
        float stdDev = computeStandardDeviation(min, max);
        return new Distance(min,max,mean, stdDev);
    }
    
    
    private static float computeStandardDeviation(int min, int max) {
        return (max-min)/SIX;
    }
    private static float computeMean(int min, int max) {
        //divide by 2 first to avoid buffer overflow of large numbers
        return min/2F + max/2F;
    }
    /**
     * Build a new {@link Distance} which is guaranteed to 
     * have values that conform to Celera Assembler Standard Deviation
     * Distance constraints.  If no transformation is needed, 
     * this method will return the same object that was passed in.
     * @param distance a {@link Distance} containing the library distance information;
     * can not be null.
     * @return a new {@link Distance} that conforms to the CA distance constraints.
     * @throws NullPointerException if distance is null.
     */
    public static Distance transformIntoCeleraAssemblerDistance(Distance distance){
        float mean = distance.getMean();
        float stdDev = distance.getStdDev();
         if(mean < calculateDelta(stdDev)){
            final float correctedStdDev = (mean-MAX_ROUNDING_ERROR)/CA_GATEKEEPER_STDDEV_LIMIT;
            return new Distance(distance.getMin(), distance.getMax(),mean,correctedStdDev);
        }
         return distance;
    }

    private Distance(int min, int max,float mean, float stdDev){
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.stdDev = stdDev;
    }
    /**
     * Get the mean (average) distance between
     * mates.
     * @return the mean as a floating point number.
     */
    public float getMean(){
        return mean;
    }
    /**
     * Get the standard deviation of the mean between
     * mates.
     * @return the std dev as a floating point number.
     */
    public float getStdDev(){
        return stdDev;
    }
    /**
     * Get the minimum distance between
     * mates.
     * @return the min as an int, should always be >=0.
     */
    public int getMin() {
        return min;
    }
    /**
     * Get the max distance between
     * mates.
     * @return the max as an int, should always be >={@link #getMin()}.
     */
    public int getMax() {
        return max;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + max;
        result = prime * result + Float.floatToIntBits(mean);
        result = prime * result + min;
        result = prime * result + Float.floatToIntBits(stdDev);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof Distance)){
            return false;
        }
        Distance other = (Distance) obj;
        if (max != other.max){
            return false;
        }
        if (Float.floatToIntBits(mean) != Float.floatToIntBits(other.mean)){
            return false;
        }
        if (min != other.min){
            return false;
        }
        if (Float.floatToIntBits(stdDev) != Float.floatToIntBits(other.stdDev)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        
        return String.format("%.3f, std: %.3f", mean, stdDev);
    }
    
    
}
