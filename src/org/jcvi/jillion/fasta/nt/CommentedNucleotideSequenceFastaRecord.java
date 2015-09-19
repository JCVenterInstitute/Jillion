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

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

class CommentedNucleotideSequenceFastaRecord extends UnCommentedNucleotideSequenceFastaRecord{

	private final String comment;
	public CommentedNucleotideSequenceFastaRecord(String id,
			NucleotideSequence sequence, String comment) {
		super(id, sequence);
		this.comment = comment;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public int hashCode() {
		//delegating to super since comment doesn't impact
		//equality checks
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		//delegating to super since comment doesn't impact
		//equality checks
		return super.equals(obj);
	}
	
	

}
