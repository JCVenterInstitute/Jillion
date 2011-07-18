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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.qual.QualitySequence;

public class DefaultFastQRecord implements FastQRecord {

    private final String id;
    private final String comments;
    private final NucleotideSequence nucleotides;
    private final QualitySequence qualities;
    
    public DefaultFastQRecord(String id, NucleotideSequence nucleotides,
            QualitySequence qualities){
        this(id, nucleotides, qualities,null);
    }
    /**
     * @param id
     * @param nucleotides
     * @param qualities
     * @param comments
     */
    public DefaultFastQRecord(String id, NucleotideSequence nucleotides,
            QualitySequence qualities, String comments) {
        this.id = id;
        this.nucleotides = nucleotides;
        this.qualities = qualities;
        this.comments = comments;
    }

    @Override
    public String getComment() {
        return comments;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Delegates to {@link #getNucleotides()}.
     * @see #getNucleotides()
     */
     @Override
     public NucleotideSequence getValue() {
         return getNucleotides();
     }
     
    @Override
    public NucleotideSequence getNucleotides() {
        return nucleotides;
    }

    @Override
    public QualitySequence getQualities() {
        return qualities;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((nucleotides == null) ? 0 : nucleotides.decode().hashCode());
        result = prime * result
                + ((qualities == null) ? 0 : qualities.decode().hashCode());
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
        if (!(obj instanceof FastQRecord)) {
            return false;
        }
        FastQRecord other = (FastQRecord) obj;
        if (comments == null) {
            if (other.getComment() != null) {
                return false;
            }
        } else if (!comments.equals(other.getComment())) {
            return false;
        }
        if (id == null) {
            if (other.getId()!= null) {
                return false;
            }
        } else if (!id.equals(other.getId())) {
            return false;
        }
        if (nucleotides == null) {
            if (other.getNucleotides() != null) {
                return false;
            }
        } else if (!nucleotides.decode().equals(other.getNucleotides().decode())) {
            return false;
        }
        if (qualities == null) {
            if (other.getQualities() != null) {
                return false;
            }
        } else if (!qualities.decode().equals(other.getQualities().decode())) {
            return false;
        }
        return true;
    }
   

}
