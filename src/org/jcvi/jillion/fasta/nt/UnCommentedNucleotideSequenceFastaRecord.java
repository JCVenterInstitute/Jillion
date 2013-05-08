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
package org.jcvi.jillion.fasta.nt;


import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
/**
 * {@code UnCommentedNucleotideSequenceFastaRecord} is an implementation
 * of {@link NucleotideSequenceFastaRecord} that saves
 * memory by not having a reference to a comment.
 * All calls to {@link #getComment()} will return null.
 * @author dkatzel
 *
 */
class UnCommentedNucleotideSequenceFastaRecord implements NucleotideFastaRecord{

	private final NucleotideSequence sequence;
	private final String id;

    public UnCommentedNucleotideSequenceFastaRecord(String id, NucleotideSequence sequence){
    	if(id == null){
            throw new NullPointerException("identifier can not be null");
        }        
         if(sequence ==null){
         	throw new NullPointerException("sequence can not be null");
         }
         this.id = id;
         this.sequence = sequence;
    }
   
    

    /**
     * @return A <code>String</code>.
     */
    public String getId()
    {
        return this.id;
    }
    /**
     *{@inheritDoc}.
     */
    @Override
    public String getComment()
    {
        return null;
    }
    @Override
    public NucleotideSequence getSequence() 
    {
        return this.sequence;
    }

	
    
    @Override
   	public String toString() {
   		return "NucleotideSequenceFastaRecord [id=" + getId()
   				+ ", comment=" + getComment() + ", sequence ="
   				+ getSequence() + "]";
   	}
       
  
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id.hashCode();
        result = prime * result + this.getSequence().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof NucleotideFastaRecord)){
            return false;
        }
        NucleotideFastaRecord other = (NucleotideFastaRecord)obj;
		return 
        ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence()) 
        && ObjectsUtil.nullSafeEquals(getId(), other.getId());
    }   
   
}
