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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.DefaultPlacedRead;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.PlacedReadBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;

final class DefaultAcePlacedRead implements AcePlacedRead {
    private final PhdInfo phdInfo;
    private final PlacedRead placedRead;
    
    
    public static AcePlacedReadBuilder createBuilder(NucleotideSequence reference, String readId,NucleotideSequence validBases,
            int offset, Direction dir, Range clearRange,PhdInfo phdInfo,
            int ungappedFullLength){
        return new Builder(reference, readId, validBases, 
                offset, dir, clearRange, phdInfo, ungappedFullLength);
    }
    private DefaultAcePlacedRead(PlacedRead placedRead, PhdInfo phdInfo) {
        this.placedRead = placedRead;
        this.phdInfo =phdInfo;
    }

    @Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }

    @Override
    public int getUngappedFullLength() {
        return placedRead.getUngappedFullLength();
    }


    @Override
	public String toString() {
		return "DefaultAcePlacedRead [placedRead=" + placedRead + ", phdInfo="
				+ phdInfo + "]";
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return placedRead.getId();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getNucleotideSequence() {
        return placedRead.getNucleotideSequence();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getLength() {
        return placedRead.getLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getBegin() {
        return placedRead.getBegin();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getEnd() {
        return placedRead.getEnd();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return placedRead.asRange();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Map<Integer, Nucleotide> getDifferenceMap() {
        return placedRead.getDifferenceMap();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range getValidRange() {
        return placedRead.getValidRange();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Direction getDirection() {
        return placedRead.getDirection();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long toGappedValidRangeOffset(long referenceIndex) {
        return placedRead.toGappedValidRangeOffset(referenceIndex);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long toReferenceOffset(long validRangeIndex) {
        return placedRead.toReferenceOffset(validRangeIndex);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((phdInfo == null) ? 0 : phdInfo.hashCode());
        result = prime * result
                + ((placedRead == null) ? 0 : placedRead.hashCode());
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        DefaultAcePlacedRead other = (DefaultAcePlacedRead) obj;
        if (phdInfo == null) {
            if (other.phdInfo != null){
                return false;
            }
        } else if (!phdInfo.equals(other.phdInfo)){
            return false;
        }
        if (placedRead == null) {
            if (other.placedRead != null){
                return false;
            }
        } else if (!placedRead.equals(other.placedRead)){
            return false;
        }        
        return true;
    }


    private static class Builder implements AcePlacedReadBuilder{
        private PhdInfo phdInfo;        
        private final PlacedReadBuilder<PlacedRead> delegateBuilder;
        
        
        public Builder(NucleotideSequence reference, String readId,NucleotideSequence validBases,
                            int offset, Direction dir, Range clearRange,PhdInfo phdInfo,
                            int ungappedFullLength){
            this.delegateBuilder = DefaultPlacedRead.createBuilder(
                    reference, readId, validBases, offset,
                    dir, clearRange, ungappedFullLength);
            this.phdInfo = phdInfo;
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reference(NucleotideSequence reference, int newOffset){
            delegateBuilder.reference(reference, newOffset);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getBegin(){
            return delegateBuilder.getBegin();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String getId(){
            return delegateBuilder.getId();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder setStartOffset(int newOffset){
            delegateBuilder.setStartOffset(newOffset);
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder shiftRight(int numberOfBases){
            delegateBuilder.shiftRight(numberOfBases);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder shiftLeft(int numberOfBases){
            delegateBuilder.shiftLeft(numberOfBases);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range getClearRange() {
            return delegateBuilder.getClearRange();
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public PhdInfo getPhdInfo() {
            return phdInfo;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public Direction getDirection() {
            return delegateBuilder.getDirection();
        }

        

        /**
        * {@inheritDoc}
        */
        @Override
        public int getUngappedFullLength() {
            return delegateBuilder.getUngappedFullLength();
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAcePlacedRead build(){
            return new DefaultAcePlacedRead(delegateBuilder.build(),phdInfo);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reAbacus(Range gappedValidRangeToChange, String newBasecalls){
            delegateBuilder.reAbacus(gappedValidRangeToChange, newBasecalls);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder reAbacus(Range gappedValidRangeToChange, List<Nucleotide> newBasecalls){
            delegateBuilder.reAbacus(gappedValidRangeToChange, newBasecalls);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized long getLength(){
            return delegateBuilder.getLength();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public long getEnd(){
            return delegateBuilder.getEnd();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Range asRange(){
            return delegateBuilder.asRange();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequenceBuilder getBasesBuilder() {
            return delegateBuilder.getBasesBuilder();
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized NucleotideSequence getCurrentNucleotideSequence(){
            return delegateBuilder.getCurrentNucleotideSequence();
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime
                    * result
                    + ((delegateBuilder == null) ? 0 : delegateBuilder
                            .hashCode());
            result = prime * result
                    + ((phdInfo == null) ? 0 : phdInfo.hashCode());
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
            if (delegateBuilder == null) {
                if (other.delegateBuilder != null) {
                    return false;
                }
            } else if (!delegateBuilder.equals(other.delegateBuilder)) {
                return false;
            }
            if (phdInfo == null) {
                if (other.phdInfo != null) {
                    return false;
                }
            } else if (!phdInfo.equals(other.phdInfo)) {
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
