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
package org.jcvi.jillion.internal.assembly.util;


import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * @author dkatzel
 *
 *
 */
public final class CompactedSliceElement implements SliceElement{
	
    private static final Nucleotide[] NUCLEOTIDE_VALUES = Nucleotide.values();
	private final String id;
    //don't use array since that takes up 12 bytes of memory
    //to store reference and length
    private final byte quality;
    /**
     * Since there are only a few nucleotides
     * we can pack both the direction AND the ordinal
     * value of the nucleotide in a single byte.
     * By using the direction as the sign bit. 
     */
    private final byte dirAndNucleotide;
    /**
     * package private constructor used by compactedSlice to build
     * already encoded elements.
     * @param id
     * @param quality
     * @param encodedDirAndNucleotide
     */
    CompactedSliceElement(String id, byte quality, byte encodedDirAndNucleotide){
        if(id ==null){
            throw new NullPointerException("fields can not be null");
        }
        this.id = id;
        this.quality = quality;
        this.dirAndNucleotide = encodedDirAndNucleotide;
    }
    public CompactedSliceElement(String id, Nucleotide base, PhredQuality quality,
            Direction direction) {
        if(id ==null ||base ==null || quality ==null || direction == null){
            throw new NullPointerException("fields can not be null");
        }
        this.id= id;
        this.quality = quality.getQualityScore();
        if(direction == Direction.FORWARD){
        	this.dirAndNucleotide = base.getOrdinalAsByte();
        }else{        
        	//This will set the ordinal to negative if the 
        	//the direction is reverse for a quick lookup.
        	dirAndNucleotide = (byte)(base.getOrdinalAsByte() | 0x80);
        }
    }
   
    byte getEncodedDirAndNucleotide() {
		return dirAndNucleotide;
	}
    byte getEncodedQuality() {
		return quality;
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return id;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Nucleotide getBase() {
        int ordinal= dirAndNucleotide & 0xF;
        return NUCLEOTIDE_VALUES[ordinal];
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public PhredQuality getQuality() {
        return PhredQuality.valueOf(quality);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Direction getDirection() {
    	if(dirAndNucleotide <0){
    		return Direction.REVERSE;
    	}
    	return Direction.FORWARD;
   }
   
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dirAndNucleotide;
		result = prime * result + id.hashCode();
		result = prime * result + quality;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SliceElement)) {
            return false;
        }
        SliceElement other = (SliceElement) obj;
        if (!id.equals(other.getId())) {
            return false;
        }
        if(!getQuality().equals(other.getQuality())){
            return false;
        }
        if(!getBase().equals(other.getBase())){
            return false;
        }
        if(!getDirection().equals(other.getDirection())){
            return false;
        }
        return true;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public String toString() {
        return "CompactedSliceElement [id=" + id + ", getBase()=" + getBase()
                + "]";
    }
    
    

}
