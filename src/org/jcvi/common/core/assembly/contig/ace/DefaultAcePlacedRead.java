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
package org.jcvi.common.core.assembly.contig.ace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.DefaultPlacedRead;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.core.symbol.residue.nuc.ReferenceEncodedNucleotideSequence;

public class DefaultAcePlacedRead implements AcePlacedRead {
    private final PhdInfo phdInfo;
    private final int ungappedFullLength;
    private final PlacedRead placedRead;
    
    public DefaultAcePlacedRead(Read<ReferenceEncodedNucleotideSequence> read,
            long start, Direction dir,PhdInfo phdInfo, int ungappedFullLength, Range validRange) {
        this.placedRead = new DefaultPlacedRead(read, start, dir,validRange);
        this.phdInfo =phdInfo;
        this.ungappedFullLength =ungappedFullLength;
    }

    @Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }

    @Override
    public int getUngappedFullLength() {
        return ungappedFullLength;
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
    public long getStart() {
        return placedRead.getStart();
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
    public int compareTo(PlacedRead o) {
        return placedRead.compareTo(o);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Map<Integer, Nucleotide> getSnps() {
        return placedRead.getSnps();
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
        result = prime * result + ungappedFullLength;
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
        if (ungappedFullLength != other.ungappedFullLength){
            return false;
        }
        return true;
    }


    public static class Builder{
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
        private PhdInfo phdInfo;
        private NucleotideSequence reference;
        private final Direction dir;
        private final int ungappedFullLength;
        
        public Builder(NucleotideSequence reference, String readId,String validBases,
                            int offset, Direction dir, Range clearRange,PhdInfo phdInfo,
                            int ungappedFullLength){
            this.readId = readId;
            this.dir =dir;
            this.clearRange = clearRange;
            this.offset = offset;
            this.phdInfo = phdInfo;
            this.originalSequence = NucleotideSequenceFactory.create(validBases);
            this.basesBuilder =null;
            if(offset + validBases.length() > reference.getLength()){
                throw new IllegalArgumentException("read goes beyond the reference");
            }
            if(offset <0){
                throw new IllegalArgumentException("read goes before the reference");
            }
            this.reference = reference;
            this.ungappedFullLength = ungappedFullLength;
        }
        
        
        public Builder reference(NucleotideSequence reference, int newOffset){
            this.reference = reference;
            this.offset = newOffset;
            return this;
        }
        public int getStartOffset(){
            return offset;
        }
        public String getId(){
            return readId;
        }
        public Builder setStartOffset(int newOffset){
            this.offset = newOffset;
            return this;
        }
        
        public Builder shiftRight(int numberOfBases){
            return setStartOffset(offset+numberOfBases);
        }
        public Builder shiftLeft(int numberOfBases){
            return setStartOffset(offset-numberOfBases);
        }
        /**
         * @return the clearRange
         */
        public Range getClearRange() {
            return clearRange;
        }


        /**
         * @return the phdInfo
         */
        public PhdInfo getPhdInfo() {
            return phdInfo;
        }


        /**
         * @return the dir
         */
        public Direction getDirection() {
            return dir;
        }

        

        /**
         * @return the ungappedFullLength
         */
        public int getUngappedFullLength() {
            return ungappedFullLength;
        }


        public DefaultAcePlacedRead build(){
            ReferenceEncodedNucleotideSequence updatedEncodedBasecalls = NucleotideSequenceFactory.createReferenceEncoded(
                        reference,
                        currentBasecallsAsString(),offset);
            Read read = new DefaultRead(readId, updatedEncodedBasecalls);
            return new DefaultAcePlacedRead(read, offset, dir, phdInfo,ungappedFullLength,clearRange);
        }
        public Builder reAbacus(Range gappedValidRangeToChange, String newBasecalls){
            return reAbacus(gappedValidRangeToChange, Nucleotides.parse(newBasecalls));
        }
        public Builder reAbacus(Range gappedValidRangeToChange, List<Nucleotide> newBasecalls){
            List<Nucleotide> oldUngappedBasecalls = Nucleotides.ungap(getBasesBuilder().asList(gappedValidRangeToChange));
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
            basesBuilder.insert((int)gappedValidRangeToChange.getStart(), newBasecalls);
            return this;
        }

        public long getLength(){
            return basesBuilder.getLength();
        }
        public long getEnd(){
            return offset + getLength()-1;
        }
        
        public Range asRange(){
            return Range.buildRange(offset,getEnd());
        }
        /**
         * @return the basesBuilder
         */
        public synchronized NucleotideSequenceBuilder getBasesBuilder() {
            if(basesBuilder==null){
                this.basesBuilder = new NucleotideSequenceBuilder(originalSequence);
                originalSequence=null;
            }
            return basesBuilder;
        }
        
        private synchronized String currentBasecallsAsString(){
            if(originalSequence !=null){
                return Nucleotides.asString(originalSequence);
            }
            return basesBuilder.toString();
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
