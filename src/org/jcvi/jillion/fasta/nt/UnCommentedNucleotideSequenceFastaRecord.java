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
package org.jcvi.jillion.fasta.nt;


import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.fasta.FastaRecord;

/**
 * {@code UnCommentedNucleotideSequenceFastaRecord} is an implementation
 * of {@link NucleotideFastaRecord} that saves
 * memory by not having a reference to a comment.
 * All calls to {@link NucleotideFastaRecord#getComment()} will return null.
 * @author dkatzel
 *
 */
class UnCommentedNucleotideSequenceFastaRecord implements NucleotideFastaRecord{

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

    @Override
    public NucleotideFastaRecord trim(Range trimRange) {
        return new UnCommentedNucleotideSequenceFastaRecord(id, sequence.trim(trimRange));
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
        if (!(obj instanceof NucleotideFastaRecord)){
            return false;
        }
        NucleotideFastaRecord other = (NucleotideFastaRecord)obj;
		return 
        ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence()) 
        && ObjectsUtil.nullSafeEquals(getId(), other.getId());
    }   
   
}
