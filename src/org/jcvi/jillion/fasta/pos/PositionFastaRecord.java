/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.pos;


import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.fasta.FastaRecord;


public final class PositionFastaRecord implements FastaRecord<Position, PositionSequence>{
	
	private final String identifier;
	private final String comments;
    private final PositionSequence positions;
    
    public PositionFastaRecord(String id, PositionSequence positions){
        this(id, null, positions);
    }
    public PositionFastaRecord(String id, String comments, PositionSequence positions){
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
        if (!(obj instanceof PositionFastaRecord)){
            return false;
        }
        PositionFastaRecord other = (PositionFastaRecord)obj;
		return 
        
        ObjectsUtil.nullSafeEquals(getId(), other.getId())
         && ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence());
    }   

   

   
    @Override
    public PositionSequence getSequence() {
        return positions;
    }
    
    

}
