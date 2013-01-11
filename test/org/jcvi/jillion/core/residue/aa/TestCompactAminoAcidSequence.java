package org.jcvi.jillion.core.residue.aa;

import java.util.List;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.CompactAminoAcidSequence;

public class TestCompactAminoAcidSequence extends AbstractTestAminoAcidSequence{

	@Override
	protected AminoAcidSequence encode(List<AminoAcid> aminoAcids) {
		return new CompactAminoAcidSequence(aminoAcids);
	}


}
