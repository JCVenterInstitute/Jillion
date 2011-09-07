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

package org.jcvi.common.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Range.CoordinateSystem;


/**
 * @author dkatzel
 *
 *
 */
public final class DirectedRange implements Placed<DirectedRange>{
    
        /**
         * Regular expression in the form (left) .. (right).
         */
        private static Pattern DOT_PATTERN = Pattern.compile("(\\d+)\\s*\\.\\.\\s*(\\d+)");
        /**
         * Regular expression in the form (left) - (right).
         */
        private static Pattern DASH_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
        private static Pattern COMMA_PATTERN = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");
    
        private final Range range;
        private final Direction direction;
        
        public static DirectedRange parse(String rangeAsString, CoordinateSystem coordinateSystem){
            Matcher dotMatcher =DOT_PATTERN.matcher(rangeAsString);
            if(dotMatcher.find()){
                return convertIntoRange(dotMatcher,coordinateSystem);
            }
            Matcher dashMatcher = DASH_PATTERN.matcher(rangeAsString);
            if(dashMatcher.find()){
                return convertIntoRange(dashMatcher,coordinateSystem);
            }
            Matcher commaMatcher = COMMA_PATTERN.matcher(rangeAsString);
            if(commaMatcher.find()){
                return convertIntoRange(commaMatcher,coordinateSystem);
            }
            throw new IllegalArgumentException("can not parse "+ rangeAsString +" into a Range");
        }
        
        public static DirectedRange create(Range range){
            return create(range,Direction.FORWARD);
        }
        public static DirectedRange create(Range range, Direction direction){
            return new DirectedRange(range,direction);
        }
        private static DirectedRange convertIntoRange(Matcher dashMatcher, CoordinateSystem coordinateSystem) {
            long first = Long.parseLong(dashMatcher.group(1));
            long second = Long.parseLong(dashMatcher.group(2));
            if(first<second){
                Range range =Range.buildRange(coordinateSystem,first,second);
                return new DirectedRange(range, Direction.FORWARD);
            }
            Range range =Range.buildRange(coordinateSystem,second,first);
            return new DirectedRange(range, Direction.REVERSE);
                    
        }
        public static DirectedRange parse(String rangeAsString){
            return parse(rangeAsString,CoordinateSystem.ZERO_BASED);
        }
        public static DirectedRange parse(String firstCoord, String secondCoord){
            return parse(firstCoord,secondCoord,CoordinateSystem.ZERO_BASED);
        }
        public static DirectedRange parse(String firstCoord, String secondCoord, CoordinateSystem coordinateSystem){
            int first = Integer.parseInt(firstCoord);
            int second = Integer.parseInt(secondCoord);
            if(first<second){
                Range range = Range.buildRange(coordinateSystem,first,second);
                return new DirectedRange(range, Direction.FORWARD);
            }
            
            Range range = Range.buildRange(coordinateSystem, second,first);
            return new DirectedRange(range, Direction.REVERSE);
        }
        
        private DirectedRange(Range range, Direction direction) {
            if(range ==null){
                throw new NullPointerException("range can not be null");
            }
            if(direction ==null){
                throw new NullPointerException("direction can not be null");
            }
            this.range = range;
            this.direction = direction;
        }

        /**
         * @return the range
         */
        public Range getRange() {
            return range;
        }

        /**
         * @return the direction
         */
        public Direction getDirection() {
            return direction;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + direction.hashCode();
            result = prime * result + range.hashCode();
            return result;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DirectedRange)) {
                return false;
            }
            DirectedRange other = (DirectedRange) obj;
            if (direction != other.direction) {
                return false;
            }
            if (!range.equals(other.range)) {
                return false;
            }
            return true;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String toString() {
            return "DirectedRange [range=" + range + ", direction=" + direction
                    + "]";
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int compareTo(DirectedRange o) {
            int rangeCmp= getRange().compareTo(o.getRange());
            if(rangeCmp!=0){
                return rangeCmp;
            }
            return getDirection().compareTo(o.getDirection());
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getStart() {
            return getRange().getStart();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getEnd() {
            return getRange().getEnd();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public long getLength() {
            return getRange().getLength();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Range asRange() {
            return getRange();
        }

        
        
}
