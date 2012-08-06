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

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code DefaultFastqRecord} is an implementation 
 * of {@link FastqRecord} that stores all the data of this
 * record into memory.
 * @author dkatzel
 *
 */
public class DefaultFastqRecord implements FastqRecord {

    private static final String CR = "\n";
	private final String id;
    private final String comments;
    private final NucleotideSequence nucleotides;
    private final QualitySequence qualities;
    /**
     * Create a new uncommented {@link DefaultFastqRecord} with the given
     * values ({@link #getComment()} will return {@code null}.
     * @param id the id of this fastq record.  This
     * id may contain whitespace.
     * @param nucleotides the {@link NucleotideSequence}
     * associated with this record.
     * @param qualities the {@link QualitySequence}
     * associated with this record, can not be null.
     * @throw NullPointerException if either id, nucleotides or qualities
     * is set to null.
     */
    public DefaultFastqRecord(String id, NucleotideSequence nucleotides,
            QualitySequence qualities){
        this(id, nucleotides, qualities,null);
    }
    /**
     * Create a new {@link DefaultFastqRecord} with the given
     * values.
     * @param id the id of this fastq record.  This
     * id may contain whitespace.
     * @param nucleotides the {@link NucleotideSequence}
     * associated with this record.
     * @param qualities the {@link QualitySequence}
     * associated with this record, can not be null.
     * @param comments the comments for this record, may
     * be set to null to indicate that there are no comments.
     * @throw NullPointerException if either id, nucleotides or qualities
     * is set to null.
     */
    public DefaultFastqRecord(String id, NucleotideSequence nucleotides,
            QualitySequence qualities, String comments) {
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
        this.comments = comments;
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getComment() {
        return comments;
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
     public NucleotideSequence getSequence() {
         return getNucleotideSequence();
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
        result = prime * result
                + ((comments == null) ? 0 : comments.hashCode());
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
        if (comments == null) {
            if (other.getComment() != null) {
                return false;
            }
        } else if (!comments.equals(other.getComment())) {
            return false;
        }
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
	@Override
	public String toFormattedString() {
		return toFormattedString(FastqQualityCodec.SANGER);
	}
	@Override
	public String toFormattedString(FastqQualityCodec qualityCodec) {
		return toFormattedString(qualityCodec, false);
	}
	@Override
	public String toFormattedString(FastqQualityCodec qualityCodec,
			boolean writeIdOnQualityLine) {
		if(qualityCodec ==null){
			throw new NullPointerException("qualityCodec can not be null");
		}
        boolean hasComment = getComment() !=null;
        
        StringBuilder builder = new StringBuilder("@").append(id);
        if(hasComment){
            builder.append(' ').append(getComment());
        }
        builder.append(CR)
        .append(getNucleotideSequence())
        .append("\n+");
        if(writeIdOnQualityLine){
            builder.append(id);
        }
        builder.append(CR)
        .append(qualityCodec.encode(getQualitySequence()))
        .append(CR);
        return builder.toString();
	}
   

}
