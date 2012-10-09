package org.jcvi.common.core.seq.read.trace.sanger;


import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.util.ObjectsUtil;


public final class PositionSequenceFastaRecord implements FastaRecord<Position, PositionSequence>{
	
	private final String identifier;
	private final String comments;
    private final PositionSequence positions;
    
    public PositionSequenceFastaRecord(String id, PositionSequence positions){
        this(id, null, positions);
    }
    public PositionSequenceFastaRecord(String id, String comments, PositionSequence positions){
    	if(id ==null){
    		throw new NullPointerException("id can not be null");
    	}
    	if(positions ==null){
    		throw new NullPointerException("positions can not be null");
    	}
        this.identifier = id;
        this.comments = comments;
        this.positions = positions;
        
    }
    /**
     * @return A <code>String</code>.
     */
    public String getId()
    {
        return this.identifier;
    }

    /**
     * @return A <code>String</code>.
     */
    public String getComment()
    {
        return this.comments;
    }
  
   
    
    @Override
	public String toString() {
		return "PositionSequenceFastaRecord [identifier=" + identifier
				+ ", comments=" + comments + ", positions=" + positions + "]";
	}
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.identifier.hashCode();
        result = prime * result + this.getSequence().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof PositionSequenceFastaRecord)){
            return false;
        }
        PositionSequenceFastaRecord other = (PositionSequenceFastaRecord)obj;
		return 
        
        ObjectsUtil.nullSafeEquals(getId(), other.getId())
         && ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence());
    }   

   

   
    @Override
    public PositionSequence getSequence() {
        return positions;
    }
    
    

}
