package org.jcvi.common.core.seq.read.trace.sanger;


import org.jcvi.common.core.seq.fastx.FastXRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.util.ObjectsUtil;


public final class PositionSequenceFastaRecord implements FastXRecord<Position, PositionSequence>{
	
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
        
        ObjectsUtil.nullSafeEquals(getId(), other.getId()) &&
        ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence());
    }   

    protected CharSequence getRecordBody() {
    	int numPositions = (int)positions.getLength();
    	int numberOfLines = numPositions/12 +1;
        StringBuilder result = new StringBuilder(5*(int)positions.getLength()+numberOfLines);
       int i=1;
       for(Position pos : positions){
    	   result.append(String.format("%04d", pos.getValue()));
    	   if(i%12 == 0){
               this.appendCarriageReturnAndLineFeed(result);
           }
           else{
               result.append(' ');
           }   
       }
       //last value doesn't get a space       
       return result.substring(0, result.length()-1);
    }

   
    @Override
    public PositionSequence getSequence() {
        return positions;
    }
    
    

}
