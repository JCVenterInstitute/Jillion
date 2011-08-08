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

import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.DefaultPlacedRead;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
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
        private ReferenceEncodedNucleotideSequence referencedEncodedBases;
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
            
            this.referencedEncodedBases = new DefaultReferenceEncodedNucleotideSequence(
                    reference, validBases, offset);
            if(referencedEncodedBases.getNumberOfBasesAfterReference()<0 || referencedEncodedBases.getNumberOfBasesAfterReference()>0){
                throw new IllegalArgumentException(String.format("read %s goes off the reference before %d, after %d",
                        readId,
                        referencedEncodedBases.getNumberOfBasesBeforeReference(),
                        referencedEncodedBases.getNumberOfBasesAfterReference()));
            }
            this.ungappedFullLength = ungappedFullLength;
        }
        
        
        public Builder reference(NucleotideSequence reference, int newOffset){
            this.reference = reference;
            this.offset = newOffset;
            return this;
        }
        public int offset(){
            return offset;
        }
        public String id(){
            return readId;
        }
        public Builder setOffset(int newOffset){
            this.offset = newOffset;
            return this;
        }
        
        public DefaultAcePlacedRead build(){
            ReferenceEncodedNucleotideSequence updatedEncodedBasecalls = 
                new DefaultReferenceEncodedNucleotideSequence(reference,
                        Nucleotides.convertToString(referencedEncodedBases.asList()),offset);
            Read read = new DefaultRead(readId, updatedEncodedBasecalls);
            return new DefaultAcePlacedRead(read, offset, dir, phdInfo,ungappedFullLength,clearRange);
        }
        
    }

}
