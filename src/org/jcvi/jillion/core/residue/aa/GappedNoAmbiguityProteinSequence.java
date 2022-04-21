package org.jcvi.jillion.core.residue.aa;

public class GappedNoAmbiguityProteinSequence extends CompactProteinSequence{

	public GappedNoAmbiguityProteinSequence(AminoAcid[] aas) {
		super(aas);
	}

	@Override
	public boolean hasAmbiguities() {
		return false;
	}

}
