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
package org.jcvi.common.core.assembly.contig;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.core.symbol.residue.nuc.ReferenceEncodedNucleotideSequence;


public class DefaultPlacedRead implements PlacedRead {

    private final Read<ReferenceEncodedNucleotideSequence> read;
    private final long start;
    private final Direction sequenceDirection;
    private final Range validRange;
    
    public DefaultPlacedRead(Read<ReferenceEncodedNucleotideSequence> read, long start, Direction sequenceDirection, Range validRange){
        if(read==null){
            throw new IllegalArgumentException("read can not be null");
        }
        this.read = read;
        this.start= start;
        this.sequenceDirection = sequenceDirection;
        this.validRange = validRange;
    }
    @Override
    public long getLength() {
        return read.getLength();
    }

    @Override
    public long getStart() {
        return start;
    }

    public Read<ReferenceEncodedNucleotideSequence> getRead(){
        return read;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + read.hashCode();
        result = prime * result + sequenceDirection.hashCode();
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
        if (obj instanceof DefaultPlacedRead){           
            DefaultPlacedRead other = (DefaultPlacedRead) obj;
            return read.equals(other.getRead()) && start== other.getStart() 
            && getDirection() == other.getDirection();
        }
        else if (obj instanceof PlacedRead){           
        	PlacedRead other = (PlacedRead) obj;
        	if(read.getId()==null && other.getId() !=null){
        		return false;
        	}
        	if(!read.getId().equals(other.getId())){
        		return false;
        	}
        	if(read.getNucleotideSequence()==null && other.getNucleotideSequence() !=null){
        		return false;
        	}
        	if(other.getNucleotideSequence() ==null){
        		return false;
        	}
        	if(!read.getNucleotideSequence().asList().equals(other.getNucleotideSequence().asList())){
        		System.out.println(Nucleotides.convertToString(read.getNucleotideSequence().asList()));
        		System.out.println(Nucleotides.convertToString(other.getNucleotideSequence().asList()));
        		System.out.println();
        		return false;
        	}
        	
            return start== other.getStart() 
            && getDirection() == other.getDirection();
        }
        return false;
        
    }
    
    @Override
    public Direction getDirection() {
        return sequenceDirection;
    }
    @Override
    public String toString() {
        return "offset = "+ getStart() + " complimented? "+ getDirection()+"  " + read.toString();
    }
    @Override
    public long getEnd() {
        return getStart()+getLength()-1;
    }

    public Map<Integer, Nucleotide> getSnps(){
        Map<Integer, Nucleotide> map = new TreeMap<Integer, Nucleotide>();
        final ReferenceEncodedNucleotideSequence encodedGlyphs = read.getNucleotideSequence();
        for(Integer offset : encodedGlyphs.getSnpOffsets()){
            map.put(offset, encodedGlyphs.get(offset));
        }
        return Collections.unmodifiableMap(map);
        
    }
    @Override
    public NucleotideSequence getNucleotideSequence() {
        return read.getNucleotideSequence();
    }
    @Override
    public String getId() {
        return read.getId();
    }
    @Override
    public Range getValidRange(){
        return validRange;
    }
    @Override
    public long toGappedValidRangeOffset(long referenceIndex) {
        
        long validRangeIndex= referenceIndex - getStart();
        checkValidRange(validRangeIndex);
        return validRangeIndex;
    }
    @Override
    public long toReferenceOffset(long validRangeIndex) {
        checkValidRange(validRangeIndex);
        return getStart() +validRangeIndex;
    }
    private void checkValidRange(long validRangeIndex) {
        if(validRangeIndex <0){
            throw new IllegalArgumentException("reference index refers to index before valid range " + validRangeIndex);
        }
        if(validRangeIndex > getLength()-1){
            throw new IllegalArgumentException("reference index refers to index after valid range");
        }
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(PlacedRead o) {
        return asRange().compareTo(o.asRange());   
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return Range.buildRange(getStart(), getEnd());
    }

    private boolean isAGap(int gappedValidRangeIndex) {
        return getNucleotideSequence().getGapIndexes().contains(Integer.valueOf(gappedValidRangeIndex));
    }
    @Override
    public int convertGappedValidRangeIndexToUngappedValidRangeIndex(
            int gappedValidRangeIndex) {
        
        if(isAGap(gappedValidRangeIndex)){
            //we are given a gap
            //which we can't convert into an ungapped index
            throw new IllegalArgumentException(gappedValidRangeIndex + " is a gap");
        }
        int numberOfGaps = getNucleotideSequence().getNumberOfGapsUntil(gappedValidRangeIndex);
        return gappedValidRangeIndex-numberOfGaps;
    }

    @Override
    public Range convertGappedValidRangeToUngappedValidRange(
            Range gappedValidRange) {
       return Range.buildRange(
               convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       AssemblyUtil.getLeftFlankingNonGapIndex(getNucleotideSequence(),(int)gappedValidRange.getStart())),
               convertGappedValidRangeIndexToUngappedValidRangeIndex(
                       AssemblyUtil.getLeftFlankingNonGapIndex(getNucleotideSequence(), (int)gappedValidRange.getEnd()))
                
        );
    }

    @Override
    public Range convertUngappedValidRangeToGappedValidRange(
            Range ungappedValidRange) {
        return  Range.buildRange(
                convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedValidRange.getStart()),
                convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedValidRange.getEnd()));
                
    }
    
    @Override
    public int convertUngappedValidRangeIndexToGappedValidRangeIndex(
            int ungappedValidRangeIndex) {
        NucleotideSequence nucleotideSequence = getNucleotideSequence();
        int numberOfGaps = nucleotideSequence.getNumberOfGapsUntil(nucleotideSequence.getGappedOffsetFor(ungappedValidRangeIndex));
        return ungappedValidRangeIndex+numberOfGaps;
    }
    
    

}
