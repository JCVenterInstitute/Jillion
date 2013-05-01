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
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

final class DefaultPhd implements Phd {

    private final String id;
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
    private final PositionSequence peaks;
    private final Map<String,String> comments;
    private final List<PhdWholeReadItem> wrs;
    private final List<PhdReadTag> readTags;
    
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            PositionSequence peaks, Map<String,String> comments,
            List<PhdWholeReadItem> wholeReadItems,
            List<PhdReadTag> readTags){
    	this.id = id;
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.peaks = peaks;
        this.comments = comments;
        this.wrs = wholeReadItems;
        this.readTags = readTags;
        
    }
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            PositionSequence peaks,Map<String,String> comments){
        this(id,basecalls, qualities, peaks, comments,Collections.<PhdWholeReadItem>emptyList(),
        		Collections.<PhdReadTag>emptyList());
    }
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            PositionSequence peaks){
        this(id,basecalls, qualities, peaks, Collections.<String,String>emptyMap());
    }
    
    @Override
    public Map<String,String> getComments() {
        return comments;
    }

    

    @Override
	public PositionSequence getPositionSequence() {
		return peaks;
	}
	@Override
    public NucleotideSequence getNucleotideSequence() {
        return basecalls;
    }

    @Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }

	@Override
	public String getId() {
		return id;
	}
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        result = prime * result
                + basecalls.hashCode();
        result = prime * result
                + comments.hashCode();
        result = prime * result + (peaks==null? 0: peaks.hashCode());
        result = prime * result
                + qualities.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof Phd)){
            return false;
        }
        Phd other = (Phd) obj;
        if(!id.equals(other.getId())){
        	return false;
        }
       if (!basecalls.equals(other.getNucleotideSequence())){
            return false;
       }
        if (!comments.equals(other.getComments())){
            return false;
        }
        if(peaks==null){
        	if(other.getPositionSequence() !=null){
        		return false;
        	}
        }else if (!peaks.equals(other.getPositionSequence())){
            return false;
        }
        if (!qualities.equals(other.getQualitySequence())){
            return false;
        }
        if (!wrs.equals(other.getWholeReadItems())){
            return false;
        }
        if (!readTags.equals(other.getReadTags())){
            return false;
        }
        return true;
    }

    

    @Override
	public String toString() {
		return "DefaultPhd [id=" + id + "]";
	}
	
	@Override
	public List<PhdWholeReadItem> getWholeReadItems() {
		//defensive copy
		return Collections.unmodifiableList(wrs);
	}
	@Override
	public List<PhdReadTag> getReadTags() {
		//defensive copy
		return Collections.unmodifiableList(readTags);
	}

    
    
    
}
