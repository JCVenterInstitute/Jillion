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
package org.jcvi.jillion.fasta.pos;


import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.fasta.FastaRecord;


public final class PositionFastaRecord implements FastaRecord<Position, PositionSequence>{
	
	private final String identifier;
	private final String comments;
    private final PositionSequence positions;
    
    public PositionFastaRecord(String id, PositionSequence positions){
        this(id, null, positions);
    }
    public PositionFastaRecord(String id, String comments, PositionSequence positions){
    	if(id ==null){
    		throw new NullPointerException("id can not be null");
    	}
    	if(positions ==null){
    		throw new NullPointerException("positions can not be null");
    	}
        this.identifier = id;
        this.comments = comments;
        this.positions = positions;
        
    }
    /**
     * @return A <code>String</code>.
     */
    public String getId()
    {
        return this.identifier;
    }

    /**
     * @return A <code>String</code>.
     */
    public String getComment()
    {
        return this.comments;
    }
  
   
    
    @Override
	public String toString() {
		return "PositionSequenceFastaRecord [identifier=" + identifier
				+ ", comments=" + comments + ", positions=" + positions + "]";
	}
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.identifier.hashCode();
        result = prime * result + this.getSequence().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof PositionFastaRecord)){
            return false;
        }
        PositionFastaRecord other = (PositionFastaRecord)obj;
		return 
        
        ObjectsUtil.nullSafeEquals(getId(), other.getId())
         && ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence());
    }   

   

   
    @Override
    public PositionSequence getSequence() {
        return positions;
    }
    
    

}
