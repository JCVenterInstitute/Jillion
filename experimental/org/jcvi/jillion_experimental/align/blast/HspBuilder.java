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
package org.jcvi.jillion_experimental.align.blast;

import java.math.BigDecimal;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public final class HspBuilder<R extends Residue, S extends Sequence<R>> implements org.jcvi.jillion.core.util.Builder<Hsp<R,S>>{

    private static final double ONE_HUNDRED = 100.0D;


        private String queryId;
        private String subjectId;
        private Double percentIdentity;
        private BigDecimal eValue, bitScore;
        private DirectedRange queryRange, subjectRange;
        private Integer numGapsOpenings ,numMismatches,alignmentLength;
        private S queryAlignment, subjectAlignment;
        
        
        /**
         * Create a new {@link HspBuilder} for BLASTN (Nucleotide query to Nucleotide subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<Nucleotide,NucleotideSequence> forBlastN(){
        	return new HspBuilder<Nucleotide,NucleotideSequence>();
        }
        /**
         * Create a new {@link HspBuilder} for BLASTP (Protein query to Protein subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence> forBlastP(){
        	return new HspBuilder<AminoAcid,ProteinSequence>();
        }
        /**
         * Create a new {@link HspBuilder} for BLASTX (Nucleotide (translated) query to Protein subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence> forBlastX(){
        	return new HspBuilder<AminoAcid,ProteinSequence>();
        }
        /**
         * Create a new {@link HspBuilder} for TBLASTX (Nucleotide (translated) query to Nucleotide (translated) subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence> forTBlastX(){
        	return new HspBuilder<AminoAcid,ProteinSequence>();
        }
        /**
         * Create a new {@link HspBuilder} for TBLASTN (Protein query to Nucleotide (translated) subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence> forTBlastN(){
        	return new HspBuilder<AminoAcid,ProteinSequence>();
        }
        /**
         * Create a new {@link HspBuilder} instance for the given type
         * by name ("blastn", "blastp" etc).
         * @param type the name of the type, case-insensitive; can not be null.
         * @return  a new {@link HspBuilder} will never be null.
         * @throws NullPointerException if type is null.
         * @throws IllegalArgumentException if type is not
         * equal-ignoring-case with "blastn", "blastp", "blastx", "tblastn",  or "tblastp".
         */
        public static HspBuilder<?,?> forType(String type){
        	if(type==null){
        		throw new NullPointerException("type can not be null");
        	}
        	if("blastn".equalsIgnoreCase(type)){
        		return forBlastN();
        	}
        	if("blastp".equalsIgnoreCase(type)){
        		return forBlastP();
        	}
        	if("blastx".equalsIgnoreCase(type)){
        		return forBlastX();
        	}
        	if("tblastx".equalsIgnoreCase(type)){
        		return forTBlastX();
        	}
        	if("tblastn".equalsIgnoreCase(type)){
        		return forTBlastN();
        	}
        	throw new IllegalArgumentException("unknown type :" + type);
        }
    	
        public static <R extends Residue, S extends Sequence<R>> HspBuilder<R,S> copy(Hsp<R,S> hsp){
            return new HspBuilder<R,S>(hsp);
        }
        
        /**
         * Create a new instance of {@link HspBuilder}
         * that is an exact copy of this instance.
         * Any future modifications to either builder
         * will not affect the other. 
         * @return  a new {@link HspBuilder} will never be null.
         */
        public  HspBuilder<R,S> copy(){
            return new HspBuilder<R,S>(this);
        }
        
        
        
        private HspBuilder(HspBuilder<R,S> copy){
        	this.bitScore =copy.bitScore;
        	this.eValue = copy.eValue;
        	this.numGapsOpenings = copy.numGapsOpenings;
        	this.numMismatches = copy.numMismatches;
            this.percentIdentity = copy.percentIdentity;
            this.queryRange = copy.queryRange;
            this.subjectRange = copy.subjectRange;
            this.queryAlignment =copy.queryAlignment;
            this.queryId = copy.queryId;
            this.subjectAlignment = copy.subjectAlignment;
            this.subjectId = copy.subjectId;
            this.alignmentLength = copy.alignmentLength;
            
        }
        private HspBuilder(Hsp<R,S> copy) {
           
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
            
            if(copy.hasAlignments()){
            	gappedAlignments(copy.getGappedQueryAlignment(), copy.getGappedSubjectAlignment());
            }
        }
        private HspBuilder(){
        	
        }
        private HspBuilder(String queryId) {
            query(queryId);
        }
        public HspBuilder<R,S> gappedAlignments(S queryAlignment, S subjectAlignment) {
            if((queryAlignment ==null && subjectAlignment !=null) 
            		|| (subjectAlignment ==null && queryAlignment !=null)){
                throw new NullPointerException("gapped alignments must be either both null or neither null");
            }
            this.queryAlignment = queryAlignment;
            this.subjectAlignment = subjectAlignment;
            return this;
        }
        public HspBuilder<R,S>  query(String queryId) {
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
        
        public HspBuilder<R,S>  subject(String subjectId){
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

        public HspBuilder<R,S>  percentIdentity(double percentIdentity){            
            if(percentIdentity <0){
                throw new IllegalArgumentException("percentIdentity score must be positive: " + percentIdentity);
            }
            if(percentIdentity >ONE_HUNDRED){
                throw new IllegalArgumentException("percentIdentity score must be <= 100: " + percentIdentity);
            }
            this.percentIdentity = percentIdentity;
            return this;
        }
        public HspBuilder<R,S>  bitScore(BigDecimal bitScore){  
            if(bitScore ==null){
                throw new NullPointerException("bit score can not be null");
            }
            if(bitScore.compareTo(BigDecimal.ZERO) <0){
                throw new IllegalArgumentException("bit score must be positive: " + bitScore);
            }
            this.bitScore = bitScore;
            return this;
        }
        
        public HspBuilder<R,S>  queryRange(DirectedRange queryRange){
            if(queryRange ==null){
                throw new NullPointerException("queryRange can not be null");
            }
            this.queryRange = queryRange;
            return this;
        }

        public HspBuilder<R,S>  subjectRange(DirectedRange subjectRange){
            if(subjectRange ==null){
                throw new NullPointerException("subjectRange can not be null");
            }
            this.subjectRange = subjectRange;
            return this;
        }
        public HspBuilder<R,S>  eValue(BigDecimal eValue){
            if(eValue ==null){
                throw new NullPointerException("e-value can not be null");
            }
            if(eValue.compareTo(BigDecimal.ZERO)<0){
                throw new IllegalArgumentException("e-value score must be positive: " + eValue);
            }
            this.eValue = eValue;
            return this;
        }
        
        public HspBuilder<R,S>  numGapOpenings(int numberOfGapOpenings){
            if(numberOfGapOpenings<0){
                throw new IllegalArgumentException("number of gap openings can not be negative : " + numberOfGapOpenings);
            }
            this.numGapsOpenings = numberOfGapOpenings;
            return this;
        }
        public HspBuilder<R,S>  numMismatches(int numberOfMismatches){
            if(numberOfMismatches<0){
                throw new IllegalArgumentException("number of mismatches can not be negative : " + numberOfMismatches);
            }
            this.numMismatches = numberOfMismatches;
            return this;
        }
        public HspBuilder<R,S>  alignmentLength(int alignmentLength){
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
        public Hsp<R,S>  build() {
            verifyAllValuesSet();
            return new BlastHitImpl<R,S> (queryId, subjectId, 
                    percentIdentity, bitScore, 
                    eValue, 
                    queryRange, subjectRange, 
                    numGapsOpenings, numMismatches, alignmentLength,
                    queryAlignment, subjectAlignment);
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
        
    

    private static final class BlastHitImpl<R extends Residue, S extends Sequence<R>> implements Hsp<R,S>{
        
    private final String queryId,subjectId;
    private final double percentIdentity;
    private final BigDecimal eValue, bitScore;
    private final DirectedRange queryRange, subjectRange;
    private final int numGapsOpenings,numMismatches;
    private final int alignmentLength;
    private final S queryAlignment;
    private final S subjectAlignment;
    
    private BlastHitImpl(String queryId, String subjectId,
            double percentIdentity, BigDecimal bitScore, BigDecimal eValue,
            DirectedRange queryRange, DirectedRange subjectRange, int numGapsOpenings,
            int numMismatches, int alignmentLength,
            S queryAlignment,S subjectAlignment) {
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
        this.queryAlignment = queryAlignment;
        this.subjectAlignment = subjectAlignment;
    }


        /**
    * {@inheritDoc}
    */
    @Override
    public boolean hasAlignments() {
        return queryAlignment!=null;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    public S getGappedQueryAlignment() {
        return queryAlignment;
    }


    /**
    * {@inheritDoc}
    */
    @Override
    public S getGappedSubjectAlignment() {
        return subjectAlignment;
    }


        /**
        * {@inheritDoc}
        */
        @Override
        public String toString() {
            return "BlastHitImpl [queryId=" + queryId + ", subjectId="
                    + subjectId + ", percentIdentity=" + percentIdentity
                    + ", eValue=" + eValue + ", bitScore=" + bitScore
                    + ", queryRange=" + queryRange + ", subjectRange="
                    + subjectRange + ", numGapsOpenings=" + numGapsOpenings
                    + ", numMismatches=" + numMismatches + ", alignmentLength="
                    + alignmentLength + ", queryAlignment=" + queryAlignment
                    + ", subjectAlignment=" + subjectAlignment + "]";
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
            result = prime * result
                    + ((bitScore == null) ? 0 : bitScore.hashCode());
            result = prime * result
                    + ((eValue == null) ? 0 : eValue.hashCode());
            result = prime * result + numGapsOpenings;
            result = prime * result + numMismatches;
            long temp;
            temp = Double.doubleToLongBits(percentIdentity);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime
                    * result
                    + ((queryAlignment == null) ? 0 : queryAlignment.hashCode());
            result = prime * result
                    + ((queryId == null) ? 0 : queryId.hashCode());
            result = prime * result
                    + ((queryRange == null) ? 0 : queryRange.hashCode());
            result = prime
                    * result
                    + ((subjectAlignment == null) ? 0 : subjectAlignment
                            .hashCode());
            result = prime * result
                    + ((subjectId == null) ? 0 : subjectId.hashCode());
            result = prime * result
                    + ((subjectRange == null) ? 0 : subjectRange.hashCode());
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
            if (bitScore == null) {
                if (other.bitScore != null) {
                    return false;
                }
            } else if (!bitScore.equals(other.bitScore)) {
                return false;
            }
            if (eValue == null) {
                if (other.eValue != null) {
                    return false;
                }
            } else if (!eValue.equals(other.eValue)) {
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
            if (queryAlignment == null) {
                if (other.queryAlignment != null) {
                    return false;
                }
            } else if (!queryAlignment.equals(other.queryAlignment)) {
                return false;
            }
            if (queryId == null) {
                if (other.queryId != null) {
                    return false;
                }
            } else if (!queryId.equals(other.queryId)) {
                return false;
            }
            if (queryRange == null) {
                if (other.queryRange != null) {
                    return false;
                }
            } else if (!queryRange.equals(other.queryRange)) {
                return false;
            }
            if (subjectAlignment == null) {
                if (other.subjectAlignment != null) {
                    return false;
                }
            } else if (!subjectAlignment.equals(other.subjectAlignment)) {
                return false;
            }
            if (subjectId == null) {
                if (other.subjectId != null) {
                    return false;
                }
            } else if (!subjectId.equals(other.subjectId)) {
                return false;
            }
            if (subjectRange == null) {
                if (other.subjectRange != null) {
                    return false;
                }
            } else if (!subjectRange.equals(other.subjectRange)) {
                return false;
            }
            return true;
        }
    }
}
