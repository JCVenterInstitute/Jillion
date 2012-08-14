package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@link FastqRecord} implementation that doesn't
 * have a comment (so {@link #getComment()} returns null).
 * This saves us 8 bytes per record.
 * @author dkatzel
 *
 */
class UncommentedFastqRecord implements FastqRecord{
	 private static final String CR = "\n";
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
	     * @param comments the comments for this record, may
	     * be set to null to indicate that there are no comments.
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
	                + getComment()==null? 0: getComment().hashCode();
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
	        if (getComment() == null) {
	            if (other.getComment() != null) {
	                return false;
	            }
	        } else if (!getComment().equals(other.getComment())) {
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
