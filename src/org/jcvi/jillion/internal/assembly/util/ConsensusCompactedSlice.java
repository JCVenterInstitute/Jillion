/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
		return Nucleotide.VALUES.get(consensusOrdinal);
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
