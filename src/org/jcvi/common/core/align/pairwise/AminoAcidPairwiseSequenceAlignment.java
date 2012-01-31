package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
/**
 * {@code AminoAcidPairwiseSequenceAlignment}
 * is a marker interface for a {@link PairwiseSequenceAlignment}
 * for {@link AminoAcid}s.
 * @author dkatzel
 *
 */
public interface AminoAcidPairwiseSequenceAlignment extends PairwiseSequenceAlignment<AminoAcid, AminoAcidSequence>{
}
