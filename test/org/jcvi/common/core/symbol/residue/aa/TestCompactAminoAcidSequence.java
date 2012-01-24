package org.jcvi.common.core.symbol.residue.aa;

import java.util.List;

public class TestCompactAminoAcidSequence extends AbstractTestAminoAcidSequence{

	@Override
	protected AminoAcidSequence encode(List<AminoAcid> aminoAcids) {
		return new CompactAminoAcidSequence(aminoAcids);
	}


}
