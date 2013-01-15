package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
/**
 * {@code AminoAcidPairwiseSequenceAlignment}
 * is a marker interface for a {@link PairwiseSequenceAlignment}
 * for {@link AminoAcid}s.
 * @author dkatzel
 *
 */
public interface AminoAcidPairwiseSequenceAlignment extends PairwiseSequenceAlignment<AminoAcid, AminoAcidSequence>{
}
