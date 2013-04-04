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
package org.jcvi.jillion.core.qual.trim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.internal.core.util.JillionUtil;



/**
 * {@code LucyLikeQualityTrimmerBuilder} 
 * is a {@link org.jcvi.jillion.core.util.Builder}
 * that can create a {@link QualityTrimmer}
 * that  trims {@link QualitySequence}s
 * using the same algorithm as the TIGR program
 *  Lucy.  The algorithm 
 * this class uses is the algorithm from the 2001 Lucy paper in
 * Bioinformatics (link is below).
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
 * until each end and the total overall error rate meet these thresholds at the same time. 
 * </li>
 * 
 * The largest of the ranges after all these steps is the final trim range.
 * </ol>
 * @author dkatzel
 *
 * @see <a href ="http://www.ncbi.nlm.nih.gov/pubmed/11751217">
 Chou HH, Holmes MH. DNA sequence quality trimming and vector removal. Bioinformatics. 2001;17:1093-1104. doi: 10.1093/bioinformatics/17.12.1093.</a>
 */
public final class LucyQualityTrimmerBuilder implements org.jcvi.jillion.core.util.Builder<QualityTrimmer>{

        public static final Window DEFAULT_BRACKET_WINDOW = new Window(10, 0.02D);
        private static final List<Window> DEFAULT_TRIM_WINDOWS = Arrays.asList(
                                                        new Window(50,0.08D),
                                                        new Window(10,0.3D));
        public static final double DEFAULT_MAX_AVG_ERROR = 0.025D;
        public static final double DEFAULT_ERROR_AT_ENDS = 0.02D;
        
        private final int minGoodLength;
        private final Window bracketWindow;
        private double maxAvgError;
        private double maxErrorAtEnds;
        
        private final Set<Window> trimWindows = new TreeSet<Window>();
        /**
         * Create a new instance of {@code LucyLikeQualityTrimmerBuilder}
         * which is initialized to default max average error {@value #DEFAULT_MAX_AVG_ERROR}
         * @param minGoodLength the minimum good quality length of the sequences
         * to be trimmed. If after trimming by 
         * {@link QualityTrimmer#trim(QualitySequence)},
         *  the trimmed length is less than this specified
         * value, then an empty Range will be returned instead.
         */
        public LucyQualityTrimmerBuilder(int minGoodLength){
            this.minGoodLength = minGoodLength;
            this.bracketWindow = DEFAULT_BRACKET_WINDOW;
            this.maxAvgError = DEFAULT_MAX_AVG_ERROR;
            this.maxErrorAtEnds = DEFAULT_ERROR_AT_ENDS;
        }
        /**
         * Set the max average error of the entire trimmed sequence.
         * The sequence will be trimmed until the remaining portion
         * has a total average error rate <= this value.
         * @param maxAvgError the max average error to set; must be
         * between 0 and 1 inclusive.
         * @return this.
         * @throws IllegalArgumentException if the given value
         * is not between 0 and 1 inclusive.
         */
        public LucyQualityTrimmerBuilder maxAvgError(double maxAvgError){
        	isValidErrorRate(maxAvgError);
            this.maxAvgError = maxAvgError;
            return this;
        }
		private void isValidErrorRate(double maxAvgError) {
			if(maxAvgError <0D || maxAvgError >1D){
        		throw new IllegalArgumentException("max avg error must be between 0.0 and 1.0");
        	}
		}
		/**
         * Set the max error at each end of the 
         * trimmed sequence.  
         * The sequence will be trimmed until the first few
         * qualities on each end of the trimmed range
         * have an error rate <= this value.
         * @param maxErrorAtEdges the max error and end of the trimmed
         * sequence can have; error to set; must be
         * between 0 and 1 inclusive.
         * @return this.
         * @throws IllegalArgumentException if the given value
         * is not between 0 and 1 inclusive.
         */
        public LucyQualityTrimmerBuilder maxErrorAtEdges(double maxErrorAtEdges){
        	isValidErrorRate(maxErrorAtEdges);
            this.maxErrorAtEnds = maxErrorAtEdges;
            return this;
        }
        /**
         * Add an additional sliding trim window to this trimmer.
         * The final trimmed sequence must meet the requirements of ALL
         * given trim windows.  This Builder will correctly sort the trim windows
         * by size for the algorithm to work so users can add trim windows
         * in any order without affecting the output of this trimmer.
         * @param windowSize the size of this trimming window.
         * @param maxErrorRate the max error rate for all the qualities in this window size.
         * @return this.
         * @throws IllegalArgumentException if the maxErrorRate
         * is not between 0 and 1 inclusive.
         * @throws IllegalArgumentException if the windowSize
         * is <1.
         */
        public LucyQualityTrimmerBuilder addTrimWindow(int windowSize, double maxErrorRate){
        	isValidErrorRate(maxErrorRate);
        	if(windowSize <1){
        		throw new IllegalArgumentException("window size must be >= 1");
        	}
            trimWindows.add(new Window(windowSize, maxErrorRate));
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public QualityTrimmer build() {
            if(trimWindows.isEmpty()){
                trimWindows.addAll(DEFAULT_TRIM_WINDOWS);
            }
            return new LucyLikeQualityTrimmerImpl(minGoodLength, 
                                            bracketWindow, 
                                            trimWindows, 
                                            maxAvgError, 
                                            maxErrorAtEnds);
        }
        /**
         * Actual {@link QualityTrimmer} implementation that
         * trims according to the lucy algorithms.
         * @author dkatzel
         *
         */
        private static final class LucyLikeQualityTrimmerImpl  implements QualityTrimmer{
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
            private LucyLikeQualityTrimmerImpl(int minGoodLength, Window bracketWindow,
                    Set<Window> trimWindows, double maxAvgError, double maxErrorAtEnds) {
                this.minGoodLength = minGoodLength;
                this.bracketWindow = bracketWindow;
                this.trimWindows = trimWindows;
                this.maxTotalAvgError = maxAvgError;
                this.maxErrorAtEnds = maxErrorAtEnds;
            }
            
            @Override
            public Range trim(QualitySequence qualities){
                List<Double> errorRates = convertToErrorRates(qualities);
                Range bracketedRegion = findBracketedRegion(errorRates);
                Range largestRange = findLargestCleanRangeFrom(bracketedRegion, errorRates);
                if(largestRange.getLength() < minGoodLength){
                    return new Range.Builder().build();
                }
                return new Range.Builder(largestRange)
                			.shift(bracketedRegion.getBegin())
                			.build();
            }


            private Range findLargestCleanRangeFrom(Range bracketedRegion,
                    List<Double> errorRates) {
                List<Double> bracketedErrorRates = getSubList(errorRates, bracketedRegion);
                List<Range> largestRanges = new ArrayList<Range>();
                for(Range candidateCleanRange : findCandidateCleanRangesFrom(bracketedErrorRates,trimWindows)){
                    List<Double> candidateErrorRates = getSubList(bracketedErrorRates, candidateCleanRange);
                    largestRanges.add(findLargestRangeThatPassesTotalAvgErrorRate(candidateErrorRates, candidateCleanRange));
                }
                
                return getLargestRangeFrom(largestRanges);
            }


            private List<Double> getSubList(List<Double> errorRates,
                    Range region) {
                return errorRates.subList((int)region.getBegin(), (int)region.getEnd()+1);
            }


            private List<Double> convertToErrorRates(QualitySequence qualities){
                List<Double> errorRates = new ArrayList<Double>((int)qualities.getLength());
                for(PhredQuality quality : qualities){
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
                        Range currentWindowRange = Range.of(i, currentWindowSize);
                        double avgErrorRate = this.computeAvgErrorRateOf(encodedCandidateErrorRates, currentWindowRange);
                        Range leftRange = new Range.Builder(SIZE_OF_ENDS)
                        					.shift(currentWindowRange.getBegin())
                        					.build();
						double leftEndErrorRate = this.computeAvgErrorRateOf(encodedCandidateErrorRates, leftRange);
                        double rightEndErrorRate = this.computeAvgErrorRateOf(encodedCandidateErrorRates, new Range.Builder(SIZE_OF_ENDS).shift(currentWindowRange.getEnd()-SIZE_OF_ENDS).build());
                        if(avgErrorRate <= this.maxTotalAvgError 
                                && leftEndErrorRate <= this.maxErrorAtEnds 
                                && rightEndErrorRate <= this.maxErrorAtEnds){
                            //found a good range!
                            return currentWindowRange;
                        }
                    }
                    currentWindowSize--;
                }
                return new Range.Builder()
        					.shift(candidateCleanRange.getBegin())
        					.build();
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
                            trimmedCandidateCleanRanges.add(new Range.Builder(newCandidateRange)
                            								.shift(range.getBegin())
                            								.build());
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
                    Range windowRange = new Range.Builder(trimWindow.getSize()).shift(i).build();
                    
                    double avgErrorRate = computeAvgErrorRateOf(errorRates,windowRange);
                    if(avgErrorRate <= trimWindow.getMaxErrorRate()){
                        candidateCleanRanges.add(windowRange);
                    }
                }
                return Ranges.merge(candidateCleanRanges);
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
                    return new Range.Builder().build();
                }
                return Range.of(leftCoordinate, rightCoordinate);
            }
            /**
             * @param qualities
             * @return
             */
            private long findRightBracketCoordinate(List<Double> errorRates) {
                long coordinate=errorRates.size()-1;
                final int bracketSize = bracketWindow.getSize();
                while(coordinate >= bracketSize){
                    Range windowRange = new Range.Builder(bracketSize).shift(coordinate-bracketSize).build();
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
                    Range windowRange = new Range.Builder(bracketSize).shift(coordinate).build();
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
                 
                 return totalErrorRate/windowRange.getLength();
             }
            
            private Range getLargestRangeFrom(List<Range> goodQualityRanges) {
                if(goodQualityRanges.isEmpty()){
                    return new Range.Builder().build();
                }
                List<Range> sorted = new ArrayList<Range>(goodQualityRanges);
                Collections.sort(sorted, Range.Comparators.LONGEST_TO_SHORTEST);
                return sorted.get(0);
                
            }
    }
        /**
         * 
         * @author dkatzel
         *
         */
        private static final class Window implements Comparable<Window>{
            private final int size;
            private final double maxErrorRate;
            /**
             * @param size
             * @param maxErrorRate
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
            	
                int sizeCmp= JillionUtil.compare(o.getSize(),size);
                if(sizeCmp !=0){
                    return sizeCmp;
                }
                return Double.compare(o.getMaxErrorRate(), maxErrorRate);
            }
            
        }

}
