package org.jcvi.jillion.core.residue.aa;
/**
 * Implementation that knows there are no ambiguous amino acids.
 * @author dkatzel
 *
 * @since 6.0
 */
class UngappedNoAmbiguityProteinSequence extends UngappedProteinSequence{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5225498501113831989L;
	
	public UngappedNoAmbiguityProteinSequence(AminoAcid[] aas) {
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
