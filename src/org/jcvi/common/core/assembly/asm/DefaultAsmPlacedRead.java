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

package org.jcvi.common.core.assembly.asm;

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
import org.jcvi.common.core.symbol.residue.nt.ReferenceEncodedNucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public final class DefaultAsmPlacedRead implements AsmPlacedRead{
    private final boolean isSurrogate;
    private final PlacedRead placedRead;
    
    public static AsmPlacedReadBuilder createBuilder(NucleotideSequence reference, String readId,String validBases,
            int offset, Direction dir, Range clearRange,
            int ungappedFullLength, boolean isSurrogate){
        return new Builder(reference, readId, validBases, 
                offset, dir, clearRange, ungappedFullLength,isSurrogate);
    }
    
    private DefaultAsmPlacedRead(PlacedRead placedRead, boolean isSurrogate) {
        this.placedRead = placedRead;
        this.isSurrogate = isSurrogate;
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
    public long toGappedValidRangeOffset(long referenceOffset) {
        return placedRead.toGappedValidRangeOffset(referenceOffset);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long toReferenceOffset(long gappedValidRangeOffset) {
        return placedRead.toReferenceOffset(gappedValidRangeOffset);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedFullLength() {
        return placedRead.getUngappedFullLength();
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
    public ReferenceEncodedNucleotideSequence getNucleotideSequence() {
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
    @Override
	public Range getContigRange() {
		return placedRead.getContigRange();
	}
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isRepeatSurrogate() {
        return isSurrogate;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isSurrogate ? 1231 : 1237);
        result = prime * result
                + ((placedRead == null) ? 0 : placedRead.hashCode());
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
        if (!(obj instanceof DefaultAsmPlacedRead)) {
            return false;
        }
        DefaultAsmPlacedRead other = (DefaultAsmPlacedRead) obj;
        if (isSurrogate != other.isSurrogate) {
            return false;
        }
        if (placedRead == null) {
            if (other.placedRead != null) {
                return false;
            }
        } else if (!placedRead.equals(other.placedRead)) {
            return false;
        }
        return true;
    }

    private static class Builder implements AsmPlacedReadBuilder{
        private boolean isSurrogate=false;        
        private final PlacedReadBuilder<PlacedRead> delegateBuilder;
        
        
        public Builder(NucleotideSequence reference, String readId,String validBases,
                            int offset, Direction dir, Range clearRange,
                            int ungappedFullLength,boolean isSurrogate){
            this.delegateBuilder = DefaultPlacedRead.createBuilder(
                    reference, readId, validBases, offset,
                    dir, clearRange, ungappedFullLength);
            this.isSurrogate = isSurrogate;
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
        public DefaultAsmPlacedRead build(){
            return new DefaultAsmPlacedRead(delegateBuilder.build(),isSurrogate);
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
                    + (isSurrogate ? 1:0);
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
            if(isSurrogate!=other.isSurrogate){
                return false;
            }
            return true;
        }



        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isRepeatSurrogate() {
            return isSurrogate;
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public void setRepeatSurrogate(boolean isRepeatSurrogate) {
            this.isSurrogate = isRepeatSurrogate;
            
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
