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
package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;

class ProteinPairwiseSequenceAlignmentImpl extends AbstractPairwiseSequenceAlignment<AminoAcid, ProteinSequence> implements ProteinPairwiseSequenceAlignment{

	public ProteinPairwiseSequenceAlignmentImpl(
			PairwiseSequenceAlignment<AminoAcid, ProteinSequence> delegate) {
		super(delegate);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ProteinPairwiseSequenceAlignment){
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		//override hashcode 
		//to make programs like PMD happy that I override
		//equals and hashcode
		return super.hashCode();
	}
	
	
}
