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
package org.jcvi.jillion.experimental.align.blast;

import java.math.BigDecimal;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public final class HspBuilder<R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R, S>> implements org.jcvi.jillion.core.util.Builder<Hsp<R,S,B>>{

    private static final double ONE_HUNDRED = 100.0D;


        private String queryId;
        private String subjectId;
        private String subjectDef;
        
        private Double percentIdentity;
        private BigDecimal eValue, bitScore;
        private DirectedRange queryRange, subjectRange;
        private Integer numGapsOpenings ,numMismatches, numIdentical, numPositive,
        	alignmentLength, queryLength, subjectLength;
        private S queryAlignment, subjectAlignment;
        private Float hspScore;
        private Integer hitFrame;
        
        
        /**
         * Create a new {@link HspBuilder} for BLASTN (Nucleotide query to Nucleotide subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<Nucleotide,NucleotideSequence,NucleotideSequenceBuilder> forBlastN(){
        	return new HspBuilder<Nucleotide,NucleotideSequence,NucleotideSequenceBuilder>();
        }
        /**
         * Create a new {@link HspBuilder} for BLASTP (Protein query to Protein subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder> forBlastP(){
        	return new HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder>();
        }
        /**
         * Create a new {@link HspBuilder} for BLASTX (Nucleotide (translated) query to Protein subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder> forBlastX(){
        	return new HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder>();
        }
        /**
         * Create a new {@link HspBuilder} for TBLASTX (Nucleotide (translated) query to Nucleotide (translated) subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder> forTBlastX(){
        	return new HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder>();
        }
        /**
         * Create a new {@link HspBuilder} for TBLASTN (Protein query to Nucleotide (translated) subject) results.
         * @return a new {@link HspBuilder} will never be null.
         */
        public static HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder> forTBlastN(){
        	return new HspBuilder<AminoAcid,ProteinSequence,ProteinSequenceBuilder>();
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
        public static HspBuilder<?,?,?> forType(String type){
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
    	
        public static <R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R, S>> HspBuilder<R,S, B> copy(Hsp<R,S, B> hsp){
            return new HspBuilder<R,S,B>(hsp);
        }
        
        /**
         * Create a new instance of {@link HspBuilder}
         * that is an exact copy of this instance.
         * Any future modifications to either builder
         * will not affect the other. 
         * @return  a new {@link HspBuilder} will never be null.
         */
        public  HspBuilder<R,S,B> copy(){
            return new HspBuilder<R,S,B>(this);
        }
        
        
        
        private HspBuilder(HspBuilder<R,S,B> copy){
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
        private HspBuilder(Hsp<R,S,B> copy) {
           
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
        public HspBuilder<R,S,B> gappedAlignments(S queryAlignment, S subjectAlignment) {
            if((queryAlignment ==null && subjectAlignment !=null) 
            		|| (subjectAlignment ==null && queryAlignment !=null)){
                throw new NullPointerException("gapped alignments must be either both null or neither null");
            }
            this.queryAlignment = queryAlignment;
            this.subjectAlignment = subjectAlignment;
            return this;
        }
        public HspBuilder<R,S,B>  query(String queryId) {
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
        public HspBuilder<R,S,B>  subjectDef(String subjectDef){           
            this.subjectDef = subjectDef;
            return this;
        }
        public HspBuilder<R,S,B>  subject(String subjectId){
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

        public HspBuilder<R,S,B>  percentIdentity(double percentIdentity){            
            if(percentIdentity <0){
                throw new IllegalArgumentException("percentIdentity score must be positive: " + percentIdentity);
            }
            if(percentIdentity >ONE_HUNDRED){
                throw new IllegalArgumentException("percentIdentity score must be <= 100: " + percentIdentity);
            }
            this.percentIdentity = percentIdentity;
            return this;
        }
        public HspBuilder<R,S,B>  bitScore(BigDecimal bitScore){  
            if(bitScore ==null){
                throw new NullPointerException("bit score can not be null");
            }
            if(bitScore.compareTo(BigDecimal.ZERO) <0){
                throw new IllegalArgumentException("bit score must be positive: " + bitScore);
            }
            this.bitScore = bitScore;
            return this;
        }
        
        public HspBuilder<R,S,B>  queryRange(DirectedRange queryRange){
            if(queryRange ==null){
                throw new NullPointerException("queryRange can not be null");
            }
            this.queryRange = queryRange;
            return this;
        }

        public HspBuilder<R,S,B>  subjectRange(DirectedRange subjectRange){
            if(subjectRange ==null){
                throw new NullPointerException("subjectRange can not be null");
            }
            this.subjectRange = subjectRange;
            return this;
        }
        public HspBuilder<R,S,B>  eValue(BigDecimal eValue){
            if(eValue ==null){
                throw new NullPointerException("e-value can not be null");
            }
            if(eValue.compareTo(BigDecimal.ZERO)<0){
                throw new IllegalArgumentException("e-value score must be positive: " + eValue);
            }
            this.eValue = eValue;
            return this;
        }
        
        public HspBuilder<R,S,B>  numGapOpenings(int numberOfGapOpenings){
            if(numberOfGapOpenings<0){
                throw new IllegalArgumentException("number of gap openings can not be negative : " + numberOfGapOpenings);
            }
            this.numGapsOpenings = numberOfGapOpenings;
            return this;
        }
        public HspBuilder<R,S,B>  numMismatches(int numberOfMismatches){
            if(numberOfMismatches<0){
                throw new IllegalArgumentException("number of mismatches can not be negative : " + numberOfMismatches);
            }
            this.numMismatches = numberOfMismatches;
            return this;
        }
        public HspBuilder<R,S,B>  numIdenticalMatches(int numIdentical){
            if(numIdentical<0){
                throw new IllegalArgumentException("number of identical can not be negative : " + numIdentical);
            }
            this.numIdentical = numIdentical;
            return this;
        }
        public HspBuilder<R,S,B>  numPositiveMatches(int numPositive){
            if(numPositive<0){
                throw new IllegalArgumentException("number of positive matches can not be negative : " + numIdentical);
            }
            this.numPositive = numPositive;
            return this;
        }
        public HspBuilder<R,S,B>  alignmentLength(int alignmentLength){
            if(alignmentLength<0){
                throw new IllegalArgumentException("alignment length can not be negative : " + alignmentLength);
            }
            this.alignmentLength = alignmentLength;
            return this;
        }
        
        public HspBuilder<R,S,B> hitFrame(Integer frame) {
    		this.hitFrame = frame;
    		return this;
    	}
        
        
        public HspBuilder<R,S,B>  queryLength(int queryLength){
            if(queryLength<0){
                throw new IllegalArgumentException("query length can not be negative : " + queryLength);
            }
            this.queryLength = queryLength;
            return this;
        }
        public HspBuilder<R,S,B>  subjectLength(int subjectLength){
            if(subjectLength<0){
                throw new IllegalArgumentException("subject length can not be negative : " + subjectLength);
            }
            this.subjectLength = subjectLength;
            return this;
        }
        public HspBuilder<R,S,B>  hspScore(float hspScore){
            if(hspScore <0){
                throw new IllegalArgumentException("hsp score can not be negative : " + hspScore);
            }
            this.hspScore = hspScore;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Hsp<R,S, B>  build() {
            verifyAllValuesSet();
            sanityCheckValues();
            return new HspImpl<>(this);
        }

        private void sanityCheckValues() {
        	if(numPositive !=null && numIdentical != null && numPositive < numIdentical){
        		throw new IllegalStateException("number of matches must be >= number of identical matches");        		
        	}
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
        
    

    private static final class HspImpl<R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R, S>> implements Hsp<R,S, B>{
        
    private final String queryId,subjectId, subjectDef;
    private final double percentIdentity;
    private final BigDecimal eValue, bitScore;
    private final DirectedRange queryRange, subjectRange;
    private final int numGapsOpenings,numMismatches ;
    private final int alignmentLength;
    private final S queryAlignment;
    private final S subjectAlignment;
    private final Integer queryLength, subjectLength,numIdenticalMatches,numPositiveMatches;
    private final Float hspScore;
    private final Integer hitFrame;
    
    private HspImpl(HspBuilder<R,S, B> builder) {
        this.queryId = builder.queryId;
        this.subjectId = builder.subjectId;
        this.percentIdentity = builder.percentIdentity;
        this.bitScore = builder.bitScore;
        this.eValue = builder.eValue;
        this.queryRange = builder.queryRange;
        this.subjectRange = builder.subjectRange;
        this.numGapsOpenings = builder.numGapsOpenings;
        this.numMismatches = builder.numMismatches;
        this.alignmentLength = builder.alignmentLength;
        this.queryAlignment = builder.queryAlignment;
        this.subjectAlignment = builder.subjectAlignment;
        this.queryLength = builder.queryLength;
        this.hspScore = builder.hspScore;
        this.subjectDef = builder.subjectDef;
        this.subjectLength = builder.subjectLength;
        this.numIdenticalMatches = builder.numIdentical;
        this.numPositiveMatches = builder.numPositive;
        this.hitFrame = builder.hitFrame;
    }


   @Override
	public Integer getHitFrame() {
		return hitFrame;
	}


@Override
	public Integer getNumberOfIdentitcalMatches() {
		return numIdenticalMatches;
	}


	@Override
	public Integer getNumberOfPositiveMatches() {
		return numPositiveMatches;
	}


@Override
	public Integer getSubjectLength() {
		return subjectLength;
	}


@Override
	public String getSubjectDefinition() {
		return subjectDef;
	}


@Override
	public Float getHspScore() {
		return hspScore;
	}


@Override
	public Integer getQueryLength() {
		return queryLength;
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
            return "HspImpl [queryId=" + queryId + ", subjectId="
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
            if (!(obj instanceof HspImpl)) {
                return false;
            }
            HspImpl other = (HspImpl) obj;
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
