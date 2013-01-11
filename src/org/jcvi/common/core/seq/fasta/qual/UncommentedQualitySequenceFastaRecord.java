package org.jcvi.common.core.seq.fasta.qual;

import org.jcvi.jillion.core.qual.QualitySequence;

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
