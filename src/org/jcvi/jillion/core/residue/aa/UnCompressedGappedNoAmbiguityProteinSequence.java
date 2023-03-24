package org.jcvi.jillion.core.residue.aa;
/**
 * Implementation that knows there are no ambiguous amino acids.
 * @author dkatzel
 *
 * @since 6.0
 */
class UnCompressedGappedNoAmbiguityProteinSequence extends UnCompressedGappedProteinSequence{

	public UnCompressedGappedNoAmbiguityProteinSequence(AminoAcid[] array) {
		super(array);
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
