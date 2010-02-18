/*
 * Created on Dec 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import org.jcvi.glyph.nuc.NucleotideGlyph;

public class DefaultConsensusResult implements ConsensusResult {

    private final NucleotideGlyph consensus;
    private final int consensusQuality;
    
    /**
     * @param consensus
     * @param consensusQuality
     */
    public DefaultConsensusResult(NucleotideGlyph consensus,
            int consensusQuality) {
        this.consensus = consensus;
        this.consensusQuality = consensusQuality;
    }

    @Override
    public NucleotideGlyph getConsensus() {
        return consensus;
    }

    @Override
    public int getConsensusQuality() {
        return consensusQuality;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((consensus == null) ? 0 : consensus.hashCode());
        result = prime * result + consensusQuality;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultConsensusResult))
            return false;
        DefaultConsensusResult other = (DefaultConsensusResult) obj;
        if (consensus == null) {
            if (other.consensus != null)
                return false;
        } else if (!consensus.equals(other.consensus))
            return false;
        if (consensusQuality != other.consensusQuality)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s[%d]",consensus, consensusQuality);
    }

}
