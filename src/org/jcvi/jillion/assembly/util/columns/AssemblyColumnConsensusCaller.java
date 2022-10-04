package org.jcvi.jillion.assembly.util.columns;

import org.jcvi.jillion.assembly.util.consensus.ConsensusResult;

/**
 * Computs the
 * {@link ConsensusResult} for the given {@link AssemblyColumn}.
 * This is a higher level abstraction of {@link org.jcvi.jillion.assembly.util.consensus.ConsensusCaller}
 * where we don't care about quality values or the ids of reads.
 * 
 * @param <T> the type of AssemblyColumn
 * @author dkatzel
 *
 * @since 6.0
 * @see org.jcvi.jillion.assembly.util.consensus.ConsensusCaller
 * 
 */
public interface AssemblyColumnConsensusCaller<E extends AssemblyColumnElement, T extends AssemblyColumn<E>> {

	/**
     * Compute the {@link ConsensusResult} for the given Slice.
     * @param column the {@link AssemblyColumn} to compute the consensus for.
     * @return a {@link ConsensusResult} will never be <code>null</code>
     * @throws NullPointerException if column is null.
     */
    ConsensusResult callConsensus(T column);
}
