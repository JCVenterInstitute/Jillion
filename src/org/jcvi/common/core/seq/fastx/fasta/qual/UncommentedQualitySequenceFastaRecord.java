package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.util.Iterator;

import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

class UncommentedQualitySequenceFastaRecord implements QualitySequenceFastaRecord{
	private final String id;
	private final QualitySequence qualities;

    public UncommentedQualitySequenceFastaRecord(String id, QualitySequence qualities){
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


	public String toFormattedString()
    {
        final StringBuilder record = new StringBuilder();
        
        record.append(this.getRecordHeader());
        appendCarriageReturnAndLineFeed(record)
        .append(this.getRecordBody());
        appendCarriageReturnAndLineFeed(record);
        
        return record.toString();
    }
    
   
    protected CharSequence getRecordHeader()
    {
        final StringBuilder result = new StringBuilder();
        result.append(FastaUtil.HEADER_PREFIX).append(
                this.getId());
        if (this.getComment() != null) {
            result.append(' ').append(this.getComment());
        }
        return result;
    }
    
    protected StringBuilder appendCarriageReturnAndLineFeed(StringBuilder s){
        return s.append(FastaUtil.LINE_SEPARATOR);
        
    }
    
    /**
     * 
    * Gets the entire formatted fasta record as a String,
    * same as {@link #toFormattedString()}.
    * @see #toFormattedString()
     */
    @Override
    public String toString()
    {
        return this.toFormattedString();
    }
    
    protected CharSequence getRecordBody() {
        int length = (int)qualities.getLength();
		StringBuilder result = new StringBuilder(3*length);
		Iterator<PhredQuality> iter = qualities.iterator();
        int i=1;
        while(iter.hasNext()){
        	result.append(String.format("%02d", iter.next().getQualityScore()));
        	if(iter.hasNext()){
        		if(i%17 == 0){
                    this.appendCarriageReturnAndLineFeed(result);
                }
                else{
                    result.append(' ');
                }   
        	}
        	i++;
        }
        return result.toString();     
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
		if (!(obj instanceof QualitySequenceFastaRecord)) {
			return false;
		}
		QualitySequenceFastaRecord other = (QualitySequenceFastaRecord) obj;
		if (!id.equals(other.getId())) {
			return false;
		}
		if (!qualities.equals(other.getSequence())) {
			return false;
		}
		
		return true;
	}
    
    
}
