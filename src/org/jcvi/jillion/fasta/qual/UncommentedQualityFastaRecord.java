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
package org.jcvi.jillion.fasta.qual;

import org.jcvi.jillion.core.qual.QualitySequence;

class UncommentedQualityFastaRecord implements QualityFastaRecord{
	private final String id;
	private final QualitySequence qualities;

    public UncommentedQualityFastaRecord(String id, QualitySequence qualities){
        if(id==null){
        	throw new NullPointerException("id can not be null");
        }
        if(qualities==null){
        	throw new NullPointerException("qualities can not be null");
        }
    	this.id=id;
        this.qualities = qualities;
        
    }
    
    
    @Override
	public String getId() {
		return id;
	}

    /**
     * Defaults To null, If a comment
     * actually exists, please
     * override this method
     * to return the comment String.
     * {@inheritDoc}
     */
	@Override
	public String getComment() {
		return null;
	}


    
    @Override
	public String toString() {
		return "QualitySequenceFastaRecord [id=" + getId()
				+ ", comment=" + getComment() + ", sequence ="
				+ getSequence() + "]";
	}
    
    

    @Override
    public QualitySequence getSequence() {
        return qualities;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result
				+ qualities.hashCode();
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
		if (!(obj instanceof QualityFastaRecord)) {
			return false;
		}
		QualityFastaRecord other = (QualityFastaRecord) obj;
		if (!id.equals(other.getId())) {
			return false;
		}
		if (!qualities.equals(other.getSequence())) {
			return false;
		}
		
		return true;
	}
    
    
}
