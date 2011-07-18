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
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.seq.read.Peaks;
import org.jcvi.glyph.Sequence;
import org.jcvi.glyph.nuc.NucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.QualitySequence;

public class DefaultPhd implements Phd {

    private final String id;
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
    private final Peaks peaks;
    private final Properties comments;
    private final List<PhdTag> tags;
    
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            Peaks peaks, Properties comments,
            List<PhdTag> tags){
    	this.id = id;
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.peaks = peaks;
        this.comments = comments;
        this.tags = tags;
    }
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            Peaks peaks,Properties comments){
        this(id,basecalls, qualities, peaks, comments,Collections.<PhdTag>emptyList());
    }
    public DefaultPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
            Peaks peaks){
        this(id,basecalls, qualities, peaks, new Properties());
    }
    
    @Override
    public Properties getComments() {
        return comments;
    }

    @Override
    public Peaks getPeaks() {
        return peaks;
    }

    @Override
    public NucleotideSequence getBasecalls() {
        return basecalls;
    }

    @Override
    public QualitySequence getQualities() {
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
                + basecalls.decode().hashCode();
        result = prime * result
                + comments.hashCode();
        result = prime * result + peaks.hashCode();
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
       if (!basecalls.decode().equals(other.getBasecalls().decode())){
            return false;
       }
        if (!comments.equals(other.getComments())){
            return false;
        }
        if (!peaks.getData().decode().equals(other.getPeaks().getData().decode())){
            return false;
        }
        if (!qualities.decode().equals(other.getQualities().decode())){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return NucleotideGlyph.convertToString(basecalls.decode());
    }

    @Override
    public int getNumberOfTracePositions() {
        Sequence<ShortGlyph> encodedPeaks= peaks.getData();        
        int lastIndex= (int)encodedPeaks.getLength() -1;
        return encodedPeaks.get(lastIndex).getNumber();
    }

    @Override
    public List<PhdTag> getTags() {
        return tags;
    }
    
    
    
}
