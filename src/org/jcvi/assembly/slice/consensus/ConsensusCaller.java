/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;
/**
 * <code>ConsensusCaller</code> compute the
 * {@link ConsensusResult} for the given Slice.
 * @author dkatzel
 *
 *
 */
public interface ConsensusCaller {
    /**
     * compute the consensus
     * {@link NucleotideGlyph} for the given Slice.
     * @param slice the Slice to compute the consensus for.
     * @return a {@link ConsensusResult} will never be <code>null</code>
     * @throws NullPointerException if slice is null.
     */
    ConsensusResult callConsensus(Slice slice);
}
