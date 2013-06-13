package org.jcvi.jillion.internal.assembly.util;

import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

public class ConsensusCompactedSlice extends NoConsensusCompactedSlice {

	private byte consensusOrdinal;
	
	public ConsensusCompactedSlice(short[] elements, List<String> ids, Nucleotide consensus) {
		super(elements, ids);
		consensusOrdinal = consensus.getOrdinalAsByte();
	}

	@Override
	public Nucleotide getConsensusCall() {
		return Nucleotide.VALUES.get(consensusOrdinal);
	}
	
	

}
