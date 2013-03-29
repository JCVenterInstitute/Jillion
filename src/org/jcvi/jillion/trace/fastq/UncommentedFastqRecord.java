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
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@link FastqRecord} implementation that doesn't
 * have a comment (so {@link #getComment()} returns null).
 * This saves us 8 bytes per record.
 * @author dkatzel
 *
 */
class UncommentedFastqRecord implements FastqRecord{
		private final String id;
	    private final NucleotideSequence nucleotides;
	    private final QualitySequence qualities;

	    /**
	     * Create a new {@link FastqRecord} with the given
	     * values.
	     * @param id the id of this fastq record.  This
	     * id may contain whitespace.
	     * @param nucleotides the {@link NucleotideSequence}
	     * associated with this record.
	     * @param qualities the {@link QualitySequence}
	     * associated with this record, can not be null.
	     * @throw NullPointerException if either id, nucleotides or qualities
	     * is set to null.
	     */
	    public UncommentedFastqRecord(String id, NucleotideSequence nucleotides,
	            QualitySequence qualities) {
	    	if(id ==null){
	    		throw new NullPointerException("id can not be null");
	    	}
	    	if(nucleotides ==null){
	    		throw new NullPointerException("nucleotides can not be null");
	    	}
	    	if(qualities ==null){
	    		throw new NullPointerException("qualities can not be null");
	    	}
	        this.id = id;
	        this.nucleotides = nucleotides;
	        this.qualities = qualities;
	    }
	    /**
	     * 
	     * {@inheritDoc}
	     */
	    @Override
	    public String getComment() {
	        return null;
	    }
	    /**
	     * 
	     * {@inheritDoc}
	     */
	    @Override
	    public String getId() {
	        return id;
	    }

	   
	     /**
	      * 
	      * {@inheritDoc}
	      */
	    @Override
	    public NucleotideSequence getNucleotideSequence() {
	        return nucleotides;
	    }
	    /**
	     * 
	     * {@inheritDoc}
	     */
	    @Override
	    public QualitySequence getQualitySequence() {
	        return qualities;
	    }
	    @Override
	    public int hashCode() {
	        final int prime = 31;
	        int result = 1;	        
	        result = prime * result + id.hashCode();
	        result = prime * result
	                + nucleotides.hashCode();
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
	        if (!(obj instanceof FastqRecord)) {
	            return false;
	        }
	        FastqRecord other = (FastqRecord) obj;	       
	        if (!id.equals(other.getId())) {
	            return false;
	        }
	        if (!nucleotides.equals(other.getNucleotideSequence())) {
	            return false;
	        }
	        if (!qualities.equals(other.getQualitySequence())) {
	            return false;
	        }
	        return true;
	    }
}
