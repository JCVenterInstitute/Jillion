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
/*
 * Created on Sep 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;


public final class DefaultPlacedRead implements AssembledRead {

    private final long start;
    private final byte directionOrdinal;
    private final ReferenceMappedNucleotideSequence sequence;
    private final String id;
    private final ReadInfo readInfo;
    
    public static AssembledReadBuilder<AssembledRead> createBuilder(NucleotideSequence reference, 
            String readId,NucleotideSequence validBases,
            int offset, Direction dir, Range clearRange,
            int ungappedFullLength){
        return new Builder(reference, readId, validBases, offset, dir, 
                clearRange, ungappedFullLength);
    }
    
    public static AssembledReadBuilder<AssembledRead> createBuilder(NucleotideSequence reference, 
            String readId,String validBases,
            int offset, Direction dir, Range clearRange,
            int ungappedFullLength){
    	 return createBuilder(reference, readId, new NucleotideSequenceBuilder(validBases).build(), offset, dir, 
                 clearRange, ungappedFullLength);
    }
    
    DefaultPlacedRead(String id, ReferenceMappedNucleotideSequence sequence, long start, Direction sequenceDirection, int ungappedFullLength, Range validRange){
       this.id = id;
       this.sequence = sequence;
        this.start= start;
        this.directionOrdinal = (byte)sequenceDirection.ordinal();
        this.readInfo = new DefaultReadInfo(validRange, ungappedFullLength);
    }
    
    
    @Override
	public ReadInfo getReadInfo() {
		return readInfo;
	}

	@Override
    public long getGappedLength() {
        return sequence.getLength();
    }

    @Override
    public long getGappedStartOffset() {
        return start;
    }
    
    
    @Override
	public String toString() {
		return "DefaultPlacedRead [start=" + start + ", directionOrdinal="
				+ directionOrdinal + ", id=" + id + "]";
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        result = prime * result + sequence.hashCode();
        result = prime * result + directionOrdinal;
        result = prime * result + (int) (start ^ (start >>> 32));
        return result;
    }
    /**
     * Two PlacedReads are equal if they have the same start value
     * and they have their reads are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj instanceof AssembledRead){           
        	AssembledRead other = (AssembledRead) obj;
        	if(getId()==null && other.getId() !=null){
        		return false;
        	}
        	if(!getId().equals(other.getId())){
        		return false;
        	}
        	if(getNucleotideSequence()==null && other.getNucleotideSequence() !=null){
        		return false;
        	}
        	if(other.getNucleotideSequence() ==null){
        		return false;
        	}
        	if(!getNucleotideSequence().equals(other.getNucleotideSequence())){
        		return false;
        	}
        	
            return start== other.getGappedStartOffset() 
            && getDirection() == other.getDirection();
        }
        return false;
        
    }
    
    @Override
    public Direction getDirection() {
        return Direction.values()[directionOrdinal];
    }
  
    @Override
    public long getGappedEndOffset() {
        return getGappedStartOffset()+getGappedLength()-1;
    }

    public Map<Integer, Nucleotide> getDifferenceMap(){
       return getNucleotideSequence().getDifferenceMap();
        
    }
    @Override
    public ReferenceMappedNucleotideSequence getNucleotideSequence() {
        return sequence;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public long toGappedValidRangeOffset(long referenceIndex) {
        
        long validRangeIndex= referenceIndex - getGappedStartOffset();
        checkValidRange(validRangeIndex);
        return validRangeIndex;
    }
    @Override
    public long toReferenceOffset(long validRangeIndex) {
        checkValidRange(validRangeIndex);
        return getGappedStartOffset() +validRangeIndex;
    }
    private void checkValidRange(long validRangeIndex) {
        if(validRangeIndex <0){
            throw new IllegalArgumentException("reference index refers to index before valid range " + validRangeIndex);
        }
        if(validRangeIndex > getGappedLength()-1){
            throw new IllegalArgumentException("reference index refers to index after valid range");
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return getGappedContigRange();
    }
    
    @Override
	public Range getGappedContigRange() {
		return Range.create(getGappedStartOffset(), getGappedEndOffset());
	}

	private static class Builder implements AssembledReadBuilder<AssembledRead>{
        private String readId;
        /**
         * Our original encoded sequence.  If we 
         * edit the basecalls, this will get set to null
         * and we use {@link #basesBuilder} instead.
         */
        private NucleotideSequence originalSequence;
        /**
         * Our edited sequence, only used if needed
         * since it takes up more memory.
         */
        private NucleotideSequenceBuilder basesBuilder=null;
        private int offset;
        private Range clearRange;
        private NucleotideSequence reference;
        private final Direction dir;
        private final int ungappedFullLength;
        
        public Builder(NucleotideSequence reference, String readId, NucleotideSequence validBases,
                            int offset, Direction dir, Range clearRange,
                            int ungappedFullLength){
            this.readId = readId;
            this.dir =dir;
            this.clearRange = clearRange;
            this.offset = offset;
            this.originalSequence = validBases;
            this.basesBuilder =null;
            if(offset + validBases.getLength() > reference.getLength()){
                throw new IllegalArgumentException(
                		String.format("read %s , last offset %d goes beyond the reference (length %d)",
                				readId, offset + validBases.getLength(), reference.getLength()));
            }
            if(offset <0){
                throw new IllegalArgumentException("read goes before the reference");
            }
            if(ungappedFullLength < clearRange.getEnd()){
            	throw new IllegalArgumentException("clear range extends beyond ungapped full length");
            }
            this.reference = reference;
            this.ungappedFullLength = ungappedFullLength;
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reference(NucleotideSequence reference, int newOffset){
            if(reference ==null){
                throw new NullPointerException("reference can not be null");
            }
            this.reference = reference;
            this.offset = newOffset;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getBegin(){
            return offset;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String getId(){
            return readId;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder setStartOffset(int newOffset){
            this.offset = newOffset;
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder shiftRight(int numberOfBases){
            return setStartOffset(offset+numberOfBases);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder shiftLeft(int numberOfBases){
            return setStartOffset(offset-numberOfBases);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range getClearRange() {
            return clearRange;
        }



        /**
        * {@inheritDoc}
        */
        @Override
        public Direction getDirection() {
            return dir;
        }

        

        /**
        * {@inheritDoc}
        */
        @Override
        public int getUngappedFullLength() {
            return ungappedFullLength;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledRead build(){
        	
            ReferenceMappedNucleotideSequence updatedEncodedBasecalls = new NucleotideSequenceBuilder(currentBasecallsAsString())
            																.setReferenceHint(reference, offset)
            																.buildReferenceEncodedNucleotideSequence();
            return new DefaultPlacedRead(readId, updatedEncodedBasecalls, offset, dir, ungappedFullLength,clearRange);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reAbacus(Range gappedValidRangeToChange, String newBasecalls){
            return reAbacus(gappedValidRangeToChange, Nucleotides.parse(newBasecalls));
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reAbacus(Range gappedValidRangeToChange, List<Nucleotide> newBasecalls){
            List<Nucleotide> oldUngappedBasecalls = getNucleotideSequenceBuilder()
            										.subSequence(gappedValidRangeToChange)
            										.ungap()
            										.asList();
            List<Nucleotide> newUngappedBasecalls = new ArrayList<Nucleotide>(newBasecalls.size());
            //make sure we aren't adding/editing any basecalls
            //only gaps should be affected
            for(Nucleotide newBase : newBasecalls){
                if(!newBase.isGap()){
                    newUngappedBasecalls.add(newBase);
                }
            }
            if(!oldUngappedBasecalls.equals(newUngappedBasecalls)){
                throw new IllegalReAbacus(oldUngappedBasecalls,newUngappedBasecalls);
            }
            basesBuilder.delete(gappedValidRangeToChange);
            basesBuilder.insert((int)gappedValidRangeToChange.getBegin(), newBasecalls);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized long getLength(){
            if(basesBuilder !=null){
                return basesBuilder.getLength();
            }
            return originalSequence.getLength();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getEnd(){
            return offset + getLength()-1;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range asRange(){
            return Range.create(offset,getEnd());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequenceBuilder getNucleotideSequenceBuilder() {
            if(basesBuilder==null){
                this.basesBuilder = new NucleotideSequenceBuilder(originalSequence);
                originalSequence=null;
            }
            return basesBuilder;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequence getCurrentNucleotideSequence(){
            if(originalSequence !=null){
                return originalSequence;
            }
            return basesBuilder.build();
        }
        private synchronized String currentBasecallsAsString(){
            if(originalSequence !=null){
                return originalSequence.toString();
            }
            return basesBuilder.toString();
        }




        
        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((basesBuilder == null) ? 0 : basesBuilder.hashCode());
            result = prime * result + offset;
            result = prime * result
                    + ((readId == null) ? 0 : readId.hashCode());
            result = prime * result
                    + ((reference == null) ? 0 : reference.hashCode());
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
            if (!(obj instanceof Builder)) {
                return false;
            }
            Builder other = (Builder) obj;
            if (basesBuilder == null) {
                if (other.basesBuilder != null) {
                    return false;
                }
            } else if (!basesBuilder.asList().equals(other.basesBuilder.asList())) {
                return false;
            }
            if (offset != other.offset) {
                return false;
            }
            if (readId == null) {
                if (other.readId != null) {
                    return false;
                }
            } else if (!readId.equals(other.readId)) {
                return false;
            }
            if (reference == null) {
                if (other.reference != null) {
                    return false;
                }
            } else if (!reference.equals(other.reference)) {
                return false;
            }
            return true;
        }
        
    }

    protected static final class IllegalReAbacus extends IllegalArgumentException{

        private static final long serialVersionUID = -8272559886165301526L;

        public IllegalReAbacus(List<Nucleotide> oldUngappedBasecalls, List<Nucleotide> newUngappedBasecalls){
            super(String.format("reAbacusing must retain same ungapped basecalls! '%s' vs '%s'", 
                    Nucleotides.asString(oldUngappedBasecalls),
                    Nucleotides.asString(newUngappedBasecalls)
                    ));
        }
    
    }
}
