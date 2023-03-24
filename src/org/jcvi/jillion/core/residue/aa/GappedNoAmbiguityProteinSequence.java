package org.jcvi.jillion.core.residue.aa;

public class GappedNoAmbiguityProteinSequence extends CompactProteinSequence{

	private static final long serialVersionUID = 7286188304603823944L;

	public GappedNoAmbiguityProteinSequence(AminoAcid[] aas) {
		super(aas);
	}

	@Override
	public boolean hasAmbiguities() {
		return false;
	}

	@Override
	public double computePercentX() {
		return 0D;
	}

	@Override
	public long getNumberOfXs() {
		return 0;
	}
	
	
	

}
