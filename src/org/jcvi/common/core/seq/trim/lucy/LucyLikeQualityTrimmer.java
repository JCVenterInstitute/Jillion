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

package org.jcvi.common.core.seq.trim.lucy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;



/**
 * {@code LucyLikeQualityTrimmer} performs Lucy like quality trimming.  The algorithm 
 * this class uses is the algorithm from the 2001 Lucy paper in
 * Bioinformatics.
 * <p>
 * The Trimming algorithm performs 3 separate trimming phases:
 * <ol>
 * <li> 
 * The beginning and end of each sequence is trimmed to remove the low
 * quality data from each end.  The resulting region is called the <code>bracket</code>.
 * </li>
 * <li>
 * The <code>bracket</code> is then trimmed against a series of sliding <code>Window</code>s.
 * A Window of a certain size is slid across the <code>bracket</code>, if the average error probability
 * of the data inside the window meets the given threshold, then that window is considered good quality.
 * If more than one window is given,then
 * the next Window is applied to the good quality range found by the previous window.
 * The order of the Windows applied to trimming is largest window size
 * to smallest window size.  The resulting ranges from this step will be referred
 * to as <code>candidate regions</code>
 * </li>
 * <li> 
 * Finally, the candidate good quality regions are further trimmed to meet overall
 * error rates and the ends of the region (currently first and last 2 bases)
 * must also meet a specific error rate threshold.  Each candidate region is trimmed
 * until each end and the total overall error rate meet these thesholds at the same time. 
 * </li>
 * 
 * The largest of the ranges after all these steps is the final trim range.
 * </ol>
 * @author dkatzel
 *
 * @see <a href ="http://www.ncbi.nlm.nih.gov/pubmed/11751217">Chou HH, Holmes MH. DNA sequence quality trimming and vector removal. Bioinformatics. 2001;17:1093-1104. doi: 10.1093/bioinformatics/17.12.1093.<a>
 */
public class LucyLikeQualityTrimmer {
    private static final int SIZE_OF_ENDS =2;
    private final int minGoodLength;
    private final Window bracketWindow;
    private final double maxTotalAvgError;
    private final double maxErrorAtEnds;
    
    private final Set<Window> trimWindows;
    
    
    
    /**
     * @param minGoodLength
     * @param bracketWindow
     * @param trimWindows
     * @param maxAvgError
     * @param maxErrorAtEnds
     */
    private LucyLikeQualityTrimmer(int minGoodLength, Window bracketWindow,
            Set<Window> trimWindows, double maxAvgError, double maxErrorAtEnds) {
        this.minGoodLength = minGoodLength;
        this.bracketWindow = bracketWindow;
        this.trimWindows = trimWindows;
        this.maxTotalAvgError = maxAvgError;
        this.maxErrorAtEnds = maxErrorAtEnds;
    }
    
    
    public Range trim(Sequence<PhredQuality> qualities){
        List<Double> errorRates = convertToErrorRates(qualities);
        Range bracketedRegion = findBracketedRegion(errorRates);
        Range largestRange = findLargestCleanRangeFrom(bracketedRegion, errorRates);
        if(largestRange.size() < minGoodLength){
            return Range.buildEmptyRange(CoordinateSystem.RESIDUE_BASED,1);
        }
        return largestRange.shiftRight(bracketedRegion.getStart()).convertRange(CoordinateSystem.RESIDUE_BASED);
    }


    private Range findLargestCleanRangeFrom(Range bracketedRegion,
            List<Double> errorRates) {
        List<Double> bracketedErrorRates = getSubList(errorRates, bracketedRegion);
        List<Range> largestRanges = new ArrayList<Range>();
        for(Range candidateCleanRange : findCandidateCleanRangesFrom(bracketedErrorRates,trimWindows)){
            List<Double> candidateErrorRates = getSubList(bracketedErrorRates, candidateCleanRange);
            largestRanges.add(findLargestRangeThatPassesTotalAvgErrorRate(candidateErrorRates, candidateCleanRange));
        }
        
        Range largestRange= getLargestRangeFrom(largestRanges);
        return largestRange;
    }


    private List<Double> getSubList(List<Double> errorRates,
            Range region) {
        return errorRates.subList((int)region.getStart(), (int)region.getEnd()+1);
    }


    private List<Double> convertToErrorRates(Sequence<PhredQuality> qualities){
        List<Double> errorRates = new ArrayList<Double>((int)qualities.getLength());
        for(PhredQuality quality : qualities.decode()){
            errorRates.add(quality.getErrorProbability());
        }
        return errorRates;
    }

    private Range findLargestRangeThatPassesTotalAvgErrorRate(List<Double> encodedCandidateErrorRates,
            Range candidateCleanRange) {
        long currentWindowSize = candidateCleanRange.getLength();
        boolean done=false;
        while(!done && currentWindowSize >=SIZE_OF_ENDS){
           
            for(int i=0; i<encodedCandidateErrorRates.size() - currentWindowSize && i<=currentWindowSize; i++){
                Range currentWindowRange = Range.buildRange(i, currentWindowSize);
                double avgErrorRate = this.computeAvgErrorRateOf(encodedCandidateErrorRates, currentWindowRange);
                double leftEndErrorRate = this.computeAvgErrorRateOf(encodedCandidateErrorRates, Range.buildRangeOfLength(currentWindowRange.getStart(),SIZE_OF_ENDS));
                double rightEndErrorRate = this.computeAvgErrorRateOf(encodedCandidateErrorRates, Range.buildRangeOfLength(currentWindowRange.getEnd()-SIZE_OF_ENDS,SIZE_OF_ENDS));
                if(avgErrorRate <= this.maxTotalAvgError && 
                        leftEndErrorRate <= this.maxErrorAtEnds 
                        && rightEndErrorRate <= this.maxErrorAtEnds){
                    //found a good range!
                    return currentWindowRange;
                }
            }
            currentWindowSize--;
        }
        return Range.buildEmptyRange(candidateCleanRange.getStart());
    }


    /**
     * @param decode
     * @return
     */
    private List<Range> findCandidateCleanRangesFrom(List<Double> bracketedErrorRates, Set<Window> slidingWindows) {
        Iterator<Window> iterator = slidingWindows.iterator();
        
        Window firstTrimWindow = iterator.next();
        List<Range> candidateCleanRanges = trim(bracketedErrorRates, firstTrimWindow);
        while(iterator.hasNext()){
            Window subsequentTrimWindow = iterator.next();
            List<Range> trimmedCandidateCleanRanges = new ArrayList<Range>();
            for(Range range : candidateCleanRanges){
                for(Range newCandidateRange : trim(getSubList(bracketedErrorRates,range), subsequentTrimWindow)){
                    trimmedCandidateCleanRanges.add(newCandidateRange.shiftRight(range.getStart()));
                }
            }
            candidateCleanRanges = trimmedCandidateCleanRanges;
        }
        return candidateCleanRanges;
    }


    /**
     * @param bracketedQualities
     * @param trimWindow
     * @return
     */
    private List<Range> trim(List<Double> errorRates, Window trimWindow) {
        List<Range> candidateCleanRanges = new ArrayList<Range>();
        for(long i=0; i<errorRates.size()-trimWindow.getSize(); i++){
            Range windowRange = Range.buildRangeOfLength(i, trimWindow.getSize());
            
            double avgErrorRate = computeAvgErrorRateOf(errorRates,windowRange);
            if(avgErrorRate <= trimWindow.getMaxErrorRate()){
                candidateCleanRanges.add(windowRange);
            }
        }
        return Range.mergeRanges(candidateCleanRanges);
    }


    /**
     * The {@code bracket} is the resulting
     * region of qualities once it is trimmed to remove the low
     * quality data from each end.
     * @param qualities
     * @return
     */
    private Range findBracketedRegion(List<Double> errorRates) {
        long leftCoordinate = findLeftBracketCoordinate(errorRates);
        long rightCoordinate = findRightBracketCoordinate(errorRates);
        //right could be several hundred bases smaller
        //so we must check before we pass to build range
        //buildRange will throw an exception if left >= right-1
        if(leftCoordinate > rightCoordinate-2){
            return Range.buildEmptyRange();
        }
        return Range.buildRange(leftCoordinate, rightCoordinate);
    }
    /**
     * @param qualities
     * @return
     */
    private long findRightBracketCoordinate(List<Double> errorRates) {
        long coordinate=errorRates.size()-1;
        final int bracketSize = bracketWindow.getSize();
        while(coordinate >= bracketSize){
            Range windowRange = Range.buildRangeOfLength(coordinate-bracketSize,bracketSize);
            double avgErrorRate = computeAvgErrorRateOf(errorRates,windowRange);
            if(avgErrorRate <= bracketWindow.getMaxErrorRate()){
                return coordinate;
            }
            coordinate--;
        }
        return coordinate;
    }


    /**
     * @param qualities
     * @return
     */
    private int findLeftBracketCoordinate(List<Double> errorRates) {
        int coordinate=0;
        final int bracketSize = bracketWindow.getSize();
        while(coordinate < errorRates.size()- bracketSize){
            Range windowRange = Range.buildRangeOfLength(coordinate, bracketSize);
            double avgErrorRate = computeAvgErrorRateOf(errorRates,windowRange);
            if(avgErrorRate <= bracketWindow.getMaxErrorRate()){
                return coordinate;
            }
            coordinate++;
        }
        return coordinate;
    }


    private double computeAvgErrorRateOf(List<Double> errorRates,
            Range windowRange) {
         double totalErrorRate = 0;
         for(double errorRate : getSubList(errorRates, windowRange)){
             totalErrorRate+= errorRate;
         }
         
         return totalErrorRate/windowRange.size();
     }
    
    private Range getLargestRangeFrom(List<Range> goodQualityRanges) {
        if(goodQualityRanges.isEmpty()){
            return Range.buildEmptyRange();
        }
        List<Range> sorted = new ArrayList<Range>(goodQualityRanges);
        Collections.sort(sorted, Range.Comparators.LONGEST_TO_SHORTEST);
        return sorted.get(0);
        
    }
    
    public static final class Window implements Comparable<Window>{
        private final int size;
        private final double maxErrorRate;
        /**
         * @param size
         * @param minAvgQuality
         */
        public Window(int size, double maxErrorRate) {
            this.size = size;
            this.maxErrorRate = maxErrorRate;
        }
        public int getSize() {
            return size;
        }
        public double getMaxErrorRate() {
            return maxErrorRate;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(maxErrorRate);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + size;
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
            if (!(obj instanceof Window)) {
                return false;
            }
            Window other = (Window) obj;
            if (Double.doubleToLongBits(maxErrorRate) != Double
                    .doubleToLongBits(other.maxErrorRate)) {
                return false;
            }
            if (size != other.size) {
                return false;
            }
            return true;
        }
        
        
        @Override
        public String toString() {
            return "Window [size=" + size + ", maxErrorRate=" + maxErrorRate
                    + "]";
        }
        /**
        * Compares 2 {@link Window}s sorted largest window size first,
        * then by largest maxErrorRate.
        */
        @Override
        public int compareTo(Window o) {
            int sizeCmp= Integer.valueOf(o.getSize()).compareTo(size);
            if(sizeCmp !=0){
                return sizeCmp;
            }
           
            return Double.valueOf(o.getMaxErrorRate()).compareTo(maxErrorRate);
        }
        
    }
    /**
     * {@code Builder}  builds a {@link LucyLikeQualityTrimmer} instance
     * with the given trimming windows.
     * @author dkatzel
     *
     *
     */
    public static class Builder implements org.jcvi.common.core.util.Builder<LucyLikeQualityTrimmer>{

        public static final Window DEFAULT_BRACKET_WINDOW = new Window(10, 0.02D);
        public static final int DEFAULT_MIN_GOOD_LENGTH = 100;
        private static final List<Window> DEFAULT_TRIM_WINDOWS = Arrays.asList(
                                                        new Window(50,0.08D),
                                                        new Window(10,0.3D));
        public static final double DEFAULT_MAX_AVG_ERROR = 0.025D;
        public static final double DEFAULT_ERROR_AT_ENDS = 0.02D;
        
        private int minGoodLength;
        private Window bracketWindow;
        private double maxAvgError;
        private double maxErrorAtEnds;
        
        private final Set<Window> trimWindows = new TreeSet<Window>();
        
        public Builder(int minGoodLength, Window bracketWindow){
            this.minGoodLength = minGoodLength;
            this.bracketWindow = bracketWindow;
            this.maxAvgError = DEFAULT_MAX_AVG_ERROR;
            this.maxErrorAtEnds = DEFAULT_ERROR_AT_ENDS;
            
        }
        public Builder maxAvgError(double maxAvgError){
            this.maxAvgError = maxAvgError;
            return this;
        }
        public Builder maxErrorAtEnds(double maxErrorAtEnds){
            this.maxErrorAtEnds = maxErrorAtEnds;
            return this;
        }
        public Builder(int minGoodLength){
            this(minGoodLength, DEFAULT_BRACKET_WINDOW);
        }
        public Builder(){
            this(DEFAULT_MIN_GOOD_LENGTH, DEFAULT_BRACKET_WINDOW);
        }
        public Builder addTrimWindow(Window window){
            if(window ==null){
                throw new NullPointerException("trimWindow can not be null");
            }
            trimWindows.add(window);
            return this;
        }
        public Builder addTrimWindow(int windowSize, double maxErrorRate){
            trimWindows.add(new Window(windowSize, maxErrorRate));
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public LucyLikeQualityTrimmer build() {
            if(trimWindows.isEmpty()){
                trimWindows.addAll(DEFAULT_TRIM_WINDOWS);
            }
            return new LucyLikeQualityTrimmer(minGoodLength, 
                                            bracketWindow, 
                                            trimWindows, 
                                            maxAvgError, 
                                            maxErrorAtEnds);
        }
        
    }
}
