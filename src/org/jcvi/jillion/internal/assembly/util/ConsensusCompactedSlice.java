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
package org.jcvi.jillion.internal.assembly.util;

import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

public class ConsensusCompactedSlice extends NoConsensusCompactedSlice {

	private final byte consensusOrdinal;
	
	public ConsensusCompactedSlice(short[] elements, List<String> ids, Nucleotide consensus) {
		super(elements, ids);
		consensusOrdinal = consensus.getOrdinalAsByte();
	}

	@Override
	public Nucleotide getConsensusCall() {
		return Nucleotide.getDnaValues().get(consensusOrdinal);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + consensusOrdinal;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ConsensusCompactedSlice)) {
			return false;
		}
		ConsensusCompactedSlice other = (ConsensusCompactedSlice) obj;
		if (consensusOrdinal != other.consensusOrdinal) {
			return false;
		}
		return true;
	}
	
	
	

}
