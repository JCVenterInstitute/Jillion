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
package org.jcvi.jillion.fasta.qual;

import org.jcvi.jillion.core.qual.QualitySequence;

class UncommentedQualityFastaRecord implements QualityFastaRecord{
	private final String id;
	private final QualitySequence qualities;

    public UncommentedQualityFastaRecord(String id, QualitySequence qualities){
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
		if (!(obj instanceof QualityFastaRecord)) {
			return false;
		}
		QualityFastaRecord other = (QualityFastaRecord) obj;
		if (!id.equals(other.getId())) {
			return false;
		}
		if (!qualities.equals(other.getSequence())) {
			return false;
		}
		
		return true;
	}
    
    
}
