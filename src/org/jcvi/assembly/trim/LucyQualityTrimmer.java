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

package org.jcvi.assembly.trim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;



/**
 * @author dkatzel
 *
 *
 */
public class LucyQualityTrimmer {
    private final int minGoodLength;
    private final Window bracketWindow;
    private final double maxAvgError;
    private final double maxErrorAtEnds;
    
    private final Set<Window> trimWindows;
    
    
    
    /**
     * @param minGoodLength
     * @param bracketWindow
     * @param trimWindows
     * @param maxAvgError
     * @param maxErrorAtEnds
     */
    private LucyQualityTrimmer(int minGoodLength, Window bracketWindow,
            Set<Window> trimWindows, double maxAvgError, double maxErrorAtEnds) {
        this.minGoodLength = minGoodLength;
        this.bracketWindow = bracketWindow;
        this.trimWindows = trimWindows;
        this.maxAvgError = maxAvgError;
        this.maxErrorAtEnds = maxErrorAtEnds;
    }
    
    
    public Range trim(EncodedGlyphs<PhredQuality> qualities){
        Range bracketedRegion = findBracketedRegion(qualities);
        final EncodedGlyphs<PhredQuality> encodedBracketedQualities = getEncodedQualitiesFor(qualities, bracketedRegion);
        List<Range> largestRanges = new ArrayList<Range>();
        for(Range candidateCleanRange : findCandidateCleanRangesFrom(encodedBracketedQualities)){
            EncodedGlyphs<PhredQuality> encodedCandidateQualities = getEncodedQualitiesFor(encodedBracketedQualities, candidateCleanRange);
            largestRanges.add(findLargestRangeThatMeetErrorRate(encodedCandidateQualities, candidateCleanRange));
        }
        
        Range largestRange= getLargestRangeFrom(largestRanges);
        if(largestRange.size() < minGoodLength){
            return Range.buildEmptyRange(CoordinateSystem.RESIDUE_BASED,1);
        }
        return largestRange.shiftRight(bracketedRegion.getStart()).convertRange(CoordinateSystem.RESIDUE_BASED);
    }


    /**
     * @param encodedBracketedQualities
     * @param candidateCleanRange
     * @return
     */
    private Range findLargestRangeThatMeetErrorRate(EncodedGlyphs<PhredQuality> encodedCandidateQualities,
            Range candidateCleanRange) {
        long currentWindowSize = candidateCleanRange.getLength();
        boolean done=false;
        while(!done && currentWindowSize >3){
            for(int i=0; i<encodedCandidateQualities.getLength() - currentWindowSize; i++){
                Range currentWindowRange = Range.buildRange(i, currentWindowSize);
                double avgErrorRate = this.computeAvgErrorRateOf(encodedCandidateQualities, currentWindowRange);
                double leftEndErrorRate = this.computeAvgErrorRateOf(encodedCandidateQualities, Range.buildRangeOfLength(currentWindowRange.getStart(),2));
                double rightEndErrorRate = this.computeAvgErrorRateOf(encodedCandidateQualities, Range.buildRangeOfLength(currentWindowRange.getEnd()-2,2));
                if(avgErrorRate <= this.maxAvgError && leftEndErrorRate <= this.maxErrorAtEnds && rightEndErrorRate <= this.maxErrorAtEnds){
                    //found a good range!
                    return currentWindowRange;
                }
            }
            currentWindowSize--;
        }
        return Range.buildEmptyRange(candidateCleanRange.getStart());
    }


    private EncodedGlyphs<PhredQuality> getEncodedQualitiesFor(
            EncodedGlyphs<PhredQuality> qualities, Range bracketedRegion) {
        return new DefaultEncodedGlyphs<PhredQuality>(
                RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, qualities.decode(bracketedRegion));
    }

    /**
     * @param decode
     * @return
     */
    private List<Range> findCandidateCleanRangesFrom(EncodedGlyphs<PhredQuality> bracketedQualities) {
        Iterator<Window> iterator = trimWindows.iterator();
        
        Window firstTrimWindow = iterator.next();
        List<Range> candidateCleanRanges = trim(bracketedQualities, firstTrimWindow);
        while(iterator.hasNext()){
            Window subsequentTrimWindow = iterator.next();
            List<Range> trimmedCandidateCleanRanges = new ArrayList<Range>();
            for(Range range : candidateCleanRanges){
                for(Range newCandidateRange : trim(getEncodedQualitiesFor(bracketedQualities,range), subsequentTrimWindow)){
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
    private List<Range> trim(EncodedGlyphs<PhredQuality> bracketedQualities, Window trimWindow) {
        List<Range> candidateCleanRanges = new ArrayList<Range>();
        for(long i=0; i<bracketedQualities.getLength()-trimWindow.getSize(); i++){
            Range windowRange = Range.buildRangeOfLength(i, trimWindow.getSize());
            
            double avgErrorRate = computeAvgErrorRateOf(bracketedQualities,windowRange);
            if(avgErrorRate <= trimWindow.getMaxErrorRate()){
                candidateCleanRanges.add(windowRange);
            }
        }
        return Range.mergeRanges(candidateCleanRanges);
    }


    /**
     * @param qualities
     * @return
     */
    private Range findBracketedRegion(EncodedGlyphs<PhredQuality> qualities) {
        long leftCoordinate = findLeftBracket(qualities);
        long rightCoordinate = findRightBracket(qualities);
        return Range.buildRange(leftCoordinate, rightCoordinate);
    }
    /**
     * @param qualities
     * @return
     */
    private long findRightBracket(EncodedGlyphs<PhredQuality> qualities) {
        long coordinate=qualities.getLength()-1;
        final int bracketSize = bracketWindow.getSize();
        while(coordinate >= bracketSize){
            Range windowRange = Range.buildRangeOfLength(coordinate-bracketSize,coordinate);
            double avgErrorRate = computeAvgErrorRateOf(qualities,windowRange);
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
    private int findLeftBracket(EncodedGlyphs<PhredQuality> qualities) {
        int coordinate=0;
        final int bracketSize = bracketWindow.getSize();
        while(coordinate < qualities.getLength()- bracketSize){
            Range windowRange = Range.buildRangeOfLength(coordinate, bracketSize);
            double avgErrorRate = computeAvgErrorRateOf(qualities,windowRange);
            if(avgErrorRate <= bracketWindow.getMaxErrorRate()){
                return coordinate;
            }
            coordinate++;
        }
        return coordinate;
    }


    private double computeAvgErrorRateOf(EncodedGlyphs<PhredQuality> qualities,
            Range windowRange) {
         double totalQuality = 0;
         for(PhredQuality quality : qualities.decode(windowRange)){
             totalQuality+= quality.getErrorProbability();
         }
         
         double avgErrorRate = totalQuality/windowRange.size();
         return avgErrorRate;
     }
    
    private Range getLargestRangeFrom(List<Range> goodQualityRanges) {
        Range largestRangeSoFar = goodQualityRanges.get(0);
           for(int i=1; i<goodQualityRanges.size();i++ ){
               Range currentRange =goodQualityRanges.get(i);
               if(currentRange.size()> largestRangeSoFar.size()){
                   largestRangeSoFar = currentRange;
               }
           }
        return largestRangeSoFar;
    }
    
    final static class Window implements Comparable<Window>{
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
    
    public static class Builder implements org.jcvi.Builder<LucyQualityTrimmer>{

        private static final Window DEFAULT_BRACKET_WINDOW = new Window(10, 0.02D);
        private static final int DEFAULT_MIN_GOOD_LENGTH = 100;
        private static final List<Window> DEFAULT_TRIM_WINDOWS = Arrays.asList(
                                                        new Window(50,0.08D),
                                                        new Window(10,0.3D));
        private static final double DEFAULT_MAX_AVG_ERROR = 0.025D;
        private static final double DEFAULT_ERROR_AT_ENDS = 0.02D;
        
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
        public Builder(int minGoodLength){
            this(minGoodLength, DEFAULT_BRACKET_WINDOW);
        }
        public Builder(){
            this(DEFAULT_MIN_GOOD_LENGTH, DEFAULT_BRACKET_WINDOW);
        }
        
        public Builder addTrimWindow(int windowSize, double maxErrorRate){
            trimWindows.add(new Window(windowSize, maxErrorRate));
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public LucyQualityTrimmer build() {
            if(trimWindows.isEmpty()){
                trimWindows.addAll(DEFAULT_TRIM_WINDOWS);
            }
            return new LucyQualityTrimmer(minGoodLength, 
                                            bracketWindow, 
                                            trimWindows, 
                                            maxAvgError, 
                                            maxErrorAtEnds);
        }
        
    }
}
