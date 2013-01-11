package org.jcvi.jillion.core.residue.aa;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 * {@code UngappedAminoAcidSequence} is a {@link AminoAcidSequence}
 * which contains no gaps.  This allows us to short circuit many 
 * of the gap to ungap computations for improved
 * performance.
 * @author dkatzel
 *
 */
class UngappedAminoAcidSequence extends CompactAminoAcidSequence{

	public UngappedAminoAcidSequence(Collection<AminoAcid> glyphs) {
		super(glyphs);
	}

	public UngappedAminoAcidSequence(String aminoAcids) {
		super(aminoAcids);
	}

	@Override
	public List<Integer> getGapOffsets() {
		return Collections.emptyList();
	}

	@Override
	public int getNumberOfGaps() {
		return 0;
	}

	@Override
	public boolean isGap(int gappedOffset) {
		return false;
	}

	@Override
	public long getUngappedLength() {
		return getLength();
	}

	@Override
	public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
		return 0;
	}


	@Override
	public int getUngappedOffsetFor(int gappedIndex) {
		return gappedIndex;
	}

	@Override
	public int getGappedOffsetFor(int ungappedIndex) {
		return ungappedIndex;
	}

}
