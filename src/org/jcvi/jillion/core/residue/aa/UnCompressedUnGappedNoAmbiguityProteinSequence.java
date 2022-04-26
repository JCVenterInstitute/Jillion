package org.jcvi.jillion.core.residue.aa;
/**
 * Implementation that knows there are no ambiguous amino acids.
 * @author dkatzel
 *
 * @since 6.0
 */
class UnCompressedUnGappedNoAmbiguityProteinSequence extends UnCompressedUngappedProteinSequence{

	public UnCompressedUnGappedNoAmbiguityProteinSequence(AminoAcid[] array) {
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

}
