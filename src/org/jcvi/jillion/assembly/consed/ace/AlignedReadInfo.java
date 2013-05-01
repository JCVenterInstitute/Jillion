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
/*
 * Created on Feb 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Direction;

/**
 * {@code AlignedReadInfo} defines where on the contig
 * a read starts to align and in which
 * orientation relative to the contig.
 * @author dkatzel
 *
 *
 */
final class AlignedReadInfo{
    private static final Direction[] DIR_VALUES = Direction.values();
	private final byte dirOrdinal;
    private final int startOffset;
    

    public AlignedReadInfo(int startOffset, Direction dir) {
        if(dir ==null){
            throw new NullPointerException("direction can not be null");
        }
        this.startOffset = startOffset;
        this.dirOrdinal = (byte)dir.ordinal();
    }
    

    public int getStartOffset() {
        return startOffset;
    }
    
    public Direction getDirection(){
        return DIR_VALUES[dirOrdinal];
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dirOrdinal;
		result = prime * result + startOffset;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AlignedReadInfo other = (AlignedReadInfo) obj;
		if (dirOrdinal != other.dirOrdinal) {
			return false;
		}
		if (startOffset != other.startOffset) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "AlignedReadInfo [dir=" + getDirection() + ", startOffset=" + startOffset
				+ "]";
	}
   
   
    
    
}
