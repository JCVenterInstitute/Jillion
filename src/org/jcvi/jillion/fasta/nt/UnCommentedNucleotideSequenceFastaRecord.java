package org.jcvi.jillion.fasta.nt;


import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
/**
 * {@code UnCommentedNucleotideSequenceFastaRecord} is an implementation
 * of {@link NucleotideSequenceFastaRecord} that saves
 * memory by not having a reference to a comment.
 * All calls to {@link #getComment()} will return null.
 * @author dkatzel
 *
 */
class UnCommentedNucleotideSequenceFastaRecord implements NucleotideSequenceFastaRecord{

	private final NucleotideSequence sequence;
	private final String id;

    public UnCommentedNucleotideSequenceFastaRecord(String id, NucleotideSequence sequence){
    	if(id == null){
            throw new NullPointerException("identifier can not be null");
        }        
         if(sequence ==null){
         	throw new NullPointerException("sequence can not be null");
         }
         this.id = id;
         this.sequence = sequence;
    }
   
    

    /**
     * @return A <code>String</code>.
     */
    public String getId()
    {
        return this.id;
    }
    /**
     *{@inheritDoc}.
     */
    @Override
    public String getComment()
    {
        return null;
    }
    @Override
    public NucleotideSequence getSequence() 
    {
        return this.sequence;
    }

	
    
    @Override
   	public String toString() {
   		return "NucleotideSequenceFastaRecord [id=" + getId()
   				+ ", comment=" + getComment() + ", sequence ="
   				+ getSequence() + "]";
   	}
       
  
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id.hashCode();
        result = prime * result + this.getSequence().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof NucleotideSequenceFastaRecord)){
            return false;
        }
        NucleotideSequenceFastaRecord other = (NucleotideSequenceFastaRecord)obj;
		return 
        ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence()) 
        && ObjectsUtil.nullSafeEquals(getId(), other.getId());
    }   
   
}
