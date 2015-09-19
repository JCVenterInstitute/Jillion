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

class CommentedQualityFastaRecord extends UncommentedQualityFastaRecord{
	private final String comment;
	public CommentedQualityFastaRecord(String id,
			QualitySequence qualities, String comment) {
		super(id, qualities);
		this.comment = comment;
	}
	@Override
	public String getComment() {
		return comment;
	}
	@Override
	public int hashCode() {
		// delegates to uncommented since comments don't count
		return super.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		// delegates to uncommented since comments don't count
		return super.equals(obj);
	}

	
	
}
