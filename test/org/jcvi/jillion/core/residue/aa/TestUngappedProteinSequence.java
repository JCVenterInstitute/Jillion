package org.jcvi.jillion.core.residue.aa;

public class TestUngappedProteinSequence extends AbstractTestProteinSequence{

	@Override
	protected ProteinSequence encode(AminoAcid[] aminoAcids) {
		return new UngappedProteinSequence(aminoAcids);
	}

}
