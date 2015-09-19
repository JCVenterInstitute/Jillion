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
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

class CommentedFastqRecord extends UncommentedFastqRecord{

	private final String comment;
	public CommentedFastqRecord(String id, NucleotideSequence nucleotides,
			QualitySequence qualities, String comment) {
		super(id, nucleotides, qualities);
		this.comment = comment;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public int hashCode() {
		// superclass already uses getComment()
		//so we can just delegate to super
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		// superclass already uses getComment()
		//so we can just delegate to super
		return super.equals(obj);
	}
	@Override
	public String toString() {
		return "CommentedFastqRecord [getId()=" + getId()
				+ ", getNucleotideSequence()=" + getNucleotideSequence()
				+ ", getQualitySequence()=" + getQualitySequence()
				+ ", comment=" + comment + "]";
	}
	
	
	
	

}
