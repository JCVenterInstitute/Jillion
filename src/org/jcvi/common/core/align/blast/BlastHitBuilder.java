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

package org.jcvi.common.core.align.blast;

import java.math.BigDecimal;

import org.jcvi.common.core.DirectedRange;

/**
 * @author dkatzel
 *
 *
 */
public final class BlastHitBuilder implements org.jcvi.common.core.util.Builder<BlastHit>{

    public static BlastHitBuilder create(String queryId){
        return new BlastHitBuilder(queryId);
    }
    public static BlastHitBuilder copy(BlastHit copy){
        return new BlastHitBuilder(copy);
    }

        private String queryId;
        private String subjectId;
        private Double percentIdentity;
        private BigDecimal eValue, bitScore;
        private DirectedRange queryRange, subjectRange;
        private Integer numGapsOpenings ,numMismatches,alignmentLength;
        
        private BlastHitBuilder(BlastHit copy) {
           
            bitScore(copy.getBitScore())
            .eValue(copy.getEvalue())
            .numGapOpenings(copy.getNumberOfGapOpenings())
            .numMismatches(copy.getNumberOfMismatches())
            .percentIdentity(copy.getPercentIdentity())
            .queryRange(copy.getQueryRange())
            .query(copy.getQueryId())
            .subject(copy.getSubjectId())
            .subjectRange(copy.getSubjectRange())
            .alignmentLength(copy.getAlignmentLength())
            ;
        }
        private BlastHitBuilder(String queryId) {
            query(queryId);
        }

        public BlastHitBuilder query(String queryId) {
            if(queryId ==null){
                throw new NullPointerException("query id can not be null");
            }
            String trimmed = queryId.trim();
            if(trimmed.isEmpty()){
                throw new IllegalArgumentException("query id can not be empty");
            }
            this.queryId = trimmed;
            return this;
        }
        
        public BlastHitBuilder subject(String subjectId){
            if(subjectId ==null){
                throw new NullPointerException("subject id can not be null");
            }
            String trimmed = subjectId.trim();
            if(trimmed.isEmpty()){
                throw new IllegalArgumentException("query id can not be empty");
            }
            this.subjectId = trimmed;
            return this;
        }

        public BlastHitBuilder percentIdentity(double percentIdentity){            
            if(percentIdentity <0){
                throw new IllegalArgumentException("percentIdentity score must be positive: " + percentIdentity);
            }
            if(percentIdentity >100.0D){
                throw new IllegalArgumentException("percentIdentity score must be <= 100: " + percentIdentity);
            }
            this.percentIdentity = percentIdentity;
            return this;
        }
        public BlastHitBuilder bitScore(BigDecimal bitScore){  
            if(bitScore ==null){
                throw new NullPointerException("bit score can not be null");
            }
            if(bitScore.compareTo(BigDecimal.ZERO) <0){
                throw new IllegalArgumentException("bit score must be positive: " + bitScore);
            }
            this.bitScore = bitScore;
            return this;
        }
        
        public BlastHitBuilder queryRange(DirectedRange queryRange){
            if(queryRange ==null){
                throw new NullPointerException("queryRange can not be null");
            }
            this.queryRange = queryRange;
            return this;
        }

        public BlastHitBuilder subjectRange(DirectedRange subjectRange){
            if(subjectRange ==null){
                throw new NullPointerException("subjectRange can not be null");
            }
            this.subjectRange = subjectRange;
            return this;
        }
        public BlastHitBuilder eValue(BigDecimal eValue){
            if(eValue ==null){
                throw new NullPointerException("e-value can not be null");
            }
            if(eValue.compareTo(BigDecimal.ZERO)<0){
                throw new IllegalArgumentException("e-value score must be positive: " + eValue);
            }
            this.eValue = eValue;
            return this;
        }
        
        public BlastHitBuilder numGapOpenings(int numberOfGapOpenings){
            if(numberOfGapOpenings<0){
                throw new IllegalArgumentException("number of gap openings can not be negative : " + numberOfGapOpenings);
            }
            this.numGapsOpenings = numberOfGapOpenings;
            return this;
        }
        public BlastHitBuilder numMismatches(int numberOfMismatches){
            if(numberOfMismatches<0){
                throw new IllegalArgumentException("number of mismatches can not be negative : " + numberOfMismatches);
            }
            this.numMismatches = numberOfMismatches;
            return this;
        }
        public BlastHitBuilder alignmentLength(int alignmentLength){
            if(alignmentLength<0){
                throw new IllegalArgumentException("alignment length can not be negative : " + alignmentLength);
            }
            this.alignmentLength = alignmentLength;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public BlastHit build() {
            verifyAllValuesSet();
            return new BlastHitImpl(queryId, subjectId, 
                    percentIdentity, bitScore, 
                    eValue, 
                    queryRange, subjectRange, 
                    numGapsOpenings, numMismatches, alignmentLength);
        }

        /**
         * 
         */
        private void verifyAllValuesSet() {
            if(subjectId == null){
                throw new IllegalStateException("must set subject id");
            }
            if(percentIdentity==null){
                throw new IllegalStateException("must set percent identity");
            }
            if(bitScore==null){
                throw new IllegalStateException("must set bit score");
            }
            if(eValue==null){
                throw new IllegalStateException("must set e-value");
            }
            if(queryRange==null){
                throw new IllegalStateException("must set query range");
            }
            if(subjectRange==null){
                throw new IllegalStateException("must set subject range");
            }
            if(numGapsOpenings==null){
                throw new IllegalStateException("must set number of gap openings");
            }
            if(numMismatches==null){
                throw new IllegalStateException("must set number of mismatches");
            }
            if(alignmentLength==null){
                throw new IllegalStateException("must set alignment length");
            }
        }
        
    

    private static class BlastHitImpl implements BlastHit{
        
    private final String queryId,subjectId;
    private final double percentIdentity;
    private final BigDecimal eValue, bitScore;
    private final DirectedRange queryRange, subjectRange;
    private final int numGapsOpenings,numMismatches;
    private final int alignmentLength;
    
    private BlastHitImpl(String queryId, String subjectId,
            double percentIdentity, BigDecimal bitScore, BigDecimal eValue,
            DirectedRange queryRange, DirectedRange subjectRange, int numGapsOpenings,
            int numMismatches, int alignmentLength) {
        this.queryId = queryId;
        this.subjectId = subjectId;
        this.percentIdentity = percentIdentity;
        this.bitScore = bitScore;
        this.eValue = eValue;
        this.queryRange = queryRange;
        this.subjectRange = subjectRange;
        this.numGapsOpenings = numGapsOpenings;
        this.numMismatches = numMismatches;
        this.alignmentLength = alignmentLength;
    }


        /**
    * {@inheritDoc}
    */
    @Override
    public String toString() {
        return "BlastHit [queryId=" + queryId + ", subjectId=" + subjectId
                + ", percentIdentity=" + percentIdentity + ", bitScore="
                + bitScore + ", eValue=" + eValue + ", queryRange="
                + queryRange + ", subjectRange=" + subjectRange
                + ", numGapsOpenings=" + numGapsOpenings + ", numMismatches="
                + numMismatches + ", alignmentLength=" + alignmentLength + "]";
    }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getQueryId() {
            return queryId;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public String getSubjectId() {
            return subjectId;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public double getPercentIdentity() {
            return percentIdentity;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public int getAlignmentLength() {
            return alignmentLength;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public int getNumberOfMismatches() {
            return numMismatches;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public int getNumberOfGapOpenings() {
            return numGapsOpenings;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public DirectedRange getQueryRange() {
            return queryRange;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public DirectedRange getSubjectRange() {
            return subjectRange;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public BigDecimal getEvalue() {
            return eValue;
        }
    
        /**
        * {@inheritDoc}
        */
        @Override
        public BigDecimal getBitScore() {
            return bitScore;
        }
    
        
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + alignmentLength;
            long temp;
            temp =  prime * result + bitScore.hashCode();
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + eValue.hashCode();
            result = prime * result + numGapsOpenings;
            result = prime * result + numMismatches;
            temp = Double.doubleToLongBits(percentIdentity);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + queryId.hashCode();
            result = prime * result
                    + queryRange.hashCode();
            result = prime * result
                    + subjectId.hashCode();
            result = prime * result
                    + subjectRange.hashCode();
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
            if (!(obj instanceof BlastHitImpl)) {
                return false;
            }
            BlastHitImpl other = (BlastHitImpl) obj;
            if (alignmentLength != other.alignmentLength) {
                return false;
            }
            if (!bitScore.equals(other.bitScore)) {
                return false;
            }
            if (!eValue.equals(other.eValue)) {
                return false;
            }
            if (numGapsOpenings != other.numGapsOpenings) {
                return false;
            }
            if (numMismatches != other.numMismatches) {
                return false;
            }
            if (Double.doubleToLongBits(percentIdentity) != Double
                    .doubleToLongBits(other.percentIdentity)) {
                return false;
            }
            if (!queryId.equals(other.queryId)) {
                return false;
            }
            if (!queryRange.equals(other.queryRange)) {
                return false;
            }
            if (!subjectId.equals(other.subjectId)) {
                return false;
            }
            if (!subjectRange.equals(other.subjectRange)) {
                return false;
            }
            return true;
        }
    }
}
